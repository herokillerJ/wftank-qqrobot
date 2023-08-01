package cn.wftank.qqrobot.app.event.handler;

import cn.wftank.qqrobot.app.mirai.QQGroupMessageSender;
import cn.wftank.qqrobot.common.event.tieba.TiebaNotifyEvent;
import cn.wftank.qqrobot.common.util.JsonUtil;
import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TiebaEventHandler implements EventHandler<TiebaNotifyEvent> {

    @Autowired
    private QQGroupMessageSender qqGroupSender;

    @Override
    public void onEvent(TiebaNotifyEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("tieba event:"+ JsonUtil.toJson(event));
        process(event);
    }

    private void process(TiebaNotifyEvent event) {
        List<MessageChain> dataChain = new ArrayList<>();
        event.getNewThreads().forEach(tiebaThread -> {
            dataChain.add(MessageUtils.newChain()
                    .plus("标题："+ tiebaThread.getTitle())
                    .plus("链接："+ tiebaThread.getUrl())
            );
        });
        MessageChain chain;
        if (event.isFirst()){
            chain = MessageUtils.newChain()
                    .plus("小助手开始监控"+ event.getAuthorName()+"发的帖子啦！").plus(new Face(Face.ZHENG_YAN))
                    .plus("，有新消息我会及时发布到群里，可以关注我来获取最新消息哟~");
        }else{
            chain = MessageUtils.newChain()
                    .plus(event.getAuthorName()+"发布新帖子啦！").plus(new Face(Face.ZHENG_YAN).plus("\n"))
                    .plus(dataChain);
        }
        qqGroupSender.sendMessageForAllGroups(chain);
    }
}
