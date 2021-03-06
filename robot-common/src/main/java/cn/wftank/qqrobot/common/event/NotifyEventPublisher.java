package cn.wftank.qqrobot.common.event;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

public class NotifyEventPublisher {

    private final RingBuffer<NotifyEventWrapper> ringBuffer;

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
        ringBuffer.publishEvent(TRANSLATOR, notifyEvent);
    }
}