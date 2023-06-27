package cn.wftank.qqrobot.app.config;

import cn.wftank.qqrobot.app.handler.QQEventHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.cssxsh.mirai.tool.FixProtocolVersion;

@Configuration
public class RobotConfig {

    private static final Logger log = LoggerFactory.getLogger(RobotConfig.class);

    @Bean
    public QQbotFactory qqbotFactory(QQEventHandlers qqEventHandlers){
        FixProtocolVersion.update();
        return new QQbotFactory(qqEventHandlers);
    }
}
