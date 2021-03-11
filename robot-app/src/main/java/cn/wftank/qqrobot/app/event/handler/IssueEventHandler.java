package cn.wftank.qqrobot.app.event.handler;

import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.qqrobot.common.event.issue.IssueNotifyEvent;
import cn.wftank.qqrobot.common.util.JsonUtil;
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IssueEventHandler implements EventHandler<IssueNotifyEvent> {

    private static final Logger log = LoggerFactory.getLogger(IssueEventHandler.class);

    @Autowired
    private Bot bot;

    @Override
    public void onEvent(IssueNotifyEvent event, long sequence, boolean endOfBatch) {
        log.info("issue event:"+ JsonUtil.toJson(event));
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

    private void processEachGroup(IssueNotifyEvent event, Group group) {
        List<MessageChain> dataChain = new ArrayList<>();
        event.getNewIssues().forEach(issueEntity -> {
            dataChain.add(MessageUtils.newChain()
                    .plus("标题："+issueEntity.getTitle()+"\n")
                    .plus("链接："+issueEntity.getUrl()+"\n")
            );
        });
        if (event.isFirst()){
            group.sendMessage(MessageUtils.newChain()
                    .plus("小助手开始监控玩家提交的bug啦！").plus(new Face(Face.ZHENG_YAN))
                    .plus("，有新消息我会及时发布到群里，可以关注我来获取最新消息哟~")
            );
        }else{
            group.sendMessage(MessageUtils.newChain()
                    .plus("发现有玩家提交了新的bug").plus(new Face(Face.ZHENG_YAN).plus("\n"))
                    .plus(dataChain)
            );
        }
    }
}
