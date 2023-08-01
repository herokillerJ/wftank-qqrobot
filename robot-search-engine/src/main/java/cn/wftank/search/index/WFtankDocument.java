package cn.wftank.search.index;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 抽象出来一个索引对象,lucene中一行索引对应一个document,
 * 这里对lucence的各种field类型的添加做一个封装,方便调用
 */
public class WFtankDocument {

    private final Document document = new Document();

    /**
     * 字符串是否需要被整个索引,不分词
     */
    private final int singleTokenMaxLength;

    /**
     * 文档的id,全局唯一,如果id相同会更新文档而不是新增
     */
    @Getter
    @Setter
    private String documentId;

    private static final int DEFAULT_SINGLE_TOKEN_STRING_LENGTH = 128;

    public WFtankDocument(int singleTokenMaxLength) {
        this.singleTokenMaxLength = singleTokenMaxLength;
    }

    public WFtankDocument() {
        this.singleTokenMaxLength = DEFAULT_SINGLE_TOKEN_STRING_LENGTH;
    }

    public void addField(String fieldName, String fieldValue){
        addField(fieldName,fieldValue,true,true);
    }

    public void addField(String fieldName,Number numberFieldValue){
        addField(fieldName, numberFieldValue, true, true,true);
    }

    public void addField(String fieldName, String fieldValue, boolean isSorted, boolean isStored){
        if (null == fieldValue || "null".equalsIgnoreCase(fieldValue)) return;
        if (isSorted){
            addSortedField(fieldName,fieldValue);
        }
        Field.Store store = isStored ? Field.Store.YES : Field.Store.NO;

        //索引长度限制内的整个字符串
        if (fieldValue.length() <= singleTokenMaxLength
                &&(fieldName.equalsIgnoreCase("id") || StringUtils.endsWithIgnoreCase(fieldName,"_id"))){
            StringField stringField = new StringField(fieldName, fieldValue, store);
            document.add(stringField);
        }else{
            TextField textField = new TextField(fieldName, fieldValue, store);
            document.add(textField);
        }
    }

    public void addField(String fieldName,Number numberFieldValue, boolean isStored, boolean isSorted, boolean isRanged){
        if (null == numberFieldValue) return;
        if (isSorted){
            addSortedField(fieldName,numberFieldValue);
        }
        if (isRanged){
            addRangedField(fieldName, numberFieldValue);
        }
        //queryParser语法中
        Field.Store store = isStored ? Field.Store.YES : Field.Store.NO;
        document.add(new StringField(fieldName,numberFieldValue.toString(),store));
    }

    /**
     * 对字段进行排序
     * @param fieldName
     * @param numberFieldValue
     */
    private void addSortedField(String fieldName,Number numberFieldValue){
        long sortNUmber;
        if (numberFieldValue instanceof Float){
            sortNUmber = NumericUtils.floatToSortableInt((Float) numberFieldValue);
        }else if (numberFieldValue instanceof Double){
            sortNUmber = NumericUtils.doubleToSortableLong((Double) numberFieldValue);
        }else if (numberFieldValue instanceof BigDecimal){
            sortNUmber = handleBigDecimal((BigDecimal)numberFieldValue);
        }else{
            sortNUmber = numberFieldValue.longValue();
        }
        SortedNumericDocValuesField sortField = new SortedNumericDocValuesField(fieldName, sortNUmber);
        document.add(sortField);
    }

    private void addSortedField(String fieldName,String fieldValue){
        document.add(new SortedDocValuesField(fieldName,new BytesRef(fieldValue)));
    }

    /**
     * 让字段支持范围和精确查询
     * @param fieldName
     * @param numberFieldValue
     */
    private void addRangedField(String fieldName,Number numberFieldValue){
        Field rangedField;
        if (numberFieldValue instanceof Float){
            rangedField = new FloatPoint(fieldName, (float)numberFieldValue);
        }else if (numberFieldValue instanceof Double){
            rangedField = new DoublePoint(fieldName, (double)numberFieldValue);
        }else if (numberFieldValue instanceof BigDecimal){
            long longValue = handleBigDecimal((BigDecimal) numberFieldValue);
            rangedField = new LongPoint(fieldName, longValue);
        }else {
            rangedField = new LongPoint(fieldName, numberFieldValue.longValue());
        }
        document.add(rangedField);
    }

    private long handleBigDecimal(BigDecimal numberFieldValue) {
        long longValue = numberFieldValue.longValue();
        if (new BigDecimal(longValue).compareTo(numberFieldValue) == 0){
            //是正数
            return longValue;
        }else{
            //是小数
            return NumericUtils.doubleToSortableLong(numberFieldValue.doubleValue());
        }
    }

    /**
     * 有则修改,无则添加
     * @param indexWriter
     * @throws IOException
     */
    public void updateDocument(IndexWriter indexWriter) throws IOException {
        if (null != documentId){
            indexWriter.updateDocument(new Term("document_id",documentId),this.document);
        }else {
            indexWriter.addDocument(this.document);
        }
    }


}
