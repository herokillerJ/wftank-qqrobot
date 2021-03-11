package cn.wftank.qqrobot.common.event;

import cn.wftank.qqrobot.common.util.JsonUtil;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NotifyEventPublisher {

    private final RingBuffer<NotifyEventWrapper> ringBuffer;

    private ExecutorService service = new ThreadPoolExecutor(1,1
            ,2, TimeUnit.MINUTES
            ,new SynchronousQueue<>(), new BasicThreadFactory.Builder()
            .namingPattern("notify-event-publisher-%d").build());

    public NotifyEventPublisher(RingBuffer<NotifyEventWrapper> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorOneArg<NotifyEventWrapper, NotifyEvent> TRANSLATOR =
            new EventTranslatorOneArg<NotifyEventWrapper, NotifyEvent>() {
                @Override
                public void translateTo(NotifyEventWrapper event, long sequence, NotifyEvent notifyEvent) {
                    event.setNotifyEvent(notifyEvent);
                }
            };

    public void publish(NotifyEvent notifyEvent) {
        //单一消费者

        service.submit(()->{
            log.info("publish event:"+ JsonUtil.toJson(notifyEvent));
            ringBuffer.publishEvent(TRANSLATOR, notifyEvent);
        });
    }
}