package cn.wftank.qqrobot.app.event.handler;

import cn.wftank.qqrobot.app.handler.CommonCommandHandler;
import cn.wftank.qqrobot.app.mirai.QQGroupMessageSender;
import cn.wftank.qqrobot.common.event.command.CommandNotifyEvent;
import cn.wftank.qqrobot.common.event.spectrum.SpectrumNotifyEvent;
import cn.wftank.qqrobot.common.translate.BaiduTranslator;
import cn.wftank.qqrobot.common.util.JsonUtil;
import cn.wftank.qqrobot.common.util.StringUtils;
import com.lmax.disruptor.EventHandler;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommandEventHandler implements EventHandler<CommandNotifyEvent> {

    private static final Logger log = LoggerFactory.getLogger(CommandEventHandler.class);

    @Autowired
    private QQGroupMessageSender qqGroupSender;

    @Autowired
    @Lazy
    private CommonCommandHandler commonCommandHandler;

    @Override
    public void onEvent(CommandNotifyEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("command event:"+ JsonUtil.toJson(event));
        proccess(event);
    }

    private void proccess(CommandNotifyEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        switch (event.getType()){
            case CREATE:
                commonCommandHandler.addCommand(event.getCommand(), event.getCommandPath());
                builder.add(String.format("小助手添加了新的指令\"%s\"，快来试试吧",event.getCommand()));
                break;
            case DELETE:
                commonCommandHandler.removeCommand(event.getCommand());
                builder.add(String.format("小助手删除了指令\"%s\"，不要在发送该指令啦",event.getCommand()));
                break;
            case MODIFY:
                commonCommandHandler.modifyCommand(event.getCommand(),event.getCommandPath());
                builder.add(String.format("小助手修改了指令\"%s\"，快来试试有什么新变化吧!",event.getCommand()));
                break;
        }
        qqGroupSender.sendMessageForAllGroups(builder.build());
    }

}
