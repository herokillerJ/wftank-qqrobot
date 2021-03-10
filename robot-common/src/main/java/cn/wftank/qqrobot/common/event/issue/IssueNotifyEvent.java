package cn.wftank.qqrobot.common.event.issue;

import cn.wftank.qqrobot.common.event.NotifyEvent;
import cn.wftank.qqrobot.common.model.event.IssueEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class IssueNotifyEvent implements NotifyEvent {

    //是否为第一次启动小助手
    private boolean first;

    private List<IssueEntity> newIssues;

}
