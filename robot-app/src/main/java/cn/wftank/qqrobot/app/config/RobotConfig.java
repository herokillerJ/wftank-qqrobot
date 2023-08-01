package cn.wftank.qqrobot.app.config;

import cn.wftank.qqrobot.app.handler.QQEventHandlers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RobotConfig {

    @Bean
    public QQbotFactory qqbotFactory(QQEventHandlers qqEventHandlers){
        return new QQbotFactory(qqEventHandlers);
    }
}
