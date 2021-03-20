package cn.wftank.qqrobot.schedule.job;

import cn.wftank.qqrobot.common.event.NotifyEventPublisher;
import cn.wftank.qqrobot.common.event.tieba.TiebaNotifyEvent;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
        try(BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String latestPid = reader.readLine();
            if (StringUtils.isBlank(latestPid)){
                first = true;
            }
            latestPid = Optional.ofNullable(latestPid).map(String::trim).orElse("");
            log.info("g-lao lastPid:"+latestPid);
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
                String pid = path.substring(path.lastIndexOf("/")+1);
                TiebaThread thread = new TiebaThread();
                thread.setTitle(title);
                thread.setGroup(group);
                thread.setUrl(path);
                thread.setPid(pid);
                threads.add(thread);
            }
            List<TiebaThread> newThreads = new ArrayList<>();
            TiebaThread firstThread = threads.get(0);
            String newestPid = firstThread.getPid().trim();
            log.info("g-lao newestPid:"+newestPid);
            //xpath选择有时会出问题导致所有选择器都是空,这里做下过滤
            if (StringUtils.isBlank(newestPid)) return;
            if (first){
                TiebaThread tiebaThread = threads.get(0);
                newThreads.add(tiebaThread);
            }else{
                if (newestPid.equals(latestPid)){
                    return;
                }
                for (TiebaThread thread : threads) {
                    if (thread.getPid().equals(latestPid)){
                        break;
                    }else{
                        if (StringUtils.isNotBlank(newestPid)){
                            newThreads.add(thread);
                        }
                    }
                }
            }
            Files.writeString(file.toPath(),newestPid);
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
