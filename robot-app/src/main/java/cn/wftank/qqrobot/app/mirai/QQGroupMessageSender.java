package cn.wftank.qqrobot.app.mirai;

import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QQGroupMessageSender {

    @Autowired
    private Bot bot;

    private static final Logger log = LoggerFactory.getLogger(QQGroupMessageSender.class);

    public void sendMessageForAllGroups(MessageChain messageChain, List<File> imageList){
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
                MessageChainBuilder messageBuilder = new MessageChainBuilder();
                Group group = bot.getGroup(groupId);
                messageBuilder.add(messageChain);
                if (CollectionUtils.isNotEmpty(imageList)){
                    imageList.parallelStream().forEach(image -> messageBuilder.add(group.uploadImage(ExternalResource.create(image))));
                }
                group.sendMessage(messageBuilder.build());
                Thread.sleep(Duration.ofSeconds(5).toMillis());
            } catch (Exception e) {
                log.error("send message to group:{} exception:{}",groupId,ExceptionUtils.getStackTrace(e));
            }
        });
    }

    public void sendMessageForAllGroups(MessageChain messageChain){
        sendMessageForAllGroups(messageChain, null);
    }

}
