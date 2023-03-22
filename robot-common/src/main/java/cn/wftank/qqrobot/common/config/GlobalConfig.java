package cn.wftank.qqrobot.common.config;

import cn.wftank.qqrobot.common.util.StringUtils;
import cn.wftank.qqrobot.common.util.WatchDir;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author: wftank
 * @create: 2021-03-11 14:30
 * @description: 全局配置定时刷新
 **/
@Slf4j
public class GlobalConfig {
    private static Logger logger = LoggerFactory.getLogger(GlobalConfig.class);

    //bot
    public static final String CONFIG_DIR = "./";
    public static final String CONFIG_NAME;
    static {
        String property = System.getProperty("spring.profiles.active");
        String filePath = "config.properties";
        if (StringUtils.isNotBlank(property)){
            filePath = "config-"+property+".properties";
        }
        CONFIG_NAME = filePath;
    }
    private static final String CONFIG_PATH = CONFIG_DIR+CONFIG_NAME;

    private static final ExecutorService GLOBALCONFIG_MONITOR = Executors.newSingleThreadExecutor(new BasicThreadFactory.Builder().daemon(true).namingPattern("globalconfig-monitor").build());

    static {
        try {
            WatchDir watchDir = new WatchDir(Paths.get(CONFIG_DIR).toAbsolutePath().normalize(), false, (event, filePath) -> {
                if (filePath.getFileName().equals(CONFIG_NAME)){
                    refreshConfig();
                }
            }, StandardWatchEventKinds.ENTRY_MODIFY);
            GLOBALCONFIG_MONITOR.submit(() ->
                watchDir.processEvents()
            );
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> close()));

    }

    private static void close(){
        GLOBALCONFIG_MONITOR.shutdownNow();
    }

    private static final LoadingCache<ConfigKeyEnum, String> configCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.DAYS)
            .refreshAfterWrite(10, TimeUnit.DAYS)
            .build(configKey -> loadConfig(configKey));

    private static String loadConfig(ConfigKeyEnum configKey){
        Properties properties = new Properties();
        File file = new File(CONFIG_PATH);

        try(BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            properties.load(reader);
            return properties.getProperty(configKey.getKey());
        } catch (IOException e) {
            log.error("加载配置文件:{}出错,请检查配置",CONFIG_NAME);
            throw new RuntimeException("加载配置文件:"+CONFIG_NAME+"出错,请检查配置",e);
        }

    }

    private static void refreshConfig(){
        Properties properties = new Properties();
        File file = new File(CONFIG_PATH);
        ConfigKeyEnum[] values = ConfigKeyEnum.values();
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            map.put(values[i].getKey(),i);
        }
        try(BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            properties.load(reader);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                Integer index = map.get(entry.getKey().toString());
                configCache.put(values[index],entry.getValue().toString());
            }
        } catch (IOException e) {
            log.error("加载配置文件:{}出错,请检查配置",CONFIG_NAME);
            throw new RuntimeException("加载配置文件:"+CONFIG_NAME+"出错,请检查配置",e);
        }

    }

    public static void checkConfig() throws FileNotFoundException {
        File file = new File(CONFIG_PATH);
        if (!file.exists()){
            throw new FileNotFoundException("请创建config.properties文件并配置!");
        }
    }

    public static String getConfig(ConfigKeyEnum key){
        return configCache.get(key);
    }




}
