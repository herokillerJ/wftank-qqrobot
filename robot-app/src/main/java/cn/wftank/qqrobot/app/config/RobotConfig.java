package cn.wftank.qqrobot.app.config;

import cn.wftank.qqrobot.app.handler.QQEventHandlers;
import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.qqrobot.common.util.StringUtils;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.GroupEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.LoggerAdapters;
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
        String miraiProtocol = GlobalConfig.getConfig(ConfigKeyEnum.MIRAI_PROTOCOL);
        //默认手机登录
        if (StringUtils.isBlank(miraiProtocol)){
            miraiProtocol = "ANDROID_PHONE";
        }
        BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.valueOf(miraiProtocol);
        protocol = protocol == null ? BotConfiguration.MiraiProtocol.ANDROID_PHONE : protocol;

        BotConfiguration.MiraiProtocol finalProtocol = protocol;
        Bot bot = BotFactory.INSTANCE.newBot(Long.valueOf(GlobalConfig.getConfig(ConfigKeyEnum.QQ)),
                GlobalConfig.getConfig(ConfigKeyEnum.PASSWORD),new BotConfiguration() {{
            fileBasedDeviceInfo(); // 使用 device.json 存储设备信息
            setProtocol(finalProtocol); // 切换协议
            File workDir = new File("./qqbot");
            if (!workDir.exists()){
                try {
                    Files.createDirectories(workDir.toPath());
                } catch (IOException e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
            setWorkingDir(workDir);
            setNetworkLoggerSupplier(bot -> LoggerAdapters.asMiraiLogger(LoggerFactory.getLogger(this.getClass())));
            setBotLoggerSupplier(bot -> LoggerAdapters.asMiraiLogger(LoggerFactory.getLogger(this.getClass())));
        }});
        bot.getEventChannel()
                .filter(ev -> ev instanceof GroupEvent)
                .registerListenerHost(qqEventHandlers);
        bot.login();
        return bot;
    }
}
