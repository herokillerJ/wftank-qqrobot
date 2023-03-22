package cn.wftank.qqrobot.app;

import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.qqrobot.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import xyz.cssxsh.mirai.tool.FixProtocolVersion;

import java.io.FileNotFoundException;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@MapperScan("cn.wftank.qqrobot.dao.mapper")
@EnableWebMvc
@ComponentScan(basePackages = "cn.wftank.qqrobot")
public class RobotAppApplication {

    public static Logger logger = LoggerFactory.getLogger(RobotAppApplication.class);

    public static void main(String[] args) throws FileNotFoundException {
        GlobalConfig.checkConfig();
        FixProtocolVersion.update();
        logger.info(JsonUtil.toJson(FixProtocolVersion.info()));
        SpringApplication.run(RobotAppApplication.class, args);
    }

}
