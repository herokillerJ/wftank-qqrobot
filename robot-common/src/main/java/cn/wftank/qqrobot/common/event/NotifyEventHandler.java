package cn.wftank.qqrobot.common.event;

import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NotifyEventHandler implements EventHandler<NotifyEventWrapper> {

    private static final Logger log = LoggerFactory.getLogger(NotifyEventHandler.class);

    private Map<Class<? extends NotifyEvent>, EventHandler> handlerMap;

    @Override
    public void onEvent(NotifyEventWrapper eventWrapper, long sequence, boolean endOfBatch) throws Exception {
        handlerMap.get(eventWrapper.getNotifyEvent().getClass()).onEvent(eventWrapper.getNotifyEvent(),sequence,endOfBatch);
    }

    public static class Builder{
        private Map<Class<? extends NotifyEvent>, EventHandler> handlerMap = new HashMap<>();

        public void addEventHandler(Class<? extends NotifyEvent> clazz, EventHandler eventHandler){
            handlerMap.put(clazz,eventHandler);
        }

        public NotifyEventHandler build(){
            NotifyEventHandler notifyEventHandler = new NotifyEventHandler();
            notifyEventHandler.handlerMap = Collections.unmodifiableMap(this.handlerMap);
            return notifyEventHandler;
        }
    }
}
