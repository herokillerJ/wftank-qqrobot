package cn.wftank.qqrobot.app.finder;

import cn.wftank.qqrobot.app.model.vo.JsonProductVO;
import cn.wftank.qqrobot.common.util.OKHttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SCDataFinder {

    private List<IndexEntity> indexList = new CopyOnWriteArrayList<>();

    private Map<Pattern,Integer> patternMap = new LinkedHashMap<>();
    {
        patternMap.put(Pattern.compile("(.+)(在|去|到|.*)(哪|那).*(买|卖|租|).*"),1);
        load();
    }

    private static final String DEFAULT_VERSION = "latest";
    private static final String URL_PREFIX = "https://cdn.jsdelivr.net/gh/herokillerJ/starcitizen-data@";
    private final LoadingCache<String, JsonProductVO> productCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .build(path -> loadProductInfo(path));

    @Scheduled(fixedDelay = 1000*60*60)
    private void load(){
        String indexUrl = URL_PREFIX+getVersion()+"/index.json";
        String extIndexUrl = URL_PREFIX+getVersion()+"/ext_index.json";
        Index index = OKHttpUtil.get(indexUrl, new TypeReference<Index>() {});
        List<IndexEntity> mainList = index.getIndex();
        Index extIndex = OKHttpUtil.get(extIndexUrl, new TypeReference<Index>() {});
        mainList.addAll(extIndex.getIndex());
        mainList = mainList.parallelStream().map(indexEntity -> {
            indexEntity.setName(indexEntity.getName().replaceAll("\\s+", " "));
            indexEntity.setNameCn(indexEntity.getNameCn().replaceAll("\\s+", " "));
            return indexEntity;
        }).collect(Collectors.toList());
        indexList.clear();
        indexList.addAll(mainList);
    }

    public List<IndexEntity> search(String keyword){
        List<IndexEntity> result;
        boolean exact = false;
        if (keyword.startsWith("{") && keyword.endsWith("}")){
            exact = true;
            keyword = keyword.substring(1,keyword.length()-1);
        }
        boolean finalExact = exact;
        String finalKeyword = keyword;
        result = indexList.parallelStream().filter(indexEntity -> {
            if (finalExact){
                //精确匹配
                return indexEntity.getName().equalsIgnoreCase(finalKeyword.toLowerCase())
                        || indexEntity.getNameCn().equals(finalKeyword);
            }else{
                //模糊匹配
                return indexEntity.getName().toLowerCase().contains(finalKeyword.toLowerCase())
                        || indexEntity.getNameCn().contains(finalKeyword);
            }
        }).collect(Collectors.toList());
        return result;
    }

    public String getVersion(){
        try {
            File file = new File("./sc_database_version");
            if (!file.exists()){
                Files.createFile(file.toPath());
            }
            String version = Files.readString(file.toPath());
            if (StringUtils.isNotBlank(version)){
                return version;
            }
        }catch (IOException e){
            log.error("get sc_database_version ex"+ ExceptionUtils.getStackTrace(e));
        }
        return DEFAULT_VERSION;
    }

    public JsonProductVO getProductInfo(String path){
        return productCache.get(path);
    }

    public JsonProductVO loadProductInfo(String path){
        String url = URL_PREFIX+getVersion()+"/"+path;
        JsonProductVO productVO = OKHttpUtil.get(url, new TypeReference<JsonProductVO>() {
        });
        return productVO;
    }

    /**
     * //通过正则确定是否在问商品信息
     * @param content
     */
    public List<MatchIndexEntity> autoFind(String content) {
        String keywordStr = getKeywordByPattern(content);
        if (keywordStr == null) return null;
        List<MatchIndexEntity> matchList = indexList.parallelStream().map(indexEntity -> {
            /**
             * 将物品名称用空格拆分,拆分后的每个单词去句子中匹配,根据匹配的单词数量排序
             * 先匹配中文,如果中文没匹配上,去匹配英文
             */
            MatchIndexEntity matchIndexEntity = matchCn(keywordStr, indexEntity);
            if (matchIndexEntity.getMatchCount().get() < 1 && matchIndexEntity.getMatchKeyLength().get() < 2) {
                matchIndexEntity = match(keywordStr, indexEntity);
            }
            return matchIndexEntity;
        }).filter(matchIndexEntity -> matchIndexEntity.getMatchCount().get() > 0 || matchIndexEntity.getMatchKeyLength().get() > 1)
                .sorted((match1, match2) -> {
                    //匹配的关键字长度越长的在前面
                    int wordNum = match2.getMatchKeyLength().get() - match1.getMatchKeyLength().get();

                    if (wordNum != 0) {
                        return wordNum;
                    } else {
                        //长度相等,匹配次数多的排在前面
                        int number = match2.getMatchCount().get() - match1.getMatchCount().get();
                        if (number != 0){
                            return number;
                        }else{
                            //前面两个都相等,那就名字短的在前面
                            return match1.getNameCn().length() - match2.getNameCn().length();
                        }
                    }
                }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(matchList)){
            int maxMatchCount = matchList.get(0).getMatchCount().get();
            matchList = matchList.stream()
                    .filter(entiy -> entiy.getMatchCount().get() == maxMatchCount)
                    .collect(Collectors.toList());
            return matchList;
        }
        return new LinkedList<>();
    }

    @NotNull
    private MatchIndexEntity matchCn(String keywordStr, IndexEntity indexEntity) {
        //比indexEntity
        MatchIndexEntity matchIndexEntity = new MatchIndexEntity();
        //去除符号
        keywordStr = keywordStr.replaceAll("[\\pP‘’“”]","");
        //将商品根据空格切开(比如:先锋 哨兵)
        String[] nameCnKeywords = indexEntity.getNameCn()
                //去掉所有除空格外的符号
                .replaceAll("\\p{Punct}|\\d","")
                .split(" ");
        //匹配次数
        for (int i = 0; i < nameCnKeywords.length; i++) {
            String nameKeyword = nameCnKeywords[i];
            //根据横杠再切开比如cf-117
            if (nameKeyword.indexOf("_")> -1){
                String[] nameKeywordPart = nameKeyword.split("-");
                for (int j = 0; j < nameKeywordPart.length; j++) {
                    if (keywordStr.toLowerCase().contains(nameKeywordPart[j].toLowerCase())) {
                        matchIndexEntity.plusMatchCount();
                        matchIndexEntity.plusMatchKeyLength(nameKeywordPart[j].length());
                    };
                }
            }else{
                if (keywordStr.toLowerCase().contains(nameKeyword.toLowerCase())) {
                    matchIndexEntity.plusMatchCount();
                    matchIndexEntity.plusMatchKeyLength(nameKeyword.length());
                };
            }

        }
        if (matchIndexEntity.getMatchCount().get() == 0){
            //匹配相同的最长子串,先匹配中文,如果中文不匹配就匹配英文
            int sameStrLength = longestCommonSubstring(keywordStr,indexEntity.getNameCn()).length();
            matchIndexEntity.plusMatchKeyLength(sameStrLength);
        }
        BeanUtils.copyProperties(indexEntity, matchIndexEntity);
        return matchIndexEntity;
    }


    @NotNull
    private MatchIndexEntity match(String keywordStr, IndexEntity indexEntity) {
        //比indexEntity
        MatchIndexEntity matchIndexEntity = new MatchIndexEntity();
        //去除符号
        keywordStr = keywordStr.replaceAll("[\\pP‘’“”]","");
        //将商品根据空格切开(比如:先锋 哨兵)
        String[] nameKeywords = indexEntity.getName()
                //去掉所有除空格外的符号
                .replaceAll("\\p{Punct}|\\d","")
                .split(" ");
        //匹配次数
        for (int i = 0; i < nameKeywords.length; i++) {
            String nameKeyword = nameKeywords[i];
            //根据横杠再切开比如cf-117
            if (nameKeyword.indexOf("_")> -1){
                String[] nameKeywordPart = nameKeyword.split("-");
                for (int j = 0; j < nameKeywordPart.length; j++) {
                    if (keywordStr.toLowerCase().contains(nameKeywordPart[j].toLowerCase())) {
                        matchIndexEntity.plusMatchCount();
                        matchIndexEntity.plusMatchKeyLength(nameKeywordPart[j].length());
                    };
                }
            }else{
                if (keywordStr.toLowerCase().contains(nameKeyword.toLowerCase())) {
                    matchIndexEntity.plusMatchCount();
                    matchIndexEntity.plusMatchKeyLength(nameKeyword.length());
                };
            }

        }
        if (matchIndexEntity.getMatchCount().get() == 0){
            //匹配相同的最长子串,先匹配中文,如果中文不匹配就匹配英文
            int sameStrLength = longestCommonSubstring(keywordStr,indexEntity.getName()).length();
            matchIndexEntity.plusMatchKeyLength(sameStrLength);
        }
        BeanUtils.copyProperties(indexEntity, matchIndexEntity);
        return matchIndexEntity;
    }

    private String getKeywordByPattern(String content){
        Iterator<Map.Entry<Pattern, Integer>> iterator = patternMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Pattern, Integer> entry = iterator.next();
            Matcher matcher = entry.getKey().matcher(content);
            while (matcher.find()){
                return matcher.group(entry.getValue());
            }
        }
        return null;
    }
    //找出最长相同的字符串
    public static String longestCommonSubstring(String S1, String S2)
    {
        int Start = 0;
        int Max = 0;
        for (int i = 0; i < S1.length(); i++)
        {
            for (int j = 0; j < S2.length(); j++)
            {
                int x = 0;
                while (Character.toLowerCase(S1.charAt(i + x)) == Character.toLowerCase(S2.charAt(j + x)))
                {
                    x++;
                    if (((i + x) >= S1.length()) || ((j + x) >= S2.length())) break;
                }
                if (x > Max)
                {
                    Max = x;
                    Start = i;
                }
            }
        }
        return S1.substring(Start, (Start + Max));
    }

}
