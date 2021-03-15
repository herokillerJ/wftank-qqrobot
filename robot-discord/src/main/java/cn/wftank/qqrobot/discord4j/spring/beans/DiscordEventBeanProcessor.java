package cn.wftank.qqrobot.discord4j.spring.beans;

import cn.wftank.qqrobot.discord4j.spring.annotations.DiscordEventListener;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.Event;
import org.reactivestreams.Publisher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * BeanPostProcessor for {@link Event}'s.
 *
 * @author Matty Southall
 * @since 1.0
 */
@Component("discordEventBeanProcessor")
@ConditionalOnBean(GatewayDiscordClient.class)
public class DiscordEventBeanProcessor implements BeanPostProcessor {

    EventDispatcher eventDispatcher;

    @Autowired
    public DiscordEventBeanProcessor( EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * 注册discord事件监听器
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithMethods(bean.getClass(), method -> {
            doDiscordEventListener(method, bean);
        },method -> method.isAnnotationPresent(DiscordEventListener.class));

        return bean;
    }

    /**
     * Methods annotated with {@link DiscordEventListener}.
     */
    @SuppressWarnings("unchecked")
    private void doDiscordEventListener(Method method, Object bean) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0) {
            if (Event.class.isAssignableFrom(parameterTypes[0])) {
                doEvent(method, bean, (Class<? extends Event>) parameterTypes[0]);
            }
        }
    }

    private void doEvent(Method method, Object bean, Class<? extends Event> parameterClazz) {
        eventDispatcher.on(parameterClazz)
                .flatMap(event -> {
                    Optional<Object> r = Optional.ofNullable(ReflectionUtils.invokeMethod(method, bean, event));
                    return r.filter(o -> o instanceof Publisher)
                            .map(o -> (Publisher)o)
                            .orElse(Mono.empty());
                })
                .doOnError(error -> EventUtils.doOnError(bean, error))
                .onErrorStop()
                .subscribe();
    }
}
