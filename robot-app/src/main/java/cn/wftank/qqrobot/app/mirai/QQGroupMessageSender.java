package cn.wftank.qqrobot.app.mirai;

import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.data.MessageChain;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QQGroupMessageSender {

    @Autowired
    private Bot bot;

    private static final Logger log = LoggerFactory.getLogger(QQGroupMessageSender.class);

    public void sendMessageForAllGroups(MessageChain messageChain){
        if (!bot.isOnline()){
            log.warn("bot is offline,will login again");
            bot.login();
        }
        //获取配置的QQ群
        String groupsStr = GlobalConfig.getConfig(ConfigKeyEnum.GROUPS);
        //使用有序set
        Set<Long> collect = Arrays.stream(groupsStr.split(",")).map(Long::valueOf).collect(Collectors.toCollection(LinkedHashSet::new));
        collect.forEach(groupId -> {
            //每个群延时发送防止被当做机器人
            try {
                bot.getGroup(groupId).sendMessage(messageChain);
                Thread.sleep(Duration.ofSeconds(5).toMillis());
            } catch (Exception e) {
                log.error("send message to group:{} exception:"+ExceptionUtils.getStackTrace(e));
            }
        });
    }

}
