package cn.wftank.qqrobot.app.event.handler;

import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.qqrobot.common.event.tieba.TiebaNotifyEvent;
import cn.wftank.qqrobot.common.util.JsonUtil;
import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TiebaEventHandler implements EventHandler<TiebaNotifyEvent> {

    @Autowired
    private Bot bot;

    @Override
    public void onEvent(TiebaNotifyEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("tieba event:"+ JsonUtil.toJson(event));
        if (!bot.isOnline()){
            log.warn("bot is offline,will login again");
            bot.login();
        }

        String groupsStr = GlobalConfig.getConfig(ConfigKeyEnum.GROUPS);
        Set<Long> collect = Arrays.stream(groupsStr.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        collect.forEach(groupId -> {
            processEachGroup(event, bot.getGroup(groupId));
            //每个群延时发送防止被当做机器人
            try {
                Thread.sleep(Duration.ofSeconds(5).toMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void processEachGroup(TiebaNotifyEvent event, Group group) {
        List<MessageChain> dataChain = new ArrayList<>();
        event.getNewThreads().forEach(tiebaThread -> {
            dataChain.add(MessageUtils.newChain()
                    .plus("标题："+ tiebaThread.getTitle())
                    .plus("链接："+ tiebaThread.getUrl())
            );
        });
        if (event.isFirst()){
            group.sendMessage(MessageUtils.newChain()
                    .plus("小助手开始监控"+ event.getAuthorName()+"发的帖子啦！").plus(new Face(Face.ZHENG_YAN))
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
