package cn.wftank.qqrobot.app.finder.query;

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
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        for (Map.Entry<QueryConditionTypeEnum, List<String>> entry : conditionMap.entrySet()) {
            QueryConditionTypeEnum typeEnum = entry.getKey();
            List<String> valueList = entry.getValue();
            switch (typeEnum){
                case NAME:
                    for (String value : valueList) {
                        builder.add(new TermQuery(new Term("name",value)), BooleanClause.Occur.SHOULD);
                        builder.add(new TermQuery(new Term("name_cn",value)), BooleanClause.Occur.SHOULD);
                    }
                    break;
                default:
                    for (String value : valueList) {
                        builder.add(new TermQuery(new Term(typeEnum.getDefaultFieldName(),value)), BooleanClause.Occur.SHOULD);
                    }
                    break;
            }

        }
        return builder.build();
    }

}
