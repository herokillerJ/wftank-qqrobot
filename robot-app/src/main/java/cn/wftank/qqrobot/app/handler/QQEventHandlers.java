package cn.wftank.qqrobot.app.handler;

import cn.wftank.qqrobot.app.finder.SCDataFinder;
import cn.wftank.qqrobot.app.finder.query.QQMixQueryManager;
import cn.wftank.qqrobot.app.finder.query.QQMixQuerySession;
import cn.wftank.qqrobot.app.finder.query.QueryConditionTypeEnum;
import cn.wftank.qqrobot.app.model.vo.JsonProductVO;
import cn.wftank.qqrobot.app.model.vo.ProductRentShopVO;
import cn.wftank.qqrobot.app.model.vo.ProductShopVO;
import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.search.WFtankSearcher;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QQEventHandlers extends SimpleListenerHost {
    private static final Logger log = LoggerFactory.getLogger(QQEventHandlers.class);
    @Autowired
    private SCDataFinder scDataFinder;
    @Autowired
    private QQMixQueryManager qqMixQueryManager;
    @Autowired
    private WFtankSearcher wftankSearcher;

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception){
        // 处理事件处理时抛出的异常
    }

    @EventHandler
    public void onMessage(@NotNull GroupMessageEvent event) throws Exception { // 可以抛出任何异常, 将在 handleException 处理
        String groups = GlobalConfig.getConfig(ConfigKeyEnum.GROUPS);
        //检查一下群组配置是否为空
        if (StringUtils.isBlank(groups))return;
        groups = groups.trim();
        //在这里过滤配置的群组而不是在初始化配置中,以达到动态的目的
        Set<Long> groupSet = Arrays.stream(groups.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        //过滤掉没有配置的群组
        if (! groupSet.contains(event.getGroup().getId())) return;
        //处理接收消息的逻辑
        MessageChain messageChain = event.getMessage();
        boolean isAtBot = false;
        StringBuilder plainTextBuilder = new StringBuilder("");
        //判断是不是在@小助手
        for (int i = 0; i < messageChain.size(); i++) {
            SingleMessage singleMessage = messageChain.get(i);
            //判断是不是在@小助手
            if (singleMessage instanceof At){
                At at = (At) singleMessage;
                if (at.getTarget() == event.getBot().getId()){
                    isAtBot = true;
                }
            }
            if (singleMessage instanceof PlainText){
                plainTextBuilder.append(singleMessage.contentToString());
            }
        }
        String content = plainTextBuilder.toString().trim();
        if (isAtBot){
            processCommand(event, content);
        }else{
            //如果当前用户存在高级查询会话则走查询,否则走自动搜索
            long qq = event.getSender().getId();
            if (null != qqMixQueryManager.get(qq)){
                processMixQuery(event, content);
            }else{
                //通过正则确定是否在问商品信息
                processAutoFind(event, content);
            }

        }
    }

    private void processMixQuery(GroupMessageEvent event, String content) {
        long qq = event.getSender().getId();
        if (StringUtils.isNotBlank(content)){
            content = StringUtils.trim(content);
            QQMixQuerySession qqMixQuerySession = qqMixQueryManager.get(qq);
            QuoteReply quote = new QuoteReply(event.getSource());
            if (QQMixQuerySession.STOP_FLAG.equals(content)){
                List<String> result = null;
                try {
                    result = qqMixQuerySession.doQuery();
                }finally {
                    qqMixQueryManager.remove(qq);
                }
                if (CollectionUtils.isEmpty(result)){
                    event.getGroup().sendMessage(quote.plus("未查询到任何商品"));
                }else{
                    StringBuilder sb = new StringBuilder("小助手已为您查询到以下商品：\n");
                    for (String eachMsg :  result) {
                        sb.append("\n"+eachMsg);
                    }
                    event.getGroup().sendMessage(quote.plus(sb));
                }

            }else{
                MessageChain messageChain = qqMixQuerySession.addQueryCondition(event, content);
                if (null != messageChain){
                    event.getGroup().sendMessage(quote.plus(messageChain));
                }
            }
        }
    }

    private void processAutoFind(GroupMessageEvent event, String content) {
        List<String> pathList = scDataFinder.autoFind(content);
        if (CollectionUtils.isEmpty(pathList)) return;
        MessageChain message = MessageUtils.newChain();
        message = message.plus("小助手感觉您似乎在询问游戏内的商品购买位置\n");
        final QuoteReply quote = new QuoteReply(event.getSource());
        if (pathList.size() == 1){
            message = message.plus("已为您匹配到了一条商品信息")
                    .plus(new Face(Face.DE_YI)).plus("，详情如下：\n");
            JsonProductVO productVO = scDataFinder.getProductInfo(pathList.get(0));
            message = convertProduct(message, productVO);
        }else{
            //找到的数量大于1条
            message = message.plus("已为您匹配到"+pathList.size()+"条商品信息")
                    .plus(new Face(Face.WU_YAN_XIAO).plus("\n")).plus("，名字最相近的商品详情如下：\n");
            JsonProductVO productVO = scDataFinder.getProductInfo(pathList.get(0));
            message = convertProduct(message, productVO);
            message = message.plus("\n如果不正确，还请去https://wftank.cn/search查询");
        }
        event.getGroup().sendMessage(quote
                .plus(message));
    }

    private MessageChain convertProduct(MessageChain message, JsonProductVO productVO) {
        if (productVO.getCanBuy()) {
            message = message.plus("商品名称：")
                    .plus(productVO.getNameCn())
                    .plus("[" + productVO.getName() + "]\n");
            message = message.plus("购买地点：\n");
            List<ProductShopVO> shopBuy = productVO.getShopBuy();
            Iterator<ProductShopVO> iterator = shopBuy.iterator();
            while (iterator.hasNext()) {
                ProductShopVO next = iterator.next();
                if (next.getLayoutName().toLowerCase().contains("cryastro")) continue;
                message = message.plus(next.getLayoutNameCn() + "\t价格：" + new BigDecimal(next.getCurrentPrice()).toPlainString() + " auec\n");
            }
        }
        if (productVO.getCanRent()) {
            message = message.plus("租赁地点：\n");
            List<ProductRentShopVO> shopRent = productVO.getShopRent();
            Iterator<ProductRentShopVO> iterator = shopRent.iterator();
            while (iterator.hasNext()) {
                ProductRentShopVO next = iterator.next();
                if (next.getLayoutName().toLowerCase().contains("cryastro")) continue;
                message = message.plus(next.getLayoutNameCn() + " auec\n")
                        .plus("租一天：" + next.getRentPrice1() + " auec\n")
                        .plus("租三天：" + next.getRentPrice3() + " auec\n")
                        .plus("租七天：" + next.getRentPrice7() + " auec\n")
                        .plus("租三十天：" + next.getRentPrice30() + " auec\n");
            }
        }
        return message;
    }

    private void processCommand(@NotNull GroupMessageEvent event, String content) {
        content = StringUtils.trim(content);
        if (StringUtils.isNotBlank(content)){
            int firstSpaceIndex = content.indexOf(" ");
            String commandKey;
            if (firstSpaceIndex < 0){
                commandKey = content;
            }else{
                commandKey = content.substring(0,firstSpaceIndex);
            }
            log.info("收到指令:"+commandKey);
            if ("高级查询".equalsIgnoreCase(commandKey)){
                processCreateMixQuery(event);
            }
            if ("帮助".equalsIgnoreCase(commandKey)){
                final QuoteReply quote = new QuoteReply(event.getSource());
                event.getGroup().sendMessage(quote
                        .plus("普通查询：不用@小助手，直接发：\"xxx在哪买\"即可搜索商品,例：\n")
                        .plus("水星在哪买\n"));
            }
        }else{
            final QuoteReply quote = new QuoteReply(event.getSource());
            event.getGroup().sendMessage(quote
                    .plus("请@小助手并输入\"帮助\"来查看小助手的使用方式"));
        }
    }

    private void processCreateMixQuery(GroupMessageEvent event) {
        long qq = event.getSender().getId();
        qqMixQueryManager.put(qq,new QQMixQuerySession(qq, wftankSearcher));
        QuoteReply quoteReply = new QuoteReply(event.getSource());
        String createMixMsg = "您以创建高级查询，请选择如下查询条件，发送他们的编号即可，无需回复本条消息";
        QueryConditionTypeEnum[] values = QueryConditionTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            QueryConditionTypeEnum conditionType = values[i];
            createMixMsg += "\n"+conditionType.getIndex()+"："+conditionType.getName();
        }
        event.getGroup().sendMessage(quoteReply.plus(createMixMsg));
    }

//    @NotNull
//    @EventHandler
//    public ListeningStatus onMessage(@NotNull MessageEvent event) throws Exception { // 可以抛出任何异常, 将在 handleException 处理
//        event.getSubject().sendMessage("received");
//        return ListeningStatus.LISTENING; // 表示继续监听事件
//        // return ListeningStatus.STOPPED; // 表示停止监听事件
//    }


}