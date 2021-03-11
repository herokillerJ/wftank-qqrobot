package cn.wftank.qqrobot.common.event;

import cn.wftank.qqrobot.common.util.JsonUtil;
import com.lmax.disruptor.EventHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotifyEventHandler implements EventHandler<NotifyEventWrapper> {

    private static final Logger log = LoggerFactory.getLogger(NotifyEventHandler.class);

    private Map<String, EventHandler> handlerMap;

    @Override
    public void onEvent(NotifyEventWrapper eventWrapper, long sequence, boolean endOfBatch) throws Exception {
        try {
            Class<? extends NotifyEvent> clazz = eventWrapper.getNotifyEvent().getClass();
            log.info(clazz.getName()+"event receive,detail"+ JsonUtil.toJson(eventWrapper));
            handlerMap.get(clazz.getName()).onEvent(eventWrapper.getNotifyEvent(),sequence,endOfBatch);
        }catch (Exception e){
            log.error("on event ex:"+ ExceptionUtils.getStackTrace(e));
        }finally {
            eventWrapper.clear();
        }
    }

    public static class Builder{
        private Map<String, EventHandler> handlerMap = new ConcurrentHashMap<>();

        public void addEventHandler(Class<? extends NotifyEvent> clazz, EventHandler eventHandler){
            handlerMap.put(clazz.getName(),eventHandler);
        }

        public NotifyEventHandler build(){
            NotifyEventHandler notifyEventHandler = new NotifyEventHandler();
            notifyEventHandler.handlerMap = this.handlerMap;
            return notifyEventHandler;
        }
    }
}
