package cn.wftank.qqrobot.app.finder.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author: wftank
 * @create: 2021-06-23 17:41
 * @description: 查询类型
 **/
@Getter
@AllArgsConstructor
public enum QueryConditionTypeEnum {

    NAME(1,"name_cn","名称","发送中文英文均可"),
    SIZE(2,"size","尺寸","发送数字即可"),
    GRADE(3,"grade","等级","发送数字即可，数字越小，商品品质越好，最小值为1，最大值为4"),
    TYPE(4,"show_type_key","商品类型",""),
    ;

    //索引值,用于输入
    private Integer index;
    //对应lucene中的字段名
    private String defaultFieldName;
    //展示的名称
    private String name;
    //描述
    private String des;

    private static LinkedList<Integer> avalibleCodeList = new LinkedList<>();

    static {
        avalibleCodeList.add(NAME.getIndex());
        avalibleCodeList.add(SIZE.getIndex());
        avalibleCodeList.add(GRADE.getIndex());
        avalibleCodeList.add(TYPE.getIndex());
    }

    private static final Map<Integer, QueryConditionTypeEnum> LOOKUP = new LinkedHashMap<>();

    static {
        for (QueryConditionTypeEnum e : QueryConditionTypeEnum.values()) {
            LOOKUP.put(e.index, e);
        }
    }

    public static QueryConditionTypeEnum get(Integer code) {
        return LOOKUP.get(code);
    }

    public static LinkedList getAvalibleCode(){
        return new LinkedList(avalibleCodeList);
    }

    public static Integer getMaxIndex(){
        return avalibleCodeList.getLast();
    }

    public static Integer getMinIndex(){
        return avalibleCodeList.getFirst();
    }


}
