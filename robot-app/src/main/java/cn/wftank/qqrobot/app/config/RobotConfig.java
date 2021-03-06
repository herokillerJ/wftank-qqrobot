package cn.wftank.qqrobot.app.config;

import cn.wftank.qqrobot.app.handler.QQEventHandlers;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.GroupEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Configuration
public class RobotConfig {

    private static final Logger log = LoggerFactory.getLogger(RobotConfig.class);


    @Bean
    public Bot bot(QQEventHandlers qqEventHandlers){
        Bot bot = BotFactory.INSTANCE.newBot(1663446950, "JjW20thborn",new BotConfiguration() {{
            fileBasedDeviceInfo(); // 使用 device.json 存储设备信息
            setProtocol(MiraiProtocol.ANDROID_PHONE); // 切换协议
            File workDir = new File("./qqbot");
            if (!workDir.exists()){
                try {
                    Files.createDirectories(workDir.toPath());
                } catch (IOException e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
            setWorkingDir(workDir);
            setNetworkLoggerSupplier(bot -> new MiraiLogger());
            setBotLoggerSupplier(bot -> new MiraiLogger());
        }});

        bot.getEventChannel()
                .filter(ev -> ev instanceof GroupEvent && ((GroupEvent)ev).getGroup().getId()==1032374245)
                .registerListenerHost(qqEventHandlers);
        bot.login();
        return bot;
    }

}
