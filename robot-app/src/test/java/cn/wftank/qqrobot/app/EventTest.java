package cn.wftank.qqrobot.app;


import cn.wftank.qqrobot.app.config.EventConfig;
import cn.wftank.qqrobot.app.event.handler.IssueEventHandler;
import cn.wftank.qqrobot.app.event.handler.SpectrumEventHandler;
import cn.wftank.qqrobot.app.event.handler.TiebaEventHandler;
import cn.wftank.qqrobot.common.event.NotifyEventFactory;
import cn.wftank.qqrobot.common.event.NotifyEventPublisher;
import cn.wftank.qqrobot.common.event.NotifyEventWrapper;
import cn.wftank.qqrobot.schedule.job.IssueCouncilJob;
import com.lmax.disruptor.EventHandler;
import org.junit.jupiter.api.Test;

import java.util.Arrays;


public class EventTest {

    private NotifyEventPublisher publisher;


    {
        init();
    }
    public void init(){
        EventConfig eventConfig = new EventConfig();
        IssueEventHandler issueEventHandler = new IssueEventHandler();
        SpectrumEventHandler spectrumEventHandler = new SpectrumEventHandler();
        TiebaEventHandler tiebaEventHandler = new TiebaEventHandler();
        EventHandler<NotifyEventWrapper> handler = eventConfig.notifyEventHandler(Arrays.asList(issueEventHandler, spectrumEventHandler, tiebaEventHandler));
        NotifyEventFactory notifyEventFactory = new NotifyEventFactory();
        this.publisher = eventConfig.notifyEventPublisher(notifyEventFactory, handler);
        return ;
    }

    @Test
    public void publishTest() throws InterruptedException {
        IssueCouncilJob issueCouncilJob = new IssueCouncilJob();
        issueCouncilJob.setNotifyEventPublisher(this.publisher);
        for (int i = 0; i < 10; i++) {
            issueCouncilJob.issueCouncilWatchJob();
            Thread.sleep(1000);
        }
    }
}
