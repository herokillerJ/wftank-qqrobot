package cn.wftank.qqrobot.app.handler;

import net.mamoe.mirai.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 状态检查
 */
@Component
public class QQHealthCheker {

    private static final Logger log = LoggerFactory.getLogger(QQHealthCheker.class);

    @Autowired
    private Bot bot;

    //5秒检查一次机器人登录状态,防止断线
    @Scheduled(fixedRate = 5000)
    private void robotHealthCheck(){
        log.info("check bot:{} health..",bot.getId());
        if (!bot.isOnline()){
            log.info("bot:{} offline，start login",bot.getId());
            bot.login();
            log.info("bot:{} login success",bot.getId());
        }
    }

}
