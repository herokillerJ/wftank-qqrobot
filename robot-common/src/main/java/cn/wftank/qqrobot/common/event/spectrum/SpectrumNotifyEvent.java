package cn.wftank.qqrobot.common.event.spectrum;

import cn.wftank.qqrobot.common.enums.event.spectrum.SpectrumEventType;
import cn.wftank.qqrobot.common.event.NotifyEvent;
import cn.wftank.qqrobot.common.model.event.SpectrumThread;

import java.util.List;

public class SpectrumNotifyEvent implements NotifyEvent {

    //是否为第一次启动小助手
    private boolean first;

    private List<SpectrumThread> newThreads;

    private SpectrumEventType type;

    public SpectrumEventType getType() {
        return type;
    }

    public void setType(SpectrumEventType type) {
        this.type = type;
    }

    public List<SpectrumThread> getNewThreads() {
        return newThreads;
    }

    public void setNewThreads(List<SpectrumThread> newThreads) {
        this.newThreads = newThreads;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }
}
