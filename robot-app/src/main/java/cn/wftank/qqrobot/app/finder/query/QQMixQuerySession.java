package cn.wftank.qqrobot.app.finder.query;

import cn.wftank.qqrobot.common.util.StringUtils;
import cn.wftank.search.WFtankSearcher;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.lucene.document.Document;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author: wftank
 * @create: 2021-06-23 16:07
 * @description: 复合查询会话,满足在QQ群中进行复杂
 **/
@Getter
@Setter
public class QQMixQuerySession {

    private long qq;

    private Map<QueryConditionTypeEnum, List<String>> conditionMap = new LinkedHashMap<>();

    public static final String STOP_FLAG = "-1";

    private WFtankSearcher wftankSearcher;

    /**
     * 用于撤回之前没用的消息
     */
    private AtomicReference<MessageReceipt> lastMessageToRecall = new AtomicReference<>();

    public QQMixQuerySession(long qq, WFtankSearcher wftankSearcher) {
        this.qq = qq;
        this.wftankSearcher = wftankSearcher;
    }

    /**
     * 当前用户操作,对于查询来说,只有选择查询条件和输入值两个操作
     * 1: 选择查询条件
     * 2: 输入值
     * 第一次肯定是选择查询条件,不会是输入值
     * 通过该状态判断当前用户要进行的操作
     */
    private volatile int curQueryStatus = CHOOSE_TYPE;

    private static final int CHOOSE_TYPE = 1;
    private static final int INPUT_VALUE = 2;

    private volatile QueryConditionTypeEnum curTypeEnum;

    /**
     * 切换用户操作,每次用户选择完查询条件或者输入值后都需要调用此方法
     *
     */
    private void switchQueryStatus(){
        synchronized (this){
            curQueryStatus = (curQueryStatus == CHOOSE_TYPE ? INPUT_VALUE : CHOOSE_TYPE);
        }
    }

    /**
     * 添加查询条件，这里会根据用户当前会话的操作状态来判断用户是在选择查询条件类型还是输入值
     * @param event
     */
    public MessageChain addQueryCondition(GroupMessageEvent event, String content){
        MessageChain messageChain = null;
        //每个qq号对应一个session，应该保证线程安全
        synchronized (this){
            switch (curQueryStatus){
                case CHOOSE_TYPE:
                    //输入类型
                    messageChain = chooseQueryConditionType(event, content);
                    break;
                case INPUT_VALUE:
                    messageChain = inputQueryValue(event, content);
                    break;
                default:
                    break;
            }
        }
        return messageChain;
    }

    /**
     * 结束查询返回结果
     * 这里懒了,应该转换成IndexEntity在外层转换消息的,后面再改
     */
    public List<String> doQuery(){
        List result = new LinkedList<>();
        //组装查询条件
        //最多显示10条,太多了QQ群就刷屏了
        int num = 1;
        List<Document> resultDoc = wftankSearcher.search(QueryConditionParser.parseConditionMap(conditionMap,wftankSearcher), 10);
        if (CollectionUtils.isNotEmpty(resultDoc)){
            for (Document doc : resultDoc) {
                result.add(""+num + "：" +doc.get("name_cn")+"["+doc.get("name")+"]");
                num++;
            }
        }
        return result;
    }

    /**
     * 输入值
     * @param event
     * @param content
     */
    private MessageChain inputQueryValue(GroupMessageEvent event, String content) {
        MessageChainBuilder chainBuilder = new MessageChainBuilder();
        content = StringUtils.trim(content);
        if (validValueInput(content, chainBuilder)){
            if (QueryConditionTypeEnum.TYPE.equals(curTypeEnum)) {
                Integer typeCode = Integer.valueOf(content);
                conditionMap.get(curTypeEnum).add(ProductTypeEnum.getByCode(typeCode).getName());
            }else {
                conditionMap.get(curTypeEnum).add(content);
            }
            //正确选择时需要切换操作类型
            switchQueryStatus();
            StringBuilder sb = new StringBuilder("您已添加查询条件：");
            if (QueryConditionTypeEnum.TYPE.equals(curTypeEnum)){
                sb.append(curTypeEnum.getName()+"--"+ProductTypeEnum.getByCode(Integer.valueOf(content)).getName());
            }else{
                sb.append(curTypeEnum.getName()+"："+content);
            }
            if (MapUtils.isNotEmpty(conditionMap)){
                sb.append("\n目前的查询条件如下：");
                conditionMap.forEach((type,valueList) -> {
                    if (CollectionUtils.isNotEmpty(valueList)){
                        if (QueryConditionTypeEnum.TYPE.equals(type)){
                            //如果是商品类型，将数字转换成类型名称
                            String typeValueStr = valueList.stream()
                                    .map(typeName -> ProductTypeEnum.getByName(typeName).getNameCn())
                                    .collect(Collectors.joining("，"));
                            sb.append("\n"+ type.getName()+"："+typeValueStr);
                        }else{
                            sb.append("\n"+ type.getName()+"："+valueList.stream().collect(Collectors.joining("，")));
                        }
                    }

                });
            }
            sb.append("\n"+"请继续添加以下条件，输入编号即可：");
            QueryConditionTypeEnum[] values = QueryConditionTypeEnum.values();
            for (int i = 0; i < values.length; i++) {
                QueryConditionTypeEnum conditionType = values[i];
                sb.append("\n"+conditionType.getIndex()+"："+conditionType.getName());
            }
            sb.append("\n或输入"+STOP_FLAG+"结束查询");
            chainBuilder.add(new PlainText(sb));
        }
        return chainBuilder.build();
    }

    private boolean validValueInput(String content,MessageChainBuilder chainBuilder) {
        if (StringUtils.isBlank(content)){
            chainBuilder.add(new PlainText("请不要输入空值，请重新发送，如需结束查询请发送\""+STOP_FLAG+"\""));
            return false;
        }
        if (QueryConditionTypeEnum.TYPE.equals(curTypeEnum)){
            try {
                Integer typeCode = Integer.valueOf(content);
                ProductTypeEnum productTypeEnum = ProductTypeEnum.getByCode(typeCode);
                if (null == productTypeEnum){
                    throw new RuntimeException();
                }
            }catch (Exception e){
                chainBuilder.add(new PlainText("输入类型不合法，请重新发送，如需结束查询请发送\""+STOP_FLAG+"\""));
                return false;
            }
        }
        return true;
    }

    /**
     * 添加查询类型
     * @param event mirai的消息事件,方便以后用到
     * @param content 输入的消息内容
     */
    private MessageChain chooseQueryConditionType(MessageEvent event,String content) {
        MessageChainBuilder chainBuilder = new MessageChainBuilder();
        //校验是否合法
        content = StringUtils.trim(content);
        if (validTypeInput(content,chainBuilder)){
            Integer type = Integer.valueOf(content);
            QueryConditionTypeEnum conditionTypeEnum = QueryConditionTypeEnum.get(type);
            curTypeEnum = conditionTypeEnum;
            conditionMap.putIfAbsent(conditionTypeEnum, new LinkedList<>());
            chainBuilder.add(new PlainText("您已选择根据 ["+ conditionTypeEnum.getName() +"]条件搜索，"));
            if (QueryConditionTypeEnum.GRADE.equals(conditionTypeEnum)) {
                chainBuilder.add(new PlainText("请输入1~4之间的整数值，1代表最好，4代表最差。"));
            }else if(QueryConditionTypeEnum.TYPE.equals(conditionTypeEnum)) {
                String typeMsg = "请输入以下类型对应的编号：\n";
                ProductTypeEnum[] productTypeEnums = ProductTypeEnum.values();
                for (int i = 0; i < productTypeEnums.length; i++) {
                    ProductTypeEnum productTypeEnum = productTypeEnums[i];
                    typeMsg += "\t"+productTypeEnum.getIndex()+"："+productTypeEnum.getNameCn();
                }
                chainBuilder.add(new PlainText(typeMsg));
            }
            chainBuilder.add(new PlainText("\n直接发送想要搜索的内容即可，不要回复本条消息。如果弄不明白了,发送"+STOP_FLAG+"结束本次查询"));
            //正确选择时需要切换操作类型
            switchQueryStatus();
        }
        return chainBuilder.build();
    }

    private boolean validTypeInput(String content, MessageChainBuilder chainBuilder) {
        if (StringUtils.isBlank(content)){
            chainBuilder.add(new PlainText("请不要输入空值，请重新发送，如需结束查询请发送\""+STOP_FLAG+"\""));
            return false;
        }
        Integer typeCode = null;
        try {
            typeCode = Integer.valueOf(content);
        }catch (Exception e){
            chainBuilder.add(new PlainText("请输入合法的数字，请重新发送，如需结束查询请发送\""+STOP_FLAG+"\""));
            return false;
        }
        QueryConditionTypeEnum conditionTypeEnum = QueryConditionTypeEnum.get(typeCode);
        if (null == conditionTypeEnum){
            String message = String.format("请输入%d-%d之间的数字，请重新发送，如需结束查询请发送\""+STOP_FLAG+"\"",
                    QueryConditionTypeEnum.getMinIndex(), QueryConditionTypeEnum.getMaxIndex());
            chainBuilder.add(new PlainText(message));
            return false;
        }
        return true;
    }


}
