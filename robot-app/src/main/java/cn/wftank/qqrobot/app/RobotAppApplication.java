package cn.wftank.qqrobot.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.wftank.qqrobot.dao.mapper")
public class RobotAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(RobotAppApplication.class, args);
    }

}
