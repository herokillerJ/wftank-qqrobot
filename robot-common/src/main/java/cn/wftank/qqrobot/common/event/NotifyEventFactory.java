package cn.wftank.qqrobot.common.event;

import com.lmax.disruptor.EventFactory;

public class NotifyEventFactory implements EventFactory<NotifyEventWrapper> {
    public NotifyEventWrapper newInstance() {
        return new NotifyEventWrapper();
    }
}