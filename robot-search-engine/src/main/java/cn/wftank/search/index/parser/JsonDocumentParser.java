package cn.wftank.search.index.parser;

import cn.wftank.search.index.WFtankDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * 解析json格式的文档
 * 属性名按照对象的层级拼接.,比如{"a1":[{a2:11},{a2:22}]} 属性名a1 a1.a2
 * 以此类推,支持多层级
 */
public class JsonDocumentParser {

    private static final Logger log = LoggerFactory.getLogger(JsonDocumentParser.class);

    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    public static String FIELD_NAME_DELIMITER = ".";

    /**
     * 将给定的json字符串转换为索引文档,不支持批量
     * @param json
     * @return WFtankDocument
     * @throws JsonProcessingException
     */
    public static WFtankDocument parseJsonDocument(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        WFtankDocument wftankDocument = new WFtankDocument();
        //root跟节点必须是对象
        Iterator<Map.Entry<String, JsonNode>> it = rootNode.fields();
        while (it.hasNext()){
            Map.Entry<String, JsonNode> rootFieldEntry = it.next();
            String fieldName = rootFieldEntry.getKey();
            JsonNode valueNode = rootFieldEntry.getValue();
            parseField(fieldName, valueNode, wftankDocument);
        }
        return wftankDocument;
    }

    /**
     * 解析非值json节点
     * @param fieldName 节点名
     * @param valueNode 节点对象
     * @param document 要加入的文档对象
     */
    private static void parseField(String fieldName, JsonNode valueNode, WFtankDocument document) {
        if (valueNode.isObject()){
            parseObject(fieldName, (ObjectNode)valueNode, document);
        }else if (valueNode.isArray()){
            parseArray(fieldName, (ArrayNode)valueNode, document);
        }else if (valueNode.isValueNode()){
            parseAndIndexValue(fieldName,valueNode,document);
        }else {
            throw new IllegalArgumentException("未知的json属性,属性名:"+fieldName);
        }

    }

    /**
     * 解析数组json节点
     * @param fieldName 节点名
     * @param arrayNode 节点对象
     * @param document 要加入的文档对象
     */
    private static void parseArray(String fieldName, ArrayNode arrayNode, WFtankDocument document) {
        Iterator<JsonNode> iterator = arrayNode.iterator();
        while (iterator.hasNext()){
            JsonNode jsonNode = iterator.next();
            parseField(fieldName, jsonNode, document);
        }
    }

    /**
     * 解析对象json节点
     * @param parentFieldName 节点名
     * @param objNode 节点对象
     * @param document 要加入的文档对象
     */
    private static void parseObject(String parentFieldName, ObjectNode objNode, WFtankDocument document) {
        Iterator<Map.Entry<String, JsonNode>> iterator = objNode.fields();
        while (iterator.hasNext()){
            Map.Entry<String, JsonNode> fieldEntry = iterator.next();
            //多层级的json,拼接属性名,不同层的属性名之间用.分隔
            String fieldName = parentFieldName + FIELD_NAME_DELIMITER +fieldEntry.getKey();
            parseField(fieldName, fieldEntry.getValue(), document);
        }

    }


    /**
     * 对值节点的处理
     * @param fieldName
     * @param valueNode
     * @param document
     */
    private static void parseAndIndexValue(String fieldName, JsonNode valueNode, WFtankDocument document) {
        if (valueNode.isNumber()){
            document.addField(fieldName, valueNode.numberValue());
        }else {
            //其他类型全按照字符串处理
            document.addField(fieldName, valueNode.asText());
        }

    }


}
