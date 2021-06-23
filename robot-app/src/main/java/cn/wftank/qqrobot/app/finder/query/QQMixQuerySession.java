package cn.wftank.qqrobot.app.finder.query;

import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: wftank
 * @create: 2021-06-23 16:07
 * @description: 复合查询会话,满足在QQ群中进行复杂
 **/
@Getter
@Setter
public class QQMixQuerySession {

    private long qq;

    private Map<QueryConditionTypeEnum, List<String>> conditionMap = new ConcurrentHashMap<>();

    /**
     * 当前用户操作,对于查询来说,只有选择查询条件和输入值两个操作
     * 1: 选择查询条件
     * 2: 输入值
     * 第一次肯定是选择查询条件,不会是输入值
     * 通过该状态判断当前用户要进行的操作
     */
    private volatile int curQueryStatus = 1;
    private volatile QueryConditionTypeEnum curTypeEnum;

    /**
     * 切换用户操作,每次用户选择完查询条件或者输入值后都需要调用此方法
     *
     */
    private void switchQueryStatus(){
        synchronized (this){
            curQueryStatus = curQueryStatus == 1 ? 0 : 1;
        }
    }

    /**
     * 添加查询条件，这里会根据用户当前会话的操作状态来判断用户是在选择查询条件类型还是输入值
     * @param event
     */
    public void addQueryCondition(GroupMessageEvent event, String content){
        //每个qq号对应一个session，应该保证线程安全
        synchronized (this){
            switch (curQueryStatus){
                case 1:
                    chooseQueryConditionType(event, content);
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 添加查询类型
     * @param event
     */
    private MessageChain chooseQueryConditionType(MessageEvent event,String content) {
        MessageChainBuilder chainBuilder = new MessageChainBuilder();
        if (!validTypeInput(content)){
        }else{
            Integer type = Integer.valueOf(content);
            QueryConditionTypeEnum conditionTypeEnum = QueryConditionTypeEnum.get(type);
            curTypeEnum = conditionTypeEnum;
            conditionMap.putIfAbsent(conditionTypeEnum, new LinkedList<>());
            chainBuilder.append(new PlainText("您已选择根据 ["+ conditionTypeEnum.getName() +"]条" +
                    "件搜索，请直接发送该条件对应的值，不要回复本条消息。"));
        }

        return chainBuilder.build();
    }


}
