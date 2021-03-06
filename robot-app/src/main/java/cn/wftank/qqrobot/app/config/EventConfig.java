package cn.wftank.qqrobot.app.config;

import cn.wftank.qqrobot.common.event.NotifyEventFactory;
import cn.wftank.qqrobot.common.event.NotifyEventHandler;
import cn.wftank.qqrobot.common.event.NotifyEventPublisher;
import cn.wftank.qqrobot.common.event.NotifyEventWrapper;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ThreadFactory;

@Configuration
public class EventConfig {

    private static final Logger log = LoggerFactory.getLogger(EventConfig.class);


    @Bean
    public NotifyEventFactory notifyEventFactory(){
        return new NotifyEventFactory();
    }

    @Bean
    public EventHandler<NotifyEventWrapper> notityEventHandler(List<EventHandler> eventHandlers){
        NotifyEventHandler.Builder builder = new NotifyEventHandler.Builder();
        eventHandlers.forEach(eventHandler -> {
            Type genericInterface = AopUtils.getTargetClass(eventHandler).getGenericInterfaces()[0];
            Type type = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
            builder.addEventHandler((Class)type,eventHandler);
            log.info("已注册:"+type.getTypeName()+"事件处理器");
        });
        return builder.build();
    }

    @Bean
    public NotifyEventPublisher notifyEventPublisher(NotifyEventFactory factory, EventHandler<NotifyEventWrapper> notityEventHandler){
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().daemon(true).namingPattern("notify-event-disruptor").build();
        Disruptor<NotifyEventWrapper> disruptor = new Disruptor(factory, 1024, threadFactory,ProducerType.SINGLE, new BlockingWaitStrategy());
        disruptor.handleEventsWith(notityEventHandler);

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<NotifyEventWrapper> ringBuffer = disruptor.getRingBuffer();

        return new NotifyEventPublisher(ringBuffer);
    }


}
