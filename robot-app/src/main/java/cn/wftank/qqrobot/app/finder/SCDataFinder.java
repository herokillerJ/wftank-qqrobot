package cn.wftank.qqrobot.app.finder;

import cn.wftank.qqrobot.app.model.vo.JsonProductVO;
import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.qqrobot.common.util.JsonUtil;
import cn.wftank.qqrobot.common.util.OKHttpUtil;
import cn.wftank.qqrobot.common.util.StringUtils;
import cn.wftank.search.WFtankSearcher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import info.debatty.java.stringsimilarity.MetricLCS;
import info.debatty.java.stringsimilarity.interfaces.StringDistance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    //字符串匹配算法
    private StringDistance similarMatcher = new MetricLCS();

    @Autowired
    private WFtankSearcher wFtankSearcher;
    {
        patternMap.put(Pattern.compile("(.+)(在|去|到)+.*(哪|那)+.*(买|卖|租)+.*"),1);
    }

    public static final String DEFAULT_VERSION = "latest";
    public static final String URL_PREFIX = "https://cdn.jsdelivr.net/gh/herokillerJ/starcitizen-data@";
    private final LoadingCache<String, JsonProductVO> productCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .build(path -> loadProductInfo(path));

    /**
     * 一小时刷新一次
     */
    @Scheduled(fixedDelay = 1000*60*60, initialDelay=1000*60*60)
    private void load(){
        log.info("开始重载查询索引");
        long start = System.currentTimeMillis();
        String indexUrl = URL_PREFIX+ GlobalConfig.getConfig(ConfigKeyEnum.SC_DB_VERSION) +"/index.json";
        String extIndexUrl = URL_PREFIX+GlobalConfig.getConfig(ConfigKeyEnum.SC_DB_VERSION)+"/ext_index.json";
        Index index = OKHttpUtil.get(indexUrl, new TypeReference<Index>() {});
        List<IndexEntity> mainList = index.getIndex();
        Index extIndex = OKHttpUtil.get(extIndexUrl, new TypeReference<Index>() {});
        mainList.addAll(extIndex.getIndex());
        List<String> indesJsonList = mainList.stream().map(JsonUtil::toJson).collect(Collectors.toList());
        wFtankSearcher.reloadIndexFromString(indesJsonList);
        long end = System.currentTimeMillis();
        log.info("重载索引完成，耗时：{}秒",(end-start)/1000);
    }

    public List<String> search(String keyword){
        return searchByEngine(keyword);
    }

    public List<String> searchByEngine(String keyword){
        List result = new LinkedList();
        List<String> keywords = wFtankSearcher.analizeString(keyword);
        if (CollectionUtils.isNotEmpty(keywords)){
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            keywords.forEach(str -> {
                builder
                        .add(new TermQuery(new Term("name", str)), BooleanClause.Occur.SHOULD)
                        .add(new TermQuery(new Term("name_cn", str)),BooleanClause.Occur.SHOULD);
            });
            BooleanQuery query = builder.build();
            List<Document> docList = wFtankSearcher.search(query, 50);
            if (CollectionUtils.isNotEmpty(docList)){
                result.addAll(docList.stream().map(doc -> doc.getField("path").stringValue()).collect(Collectors.toList()));
            }
        }
        log.info("搜索:{} 结果为:{}",keyword,result);
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
    public List<String> autoFind(String content) {
        String keywordStr = getKeywordByPattern(content);
        if (keywordStr == null) return null;
        return searchByEngine(keywordStr);
    }



    private MatchIndexEntity match(String keywordStr, IndexEntity indexEntity) {
        MatchIndexEntity matchIndexEntity = new MatchIndexEntity();
        //替换掉所有符号并转小写
        String sourceStr = StringUtils.replaceAllMarks(keywordStr).toLowerCase();
        //商品中文名替换掉所有符号转小写
        String cnName = StringUtils.replaceAllMarks(indexEntity.getNameCn()).toLowerCase();
        //商品英文名替换掉所有符号转小写
        String enName = StringUtils.replaceAllMarks(indexEntity.getName()).toLowerCase();
        /**
         * 分数越小证明源字符串"转换"位商品名的"步数"越低，中文和英文哪个低取哪个
         */
        double cnScore = similarMatcher.distance(sourceStr, cnName);
        double enScore = similarMatcher.distance(sourceStr, enName);
        matchIndexEntity.setMatchScore(Math.min(cnScore,enScore));
        BeanUtils.copyProperties(indexEntity, matchIndexEntity);
        return matchIndexEntity;
    }

    private String getKeywordByPattern(String content){
        //通过正则提取关键词
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
