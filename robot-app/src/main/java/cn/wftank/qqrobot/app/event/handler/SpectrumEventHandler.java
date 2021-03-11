package cn.wftank.qqrobot.app.event.handler;

import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.qqrobot.common.event.spectrum.SpectrumNotifyEvent;
import cn.wftank.qqrobot.common.translate.BaiduTranslator;
import cn.wftank.qqrobot.common.util.JsonUtil;
import com.lmax.disruptor.EventHandler;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.AtAll;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SpectrumEventHandler implements EventHandler<SpectrumNotifyEvent> {

    private static final Logger log = LoggerFactory.getLogger(SpectrumEventHandler.class);

    @Autowired
    private Bot bot;
    @Autowired
    private BaiduTranslator baiduTranslator;

    @Override
    public void onEvent(SpectrumNotifyEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("spectrum event:"+ JsonUtil.toJson(event));
        if (!bot.isOnline()){
            log.warn("bot is offline,will login again");
            bot.login();
        }
        String groupsStr = GlobalConfig.getConfig(ConfigKeyEnum.GROUPS);
        Set<Long> collect = Arrays.stream(groupsStr.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        collect.forEach(groupId -> {
            proccessEachGroup(event, bot.getGroup(groupId));
            //每个群延时发送防止被当做机器人
            try {
                Thread.sleep(Duration.ofSeconds(5).toMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void proccessEachGroup(SpectrumNotifyEvent event, Group group) {
        switch (event.getType()){
            case ANNOUNCEMENTS:
                announcementNotify(event, group);
                break;
            case PATCH_NOTES:
                patchNoteNotify(event, group);
                break;
        }
    }

    private void patchNoteNotify(SpectrumNotifyEvent event, Group group) {
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
                    .plus(AtAll.INSTANCE)
                    .plus(dataChain)
            );
        }
    }

    private void announcementNotify(SpectrumNotifyEvent event, Group group) {
        List<MessageChain> dataChain = new ArrayList<>();
        event.getNewThreads().forEach(spectrumThread -> {
            dataChain.add(MessageUtils.newChain()
                    .plus("标题："+spectrumThread.getSubject())
                    .plus("("+baiduTranslator.translate(spectrumThread.getSubject(),"en","zh")+")\n")
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
                    .plus(AtAll.INSTANCE)
                    .plus("官方在光谱发布的公告啦！").plus(new Face(Face.ZHENG_YAN).plus("\n"))
                    .plus(dataChain)
            );
        }
    }
}
