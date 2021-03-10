package cn.wftank.qqrobot.schedule.job;

import cn.wftank.qqrobot.common.event.NotifyEventPublisher;
import cn.wftank.qqrobot.common.event.issue.IssueNotifyEvent;
import cn.wftank.qqrobot.common.model.event.IssueEntity;
import cn.wftank.qqrobot.common.util.JsonUtil;
import cn.wftank.qqrobot.common.util.OKHttpUtil;
import cn.wftank.qqrobot.schedule.convertor.IssueEntityConvertor;
import cn.wftank.qqrobot.schedule.model.vo.request.issue.IssueCouncilReq;
import cn.wftank.qqrobot.schedule.model.vo.response.issue.IssueCouncilResp;
import cn.wftank.qqrobot.schedule.model.vo.response.issue.ResultsetItem;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Component
public class IssueCouncilJob {

    private static final Logger log = LoggerFactory.getLogger(IssueCouncilJob.class);

    @Autowired
    private NotifyEventPublisher notifyEventPublisher;

    @Scheduled(fixedDelay = 1000*60)
    private void issueCouncilWatchJob(){
        String jobName = "issue council watch job";
        log.info(jobName+" start");
        File file = new File("./issue_council_flag.txt");
        boolean first = false;
        if (!file.exists()){
            first = true;
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                log.error(jobName+"create flag file ex:"+ ExceptionUtils.getStackTrace(e));
            }
        }
        try {
            String latestId = Files.readString(file.toPath());
            if (StringUtils.isBlank(latestId)){
                first = true;
            }
            IssueCouncilReq req = new IssueCouncilReq();
            req.setPage(1);
            req.setPagesize(10);
            req.setSort("newest");
            req.setModuleUrl("star-citizen-alpha-3");
            IssueCouncilResp resp = OKHttpUtil.post("https://robertsspaceindustries.com/community/issue-council/api/issue/list"
                    , req, new TypeReference<IssueCouncilResp>() {});
            List<IssueEntity> newIssues = new ArrayList<>();
            ResultsetItem firstIssue = resp.getData().getResultset().get(0);
            String newestId = firstIssue.getId();
            if (first){
                newIssues.add(IssueEntityConvertor.convert(firstIssue));
            }else{
                if (newestId.equals(latestId)){
                    return;
                }
                for (ResultsetItem issue : resp.getData().getResultset()) {
                    if (issue.getId().equals(latestId)){
                        break;
                    }else{
                        newIssues.add(IssueEntityConvertor.convert(issue));
                    }
                }
            }
            Files.writeString(file.toPath(),newestId);
            if (!newIssues.isEmpty()){
                IssueNotifyEvent event = new IssueNotifyEvent();
                event.setNewIssues(newIssues);
                event.setFirst(first);
                notifyEventPublisher.publish(event);
                log.info(jobName+" new issues:"+JsonUtil.toJson(newIssues));
            }

        } catch (IOException e) {
            log.error(jobName+"create flag file ex:"+ ExceptionUtils.getStackTrace(e));
        }
    }

}
