package cn.wftank.qqrobot.app.handler;

import cn.wftank.qqrobot.discord4j.spring.annotations.DiscordEventListener;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DiscordEventHandlers {
    private static final Logger log = LoggerFactory.getLogger(DiscordEventHandlers.class);

    @DiscordEventListener
    public Mono<Message> listen(MessageCreateEvent messageCreateEvent) {
        Message msg = messageCreateEvent.getMessage();
        log.info(msg.getContent());
        return Mono.empty();
    }

}