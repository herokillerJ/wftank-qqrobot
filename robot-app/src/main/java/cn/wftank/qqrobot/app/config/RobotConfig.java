package cn.wftank.qqrobot.app.config;

import cn.wftank.qqrobot.app.handler.QQEventHandlers;
import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.GroupEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class RobotConfig {

    private static final Logger log = LoggerFactory.getLogger(RobotConfig.class);


    @Bean
    public Bot bot(QQEventHandlers qqEventHandlers){
        Bot bot = BotFactory.INSTANCE.newBot(Long.valueOf(GlobalConfig.getConfig(ConfigKeyEnum.QQ)),
                GlobalConfig.getConfig(ConfigKeyEnum.PASSWORD),new BotConfiguration() {{
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
        String groups = GlobalConfig.getConfig(ConfigKeyEnum.GROUPS);
        if (StringUtils.isBlank(groups)){
            throw new RuntimeException("请配置发通知的QQ群");
        }
        Set<Long> groupSet = Arrays.stream(groups.split(",")).map(Long::valueOf).collect(Collectors.toSet());
        bot.getEventChannel()
                .filter(ev -> ev instanceof GroupEvent && groupSet.contains(((GroupEvent)ev).getGroup().getId()) )
                .registerListenerHost(qqEventHandlers);
        bot.login();
        return bot;
    }

}
