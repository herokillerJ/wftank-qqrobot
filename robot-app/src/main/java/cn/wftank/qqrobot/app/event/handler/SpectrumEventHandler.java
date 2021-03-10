package cn.wftank.qqrobot.app.event.handler;

import cn.wftank.qqrobot.common.event.spectrum.SpectrumNotifyEvent;
import com.lmax.disruptor.EventHandler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpectrumEventHandler implements EventHandler<SpectrumNotifyEvent> {

    private static final Logger log = LoggerFactory.getLogger(SpectrumEventHandler.class);

    @Autowired
    private Bot bot;

    @Override
    public void onEvent(SpectrumNotifyEvent event, long sequence, boolean endOfBatch) throws Exception {

        switch (event.getType()){
            case ANNOUNCEMENTS:
                announcementNotify(event, bot);
                break;
            case PATCH_NOTES:
                patchNoteNotify(event, bot);
                break;
        }


    }

    private void patchNoteNotify(SpectrumNotifyEvent event, Bot bot) {
        Group group = bot.getGroup(1032374245);
        List<MessageChain> dataChain = new ArrayList<>();
        event.getNewThreads().forEach(spectrumThread -> {
            String version = spectrumThread.getSubject()
                    .replaceAll("Star Citizen ","")
                    .replaceAll(" Patch Notes","");
            dataChain.add(MessageUtils.newChain()
                    .plus("新版本："+version+"发布啦！").plus(new Face(Face.ZHENG_YAN)).plus("\n")
                    .plus("点击下面链接查看更新了什么吧~\n")
                    .plus(spectrumThread.getUrl())
            );
        });
        if (event.isFirst()){
            group.sendMessage(MessageUtils.newChain()
                    .plus("小助手开始监控游戏版本更新啦！").plus(new Face(Face.ZHENG_YAN))
                    .plus("，有新消息我会及时发布到群里，可以关注我来获取最新消息哟~")
            );
        }else{
            group.sendMessage(MessageUtils.newChain()
                    .plus(dataChain)
            );
        }
    }

    private void announcementNotify(SpectrumNotifyEvent event, Bot bot) {
        Group group = bot.getGroup(1032374245);
        List<MessageChain> dataChain = new ArrayList<>();
        event.getNewThreads().forEach(spectrumThread -> {
            dataChain.add(MessageUtils.newChain()
                    .plus("标题："+spectrumThread.getSubject()+"\n")
                    .plus("链接："+spectrumThread.getUrl()+"\n")
            );
        });
        if (event.isFirst()){
            group.sendMessage(MessageUtils.newChain()
                    .plus("小助手开始监控官方发布的公告啦！").plus(new Face(Face.ZHENG_YAN))
                    .plus("，有新消息我会及时发布到群里，可以关注我来获取最新消息哟~")
            );
        }else{
            group.sendMessage(MessageUtils.newChain()
                    .plus("官方在光谱发布的公告啦！").plus(new Face(Face.ZHENG_YAN).plus("\n"))
                    .plus(dataChain)
            );
        }
    }
}
