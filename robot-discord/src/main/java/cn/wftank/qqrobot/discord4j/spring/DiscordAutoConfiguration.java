package cn.wftank.qqrobot.discord4j.spring;

import cn.wftank.qqrobot.discord4j.spring.beans.DiscordChatBeanProcessor;
import cn.wftank.qqrobot.discord4j.spring.beans.DiscordEventBeanProcessor;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.shard.GatewayBootstrap;
import discord4j.gateway.GatewayOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration for {@link DiscordClient}.
 *
 * @author Matty Southall
 * @since 1.0
 */
@Configuration
@Import(DiscordTokenAutoConfiguration.class)
@ConditionalOnProperty("discord.token")
public class DiscordAutoConfiguration extends DiscordClientConfig {

    @Bean("discordClient")
    @ConditionalOnMissingBean
    public DiscordClient discordClient(DiscordTokenProvider tokenProvider) {
        return DiscordClientBuilder.create(tokenProvider.getToken()).build();
    }

    @Bean("eventDispatcher")
    @ConditionalOnMissingBean
    public EventDispatcher eventDispatcher() {
        return EventDispatcher.builder().build();
    }

    @Bean("discordEventBeanProcessor")
    @ConditionalOnMissingBean
    public DiscordEventBeanProcessor discordEventBeanProcessor(EventDispatcher eventDispatcher){
        return new DiscordEventBeanProcessor(eventDispatcher);
    }

    @Bean("discordChatBeanProcessor")
    @ConditionalOnMissingBean
    public DiscordChatBeanProcessor discordChatBeanProcessor(EventDispatcher eventDispatcher){
        return new DiscordChatBeanProcessor(eventDispatcher);
    }


    @Bean
    @DependsOn({"discordClient", "eventDispatcher", "discordEventBeanProcessor"})
    @ConditionalOnMissingBean
    public GatewayDiscordClient gatewayDiscordClient(DiscordClient discordClient, EventDispatcher eventDispatcher) {
        GatewayBootstrap<GatewayOptions> gateway = discordClient.gateway();
        return gateway
                .setEventDispatcher(eventDispatcher)
                .login()
                .doOnNext(this::startAwaitThread)
                .block();
    }
}
