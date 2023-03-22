package cn.wftank.qqrobot.common.event.command;

import cn.wftank.qqrobot.common.enums.event.command.CommandEventType;
import cn.wftank.qqrobot.common.event.NotifyEvent;
import cn.wftank.qqrobot.common.model.event.TiebaThread;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CommandNotifyEvent implements NotifyEvent {

    //是否为第一次启动小助手
    private String command;
    private Path commandPath;
    private CommandEventType type;

}
