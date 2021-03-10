package cn.wftank.qqrobot.app.event.handler;

import cn.wftank.qqrobot.common.event.spectrum.TiebaNotifyEvent;
import com.lmax.disruptor.EventHandler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TiebaEventHandler implements EventHandler<TiebaNotifyEvent> {

    @Autowired
    private Bot bot;

    @Override
    public void onEvent(TiebaNotifyEvent event, long sequence, boolean endOfBatch) throws Exception {
        Group group = bot.getGroup(1032374245);
        List<MessageChain> dataChain = new ArrayList<>();
        event.getNewThreads().forEach(tiebaThread -> {
            dataChain.add(MessageUtils.newChain()
                    .plus("标题："+ tiebaThread.getTitle())
                    .plus("链接："+ tiebaThread.getUrl())
            );
        });
        if (event.isFirst()){
            group.sendMessage(MessageUtils.newChain()
                    .plus("小助手开始监控"+event.getAuthorName()+"发的帖子啦！").plus(new Face(Face.ZHENG_YAN))
                    .plus("，有新消息我会及时发布到群里，可以把关注我来获取最新消息哟~")
            );
        }else{
            group.sendMessage(MessageUtils.newChain()
                    .plus(event.getAuthorName()+"发布新帖子啦！").plus(new Face(Face.ZHENG_YAN).plus("\n"))
                    .plus(dataChain)
            );
        }
    }

}
