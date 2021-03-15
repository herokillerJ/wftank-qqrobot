package cn.wftank.qqrobot.app;


import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DiscordTest {

    @Test
    public void autoFind() {
        GatewayDiscordClient client = DiscordClientBuilder.create("ODIxMDAyNzI4MDYyMzIwNjU3.YE9YXw.ppWdZm0_7iw3M8iwa3lWE8YDqNY")
                .build()
                .login()
                .block();
        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    final User self = event.getSelf();
                    System.out.println(String.format(
                            "Logged in as %s#%s", self.getUsername(), self.getDiscriminator()
                    ));
                });
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getChannelId().asString().equals("821036487269154816"))
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .subscribe(message -> System.out.println(message.getContent()));
        client.onDisconnect().block();
    }

    private void registerListeners(EventDispatcher eventDispatcher) {
        eventDispatcher.on(MessageCreateEvent.class).subscribe(event -> System.out.println(event.getMessage()));
    }

}
