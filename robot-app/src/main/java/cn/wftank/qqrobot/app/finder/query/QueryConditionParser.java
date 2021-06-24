package cn.wftank.qqrobot.app.finder.query;

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
public class QueryConditionParser {

    public static BooleanQuery parseConditionMap(Map<QueryConditionTypeEnum, List<String>> conditionMap){
        BooleanQuery.Builder rootQuery = new BooleanQuery.Builder();
        for (Map.Entry<QueryConditionTypeEnum, List<String>> entry : conditionMap.entrySet()) {
            QueryConditionTypeEnum typeEnum = entry.getKey();
            List<String> valueList = entry.getValue();
            if (CollectionUtils.isNotEmpty(valueList)){
                BooleanQuery.Builder typeQueryBuilder = new BooleanQuery.Builder();
                switch (typeEnum){
                    case NAME:
                        for (String value : valueList) {
                            typeQueryBuilder.add(new TermQuery(new Term("name",value)), BooleanClause.Occur.SHOULD);
                            typeQueryBuilder.add(new TermQuery(new Term("name_cn",value)), BooleanClause.Occur.SHOULD);
                        }
                        break;
                    default:
                        for (String value : valueList) {
                            typeQueryBuilder.add(new TermQuery(new Term(typeEnum.getDefaultFieldName(),value)), BooleanClause.Occur.SHOULD);
                        }
                        break;
                }
                BooleanQuery typeQuery = typeQueryBuilder.build();
                //类型的查询必须是must
                rootQuery.add(typeQuery, BooleanClause.Occur.MUST);
            }


        }
        return rootQuery.build();
    }

}
