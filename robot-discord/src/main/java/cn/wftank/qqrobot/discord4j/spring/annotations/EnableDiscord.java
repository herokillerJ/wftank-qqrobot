package cn.wftank.qqrobot.discord4j.spring.annotations;

import cn.wftank.qqrobot.discord4j.spring.DiscordAutoConfiguration;
import cn.wftank.qqrobot.discord4j.spring.beans.DiscordChatBeanProcessor;
import cn.wftank.qqrobot.discord4j.spring.beans.DiscordEventBeanProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable Discord support in a Spring Boot application.
 *
 * @author Matty Southall
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({DiscordAutoConfiguration.class, DiscordEventBeanProcessor.class, DiscordChatBeanProcessor.class})
public @interface EnableDiscord {
}
