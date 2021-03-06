package cn.wftank.qqrobot.schedule.job;

import cn.wftank.qqrobot.common.event.NotifyEventPublisher;
import cn.wftank.qqrobot.common.event.spectrum.TiebaNotifyEvent;
import cn.wftank.qqrobot.common.model.event.TiebaThread;
import cn.wftank.qqrobot.common.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.Xsoup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class BaiduTiebaJob {

    private static final Logger log = LoggerFactory.getLogger(BaiduTiebaJob.class);

    @Autowired
    private NotifyEventPublisher notifyEventPublisher;


    @Scheduled(fixedDelay = 1000*60)
    private void gLaoWatchJob(){
        String jobName = "G-LAO watch job";
        log.info(jobName+" start");
        File file = new File("./tieba_glao_flag.txt");
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
            String latestTitle = Files.readString(file.toPath());
            if (StringUtils.isBlank(latestTitle)){
                first = true;
            }
            //获取Glao首页的帖子
            List<TiebaThread> threads = new ArrayList<>();
            Document document = Jsoup.connect("https://tieba.baidu.com/home/main?un=%E7%81%AC%E7%81%ACG%E7%81%AC%E7%81%AC&ie=utf-8&id=tb.1.c71983a8._2RHIWDtaJALopgO4mFVeg")
                    .get();
            XElements threadsEle = Xsoup.select(document.html(), "//div[@class='thread_name']");
            Iterator<Element> iterator = threadsEle.getElements().iterator();
            while (iterator.hasNext()){
                Element element = iterator.next();
                Element groupElement = Xsoup.select(element, "//a[@class='n_name']").getElements().get(0);
                String group = groupElement.attr("title");
                //不是星际公民吧的不看
                if (!"星际公民".equals(group)) continue;
                Element titleElement = Xsoup.select(element, "//a[@class='title']").getElements().get(0);
                String title = titleElement.attr("title");
                String path = titleElement.attr("href");
                path = "https://tieba.baidu.com/"+path.substring(0,path.indexOf("?"));
                TiebaThread thread = new TiebaThread();
                thread.setTitle(title);
                thread.setGroup(group);
                thread.setUrl(path);
                threads.add(thread);
            }
            List<TiebaThread> newThreads = new ArrayList<>();
            TiebaThread firstThread = threads.get(0);
            String newestTitle = firstThread.getTitle();
            if (first){
                TiebaThread tiebaThread = threads.get(0);
                newThreads.add(tiebaThread);
            }else{
                if (newestTitle.equals(latestTitle)){
                    return;
                }
                for (TiebaThread thread : threads) {
                    if (thread.getTitle().equals(latestTitle)){
                        break;
                    }else{
                        newThreads.add(thread);
                    }
                }
            }
            Files.writeString(file.toPath(),newestTitle);
            if (!newThreads.isEmpty()){
                TiebaNotifyEvent event = new TiebaNotifyEvent();
                event.setNewThreads(newThreads);
                event.setFirst(first);
                event.setAuthorName("G佬");
                notifyEventPublisher.publish(event);
                log.info(jobName+" new threads:"+JsonUtil.toJson(newThreads));
            }

        } catch (IOException e) {
            log.error(jobName+" ex:"+ ExceptionUtils.getStackTrace(e));
        }

    }

}
