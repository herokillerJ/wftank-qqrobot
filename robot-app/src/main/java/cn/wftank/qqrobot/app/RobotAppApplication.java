package cn.wftank.qqrobot.app;

import cn.wftank.qqrobot.common.config.GlobalConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.FileNotFoundException;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@MapperScan("cn.wftank.qqrobot.dao.mapper")
@EnableWebMvc
@ComponentScan("cn.wftank.qqrobot")
public class RobotAppApplication {

    public static void main(String[] args) throws FileNotFoundException {
        GlobalConfig.checkConfig();
        SpringApplication.run(RobotAppApplication.class, args);
    }

}
