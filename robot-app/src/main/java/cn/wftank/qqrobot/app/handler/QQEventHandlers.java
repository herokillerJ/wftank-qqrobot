package cn.wftank.qqrobot.app.handler;

import cn.wftank.qqrobot.app.finder.IndexEntity;
import cn.wftank.qqrobot.app.finder.MatchIndexEntity;
import cn.wftank.qqrobot.app.finder.SCDataFinder;
import cn.wftank.qqrobot.app.model.vo.JsonProductVO;
import cn.wftank.qqrobot.app.model.vo.ProductRentShopVO;
import cn.wftank.qqrobot.app.model.vo.ProductShopVO;
import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
            //通过正则确定是否在问商品信息
            processAutoFind(event, content);
        }
    }

    private void processAutoFind(GroupMessageEvent event, String content) {
        List<MatchIndexEntity> matchIndexList = scDataFinder.autoFind(content);
        if (CollectionUtils.isEmpty(matchIndexList)) return;
        MessageChain message = MessageUtils.newChain();
        message = message.plus("小助手感觉您似乎在询问游戏内的商品购买位置\n");
        final QuoteReply quote = new QuoteReply(event.getSource());
        if (matchIndexList.size() == 1){
            message = message.plus("已为您匹配到了一条商品信息")
                    .plus(new Face(Face.DE_YI)).plus("，详情如下：\n");
            JsonProductVO productVO = scDataFinder.getProductInfo(matchIndexList.get(0).getPath());
            message = convertProduct(message, productVO);
        }else{
            //找到的数量大于1条
            message = message.plus("已为您匹配到"+matchIndexList.size()+"条商品信息")
                    .plus(new Face(Face.WU_YAN_XIAO).plus("\n")).plus("，名字最相近的商品详情如下：\n");
            JsonProductVO productVO = scDataFinder.getProductInfo(matchIndexList.get(0).getPath());
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
                message = message.plus(next.getLayoutNameCn() + "\t" + new BigDecimal(next.getCurrentPrice()).toPlainString() + "\n");
            }
        }
        if (productVO.getCanRent()) {
            message = message.plus("租赁地点：\n");
            List<ProductRentShopVO> shopRent = productVO.getShopRent();
            Iterator<ProductRentShopVO> iterator = shopRent.iterator();
            while (iterator.hasNext()) {
                ProductRentShopVO next = iterator.next();
                if (next.getLayoutName().toLowerCase().contains("cryastro")) continue;
                message = message.plus(next.getLayoutNameCn() + "\n")
                        .plus("租一天：" + next.getRentPrice1() + "\n")
                        .plus("租三天：" + next.getRentPrice3() + "\n")
                        .plus("租七天：" + next.getRentPrice7() + "\n")
                        .plus("租三十天：" + next.getRentPrice30() + "\n");
            }
        }
        return message;
    }

    private void processCommand(@NotNull GroupMessageEvent event, String content) {
        boolean isCommand = false;
        if (content.startsWith("-")){
            isCommand = true;
        }
        if (isCommand){
            int firstSpaceIndex = content.indexOf(" ");
            String commandKey;
            String commandContent;
            if (firstSpaceIndex < 0){
                commandKey = content.substring(1);
                commandContent = "";
            }else{
                commandKey = content.substring(1,firstSpaceIndex);
                commandContent = content.substring(firstSpaceIndex+1);
            }
            if ("s".equalsIgnoreCase(commandKey)){
                searchCommandProccess(event,commandContent);
            }else if ("help".equalsIgnoreCase(commandKey)){
                final QuoteReply quote = new QuoteReply(event.getSource());
                event.getGroup().sendMessage(quote
                        .plus("小助手仅支持以下指令哦~\n")
                        .plus("-s 查找物品 模糊匹配: -s 小矿 精确匹配: /s {小矿}\n"));
            }else{
                final QuoteReply quote = new QuoteReply(event.getSource());
                event.getGroup().sendMessage(quote
                        .plus("请使用-help查看小助手支持的指令"));
            }
        }else{
            final QuoteReply quote = new QuoteReply(event.getSource());
            event.getGroup().sendMessage(quote
                    .plus("请使用-help查看小助手支持的指令"));
        }
    }

    /**
     * 搜索指令处理
     * @param event
     * @param commandContent
     */
    private void searchCommandProccess(GroupMessageEvent event,String commandContent) {
        int limit = 5;
        List<IndexEntity> search = scDataFinder.search(commandContent);
        //引用回复
        final QuoteReply quote = new QuoteReply(event.getSource());
        MessageChain message = MessageUtils.newChain();
        if (search.size() < 1){
            message = message.plus("抱歉,小助手没找到你描述的商品")
                    .plus(new Face(Face.YUN));
        }else if(search.size() == 1){
            JsonProductVO productVO = scDataFinder.getProductInfo(search.get(0).getPath());
            if (productVO.getCommodity()){
                message = message.plus("机器人暂不支持查询贸易品,请去下面提示的网站查询：\n");
            }else{
                //确定了唯一商品
                message = message.plus("找到商品了，详情如下：")
                        .plus(new Face(Face.DE_YI).plus("\n"));
                message = convertProduct(message, productVO);
            }

        }else if (search.size() > limit){
            //商品数量多于5条
            message = message.plus("麻烦您说的精确一点,我找到了"+search.size()+"件商品,只能显示前"+limit+"条哦，只有查找到一条信息的时候我才会显示详情。")
                    .plus("或者用花括号包裹关键字采用精确匹配,例如{"+commandContent+"}\n")
                    .plus(new Face(Face.QIU_DA_LE).plus("\n"));
            for (int i = 0; i < limit; i++) {
                IndexEntity indexEntity = search.get(i);
                message = message.plus(""+(i+1)+"：")
                        .plus(indexEntity.getNameCn())
                        .plus("["+indexEntity.getName()+"]\n");
            }
        }else{
            //商品数量在2-5条之间
            message = message.plus("小助手已为你找到以下"+search.size()+"件商品,请复制其中一个的完整名称给我：\n")
                    .plus("或者用花括号包裹关键字采用精确匹配,例如{"+commandContent+"}\n");
            for (int i = 0; i < search.size(); i++) {
                IndexEntity indexEntity = search.get(i);
                message = message.plus(""+(i+1)+"："+indexEntity.getNameCn()+"["+indexEntity.getName()+"]\n");
            }

        }
        event.getGroup().sendMessage(quote.plus(message.plus("\n https://wftank.cn/search可获取更多信息")));
    }

//    @NotNull
//    @EventHandler
//    public ListeningStatus onMessage(@NotNull MessageEvent event) throws Exception { // 可以抛出任何异常, 将在 handleException 处理
//        event.getSubject().sendMessage("received");
//        return ListeningStatus.LISTENING; // 表示继续监听事件
//        // return ListeningStatus.STOPPED; // 表示停止监听事件
//    }


}