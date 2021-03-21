package cn.wftank.qqrobot.app.handler;

import cn.wftank.qqrobot.app.mirai.QQGroupMessageSender;
import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.qqrobot.common.translate.BaiduTranslator;
import cn.wftank.qqrobot.common.util.FileUtil;
import cn.wftank.qqrobot.discord4j.spring.annotations.DiscordEventListener;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.Embed;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class DiscordEventHandlers {
    private static final Logger log = LoggerFactory.getLogger(DiscordEventHandlers.class);
    @Autowired
    private QQGroupMessageSender sender;
    @Autowired
    private BaiduTranslator translator;

    @DiscordEventListener
    public Mono<Message> listen(MessageCreateEvent event) {
        try {
            Message message = event.getMessage();
            log.info("discord receive message:"+message.getContent());
            if (!filterMessage(message)) return Mono.empty();
            List msgList = new LinkedList<>();
            msgList.add("Discord消息：");
            //发言人
            message.getAuthor().ifPresent(author -> {
                msgList.add("\n"+author.getTag()+"说：");
            });
            //消息体
            if (StringUtils.hasText(message.getContent())){
                msgList.add("\n"+message.getContent());
                //翻译
                msgList.add("[机翻:"+translator.translateEn2Cn(message.getContent())+"]");
            }
            //附件 一般是图片
            Set<Attachment> attachments = message.getAttachments();
            if (!CollectionUtils.isEmpty(attachments)){
                for (Attachment attachment: attachments) {
                    msgList.add(FileUtil.downloadAsTmpFromUrl(attachment.getUrl()));
                }
            }
            //内嵌消息
            List<Embed> embeds = event.getMessage().getEmbeds();
            if (!CollectionUtils.isEmpty(embeds)){
                msgList.add("\n内嵌消息：");
                embeds.stream().forEach(embed -> {
                    embed.getAuthor().ifPresent(author -> msgList.add("\n作者："+author.getName()+""));
                    embed.getTitle().ifPresent(title -> {
                        msgList.add("\n标题："+title+"");
                        msgList.add("[机翻："+translator.translateEn2Cn(title)+"]");
                    });
                    embed.getDescription().filter(StringUtils::hasText)
                            .ifPresent(des -> {
                                msgList.add("\n描述："+des);
                                msgList.add("[机翻："+translator.translateEn2Cn(des)+"]");
                            });
                    embed.getImage().ifPresent(image -> {
                        try {
                            msgList.add("\n图片："+image.getUrl());
                            msgList.add(FileUtil.downloadAsTmpFromUrl(image.getUrl()));
                        } catch (Exception e) {
                            log.error(ExceptionUtils.getStackTrace(e));
                        }
                    });
                    embed.getVideo().ifPresent(video -> msgList.add("\n视频："+video.getUrl()));
                    embed.getUrl().ifPresent(url -> msgList.add("\n链接："+url));
                });
            }
            sender.sendMessageForAllGroups(msgList);
        }catch (Exception e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return Mono.empty();
    }

    //过滤分组
    private boolean filterMessage(Message message) {
        Boolean flag = false;
        String channelStr = GlobalConfig.getConfig(ConfigKeyEnum.DISCORD_CHANNEL_IDS);
        if (StringUtils.hasText(channelStr)){
            if (Arrays.asList(channelStr.split(",")).contains(message.getChannelId().asString())) {
                flag=true;
            };
        }
        return flag;
    }

}