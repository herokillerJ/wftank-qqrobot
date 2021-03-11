package cn.wftank.qqrobot.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author: wftank
 * @create: 2021-03-11 14:30
 * @description: 全局配置定时刷新
 **/
@Slf4j
public class GlobalConfig {

    //bot
    public static final String CONFIG_DIR = "./";
    public static final String CONFIG_NAME = "config.txt";
    public static final String CONFIG_PATH = CONFIG_DIR+CONFIG_NAME;

    private static final LoadingCache<ConfigKeyEnum, String> configCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .build(configKey -> loadConfig(configKey));

    private static String loadConfig(ConfigKeyEnum configKey){
        Properties properties = new Properties();
        File file = new File(CONFIG_PATH);

        try(BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            properties.load(reader);
            return properties.getProperty(configKey.getKey());
        } catch (IOException e) {
            log.error("加载配置文件:{}出错,请检查配置",CONFIG_NAME);
            throw new RuntimeException("加载配置文件:{}出错,请检查配置",e);
        }

    }

    public static void main(String[] args) {
        System.out.println(getConfig(ConfigKeyEnum.PASSWORD));
    }

    public static void checkConfig() throws FileNotFoundException {
        File file = new File(CONFIG_PATH);
        if (!file.exists()){
            throw new FileNotFoundException("请创建config.txt文件并配置!");
        }
    }

    public static String getConfig(ConfigKeyEnum key){
        return configCache.get(key);
    }




}
