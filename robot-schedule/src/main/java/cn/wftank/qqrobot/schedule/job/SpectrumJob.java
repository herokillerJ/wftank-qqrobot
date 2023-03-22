package cn.wftank.qqrobot.schedule.job;

import cn.wftank.qqrobot.common.enums.event.spectrum.SpectrumEventType;
import cn.wftank.qqrobot.common.event.NotifyEventPublisher;
import cn.wftank.qqrobot.common.event.spectrum.SpectrumNotifyEvent;
import cn.wftank.qqrobot.common.model.event.SpectrumThread;
import cn.wftank.qqrobot.common.util.JsonUtil;
import cn.wftank.qqrobot.common.util.OKHttpUtil;
import cn.wftank.qqrobot.schedule.convertor.SpectrumThreadConvertor;
import cn.wftank.qqrobot.schedule.model.vo.request.spectrum.SpectrumAnnouncementsReq;
import cn.wftank.qqrobot.schedule.model.vo.response.spectrum.SpectrumResp;
import cn.wftank.qqrobot.schedule.model.vo.response.spectrum.ThreadsItem;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class SpectrumJob {

    private static final Logger log = LoggerFactory.getLogger(SpectrumJob.class);

    @Autowired
    private NotifyEventPublisher notifyEventPublisher;


    @Scheduled(fixedDelay = 1000*60)
    private void AnnouncementsWatchJob(){
        String jobName = "spectrum announcements watch job";
        log.info(jobName+" start");
        File file = new File("./spectrum_announcements_flag.txt");
        boolean first = false;
        if (!file.exists()){
            first = true;
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                log.error(jobName+"create flag file ex:"+ ExceptionUtils.getStackTrace(e));
            }
        }
        try(BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String latestId = reader.readLine();
            if (StringUtils.isBlank(latestId)){
                first = true;
            }
            SpectrumAnnouncementsReq req = new SpectrumAnnouncementsReq();
            req.setChannelId(1);
            req.setPage(1);
            req.setSort("newest");
            SpectrumResp<ThreadsItem> resp = OKHttpUtil.postJson("https://robertsspaceindustries.com/api/spectrum/forum/channel/threads"
                    , req, new TypeReference<SpectrumResp<ThreadsItem>>() {});
            List<SpectrumThread> newThreads = new ArrayList<>();
            ThreadsItem firstThread = resp.getRespData().getThreads().get(0);
            String newestId = firstThread.getId();
            if (first){
                newThreads.add(SpectrumThreadConvertor.convert(firstThread));
            }else{
                if (newestId.equals(latestId)){
                    return;
                }
                for (ThreadsItem thread : resp.getRespData().getThreads()) {
                    if (thread.getId().equals(latestId)){
                        break;
                    }else{
                        newThreads.add(SpectrumThreadConvertor.convert(thread));
                    }
                }
            }
            Files.write(file.toPath(),newestId.getBytes(StandardCharsets.UTF_8));
            if (!newThreads.isEmpty()){
                SpectrumNotifyEvent event = new SpectrumNotifyEvent();
                event.setNewThreads(newThreads);
                event.setFirst(first);
                event.setType(SpectrumEventType.ANNOUNCEMENTS);
                notifyEventPublisher.publish(event);
                log.info(jobName+" new threads:"+JsonUtil.toJson(newThreads));
            }

        } catch (IOException e) {
            log.error(jobName+"create flag file ex:"+ ExceptionUtils.getStackTrace(e));
        }

    }

    @Scheduled(fixedDelay = 1000*60)
    private void PatchNotesWatchJob(){
        String jobName = "spectrum patchNotes watch job";
        log.info(jobName+" start");
        File file = new File("./spectrum_patch_notes_flag.txt");
        boolean first = false;
        if (!file.exists()){
            first = true;
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                log.error(jobName+"create flag file ex:"+ ExceptionUtils.getStackTrace(e));
            }
        }
        try(BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String latestId = reader.readLine();
            if (StringUtils.isBlank(latestId)){
                first = true;
            }
            SpectrumAnnouncementsReq req = new SpectrumAnnouncementsReq();
            req.setChannelId(190048);
            req.setPage(1);
            req.setSort("newest");
            SpectrumResp<ThreadsItem> resp = OKHttpUtil.postJson("https://robertsspaceindustries.com/api/spectrum/forum/channel/threads"
                    , req, new TypeReference<SpectrumResp<ThreadsItem>>() {});
            List<SpectrumThread> newThreads = new ArrayList<>();
            ThreadsItem firstThread = resp.getRespData().getThreads().get(0);
            String newestId = firstThread.getId();
            if (first){
                newThreads.add(SpectrumThreadConvertor.convert(firstThread));
            }else{
                if (newestId.equals(latestId)){
                    return;
                }
                for (ThreadsItem thread : resp.getRespData().getThreads()) {
                    if (!thread.getId().equals(latestId)){
                        newThreads.add(SpectrumThreadConvertor.convert(thread));
                    }
                    break;
                }
            }
            Files.write(file.toPath(),newestId.getBytes(StandardCharsets.UTF_8), StandardOpenOption.SYNC);
            if (!newThreads.isEmpty()){
                SpectrumNotifyEvent event = new SpectrumNotifyEvent();
                event.setNewThreads(newThreads);
                event.setFirst(first);
                event.setType(SpectrumEventType.PATCH_NOTES);
                notifyEventPublisher.publish(event);
                log.info(jobName+" new threads:"+JsonUtil.toJson(newThreads));
            }

        } catch (IOException e) {
            log.error(jobName+"create flag file ex:"+ ExceptionUtils.getStackTrace(e));
        }

    }
}
