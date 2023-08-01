package cn.wftank.qqrobot.app.mirai;

import cn.wftank.qqrobot.app.config.QQbotFactory;
import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.qqrobot.common.util.FileUtil;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.file.AbsoluteFile;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class QQGroupMessageSender {

    @Autowired
    private QQbotFactory qQbotFactory;

    private static final Logger log = LoggerFactory.getLogger(QQGroupMessageSender.class);

    public void sendMessageForAllGroups(MessageChain messageChain, List<File> imageList){
        if (messageChain.isEmpty()){
            return;
        }
        //获取配置的QQ群
        String groupsStr = GlobalConfig.getConfig(ConfigKeyEnum.GROUPS);
        //使用有序set
        Set<Long> collect = Arrays.stream(groupsStr.split(",")).map(Long::valueOf).collect(Collectors.toCollection(LinkedHashSet::new));
        collect.forEach(groupId -> {
            //每个群延时发送防止被当做机器人
            try {
                MessageChainBuilder messageBuilder = new MessageChainBuilder();
                Group group = qQbotFactory.getBot().getGroup(groupId);
                messageBuilder.add(messageChain);
                if (CollectionUtils.isNotEmpty(imageList)){
                    imageList.parallelStream().forEach(image -> messageBuilder.add(group.uploadImage(ExternalResource.create(image))));
                }
                log.info("send qq msg:"+messageChain.contentToString());
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

    public void sendMessageForAllGroups(List messageList){
        //获取配置的QQ群
        String groupsStr = GlobalConfig.getConfig(ConfigKeyEnum.GROUPS);
        //使用有序set
        Set<Long> collect = Arrays.stream(groupsStr.split(",")).map(Long::valueOf).collect(Collectors.toCollection(LinkedHashSet::new));
        collect.forEach(groupId -> {
            //每个群延时发送防止被当做机器人
            try {
                MessageChainBuilder messageBuilder = new MessageChainBuilder();
                Group group = qQbotFactory.getBot().getGroup(groupId);
                if (CollectionUtils.isNotEmpty(messageList)){
                    ListIterator iterator = messageList.listIterator();
                    while (iterator.hasNext()){
                        Object message = iterator.next();
                        if (message instanceof String){
                            messageBuilder.add((String)message);
                        }else if (message instanceof File){
                            File file  = (File) message;
                            MimeType mimeType = FileUtil.detectFileMimeType(file);
                            if (null == mimeType){
                                messageBuilder.add(group.uploadImage(ExternalResource.create(file)));
                            }else{
                                if ("image".equals(mimeType.getType())){
                                    messageBuilder.add(group.uploadImage(ExternalResource.create(file)));
                                }else if ("text".equals(mimeType.getType())){
                                    messageBuilder.add(Files.readString(file.toPath(), StandardCharsets.UTF_8));
                                }else{
                                    AbsoluteFile absoluteFile = group.getFiles().uploadNewFile(file.getAbsolutePath(), ExternalResource.create(file));
                                    messageBuilder.add(absoluteFile.toMessage());
                                }
                            }
                        }
                    }
                }
                group.sendMessage(messageBuilder.build());
                Thread.sleep(Duration.ofSeconds(2).toMillis());
            } catch (Exception e) {
                log.error("send message to group:{} exception:{}",groupId,ExceptionUtils.getStackTrace(e));
            }
        });
    }

}
