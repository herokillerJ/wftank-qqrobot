package cn.wftank.qqrobot.app.finder;

import cn.wftank.qqrobot.app.model.vo.JsonProductVO;
import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.qqrobot.common.util.OKHttpUtil;
import cn.wftank.qqrobot.common.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import info.debatty.java.stringsimilarity.MetricLCS;
import info.debatty.java.stringsimilarity.interfaces.StringDistance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

    private StringDistance similarMatcher = new MetricLCS();

    {
        patternMap.put(Pattern.compile("(.+)(在|去|到)*.*(哪|那)+.*(买|卖|租)+.*"),1);
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
        String indexUrl = URL_PREFIX+ GlobalConfig.getConfig(ConfigKeyEnum.SC_DB_VERSION) +"/index.json";
        String extIndexUrl = URL_PREFIX+GlobalConfig.getConfig(ConfigKeyEnum.SC_DB_VERSION)+"/ext_index.json";
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
                return StringUtils.longestCommonSubstring(StringUtils.replaceAllMarks(indexEntity.getName()).toLowerCase(),StringUtils.replaceAllMarks(finalKeyword).toLowerCase()).length() > 0
                        || StringUtils.longestCommonSubstring(StringUtils.replaceAllMarks(indexEntity.getNameCn()).toLowerCase(),StringUtils.replaceAllMarks(finalKeyword).toLowerCase()).length() > 0;
            }
        }).sorted((i1,i2) -> {
            MatchIndexEntity match1 = match(finalKeyword, i1);
            MatchIndexEntity match2 = match(finalKeyword, i2);
            return sort(match1, match2);
        }).collect(Collectors.toList());
        return result;
    }

    private int sort(MatchIndexEntity match1, MatchIndexEntity match2) {
        double value = match1.getMatchScore() - match2.getMatchScore();
        if (value < 0d) return -1;
        if (value > 0d) return 1;
        return match2.getNameCn().length() - match1.getNameCn().length();
    }

    public JsonProductVO getProductInfo(String path){
        return productCache.get(path);
    }

    public JsonProductVO loadProductInfo(String path){
        String url = URL_PREFIX+GlobalConfig.getConfig(ConfigKeyEnum.SC_DB_VERSION)+"/"+path;
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
        List<MatchIndexEntity> matchList = indexList.parallelStream()
                .filter(matchIndexEntity ->
                StringUtils.longestCommonSubstring(keywordStr,matchIndexEntity.getNameCn()).length() > 0
                        || StringUtils.longestCommonSubstring(keywordStr,matchIndexEntity.getName()).length() > 0)
                .map(indexEntity -> {
                    /**
                     * 将物品名称用空格拆分,拆分后的每个单词去句子中匹配,根据匹配的单词数量排序
                     * 先匹配中文,如果中文没匹配上,去匹配英文
                     */
                    MatchIndexEntity matchIndexEntity = match(keywordStr, indexEntity);
                    return matchIndexEntity;
                }).sorted((match1, match2) -> {
                            //分数小的在前面(分数越低,目标字符串转换到原字符串步骤越少)
                            return sort(match1, match2);
                }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(matchList)){
            //做多取前5个
            if (matchList.size() > 5){
                return matchList.subList(0,4);
            }
            return matchList;
        }
        return new LinkedList<>();
    }



    private MatchIndexEntity match(String keywordStr, IndexEntity indexEntity) {
        //比indexEntity
        MatchIndexEntity matchIndexEntity = new MatchIndexEntity();
        String sourceStr = StringUtils.replaceAllMarks(keywordStr).toLowerCase();
        String cnName = StringUtils.replaceAllMarks(indexEntity.getNameCn()).toLowerCase();
        String enName = StringUtils.replaceAllMarks(indexEntity.getName()).toLowerCase();
        double cnScore = similarMatcher.distance(sourceStr, cnName);
        double enScore = similarMatcher.distance(sourceStr, enName);
        matchIndexEntity.setMatchScore(Math.min(cnScore,enScore));
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


}
