package cn.wftank.qqrobot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
//@MapperScan("cn.wftank.qqrobot.dao.mapper")
@EnableWebMvc
@ComponentScan("cn.wftank.qqrobot")
public class RobotAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(RobotAppApplication.class, args);
    }

}
