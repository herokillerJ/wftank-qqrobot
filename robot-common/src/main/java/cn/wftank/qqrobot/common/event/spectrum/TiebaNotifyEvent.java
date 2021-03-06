package cn.wftank.qqrobot.common.event.spectrum;

import cn.wftank.qqrobot.common.event.NotifyEvent;
import cn.wftank.qqrobot.common.model.event.TiebaThread;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TiebaNotifyEvent implements NotifyEvent {

    //是否为第一次启动小助手
    private boolean first;

    private List<TiebaThread> newThreads;

    private String authorName;

}
