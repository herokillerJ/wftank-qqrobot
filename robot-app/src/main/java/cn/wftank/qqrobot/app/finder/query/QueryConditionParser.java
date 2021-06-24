package cn.wftank.qqrobot.app.finder.query;

import cn.wftank.search.WFtankSearcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import java.util.List;
import java.util.Map;

/**
 * @author: wftank
 * @create: 2021-06-24 15:22
 * @description: 将querysession中的查询条件转换成lucene查询他条件
 **/
@Slf4j
public class QueryConditionParser {

    public static BooleanQuery parseConditionMap(Map<QueryConditionTypeEnum, List<String>> conditionMap, WFtankSearcher searcher){
        BooleanQuery.Builder rootQuery = new BooleanQuery.Builder();
        for (Map.Entry<QueryConditionTypeEnum, List<String>> entry : conditionMap.entrySet()) {
            QueryConditionTypeEnum typeEnum = entry.getKey();
            List<String> valueList = entry.getValue();
            if (CollectionUtils.isNotEmpty(valueList)){
                BooleanQuery.Builder typeQueryBuilder = new BooleanQuery.Builder();
                switch (typeEnum){
                    case NAME:
                        for (String value : valueList) {
                            //分词
                            List<String> keywords = searcher.analizeString(value);
                            if (CollectionUtils.isNotEmpty(keywords)){
                                for (String keyword : keywords) {
                                    typeQueryBuilder.add(new TermQuery(new Term("name",keyword)), BooleanClause.Occur.SHOULD);
                                    typeQueryBuilder.add(new TermQuery(new Term("name_cn",keyword)), BooleanClause.Occur.SHOULD);
                                }

                            }
                        }
                        break;
                    default:
                        for (String value : valueList) {
                            typeQueryBuilder.add(new TermQuery(new Term(typeEnum.getDefaultFieldName(),value)), BooleanClause.Occur.SHOULD);
                            if (typeEnum.equals(QueryConditionTypeEnum.TYPE)){
                                log.info(""+searcher.analizeString(value));
                            }
                        }
                        if (typeEnum.equals(QueryConditionTypeEnum.TYPE)){
                            log.info(typeEnum.getDefaultFieldName()+":"+valueList);
                        }
                        break;
                }
                BooleanQuery typeQuery = typeQueryBuilder.build();
                rootQuery.add(typeQuery, BooleanClause.Occur.MUST);
            }
        }
        return rootQuery.build();
    }

}
