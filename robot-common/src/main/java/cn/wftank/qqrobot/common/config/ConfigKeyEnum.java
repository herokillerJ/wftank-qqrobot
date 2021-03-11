package cn.wftank.qqrobot.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @author: wftank
 * @create: 2021-03-11 14:30
 * @description: 配置key枚举
 **/
@Getter
@AllArgsConstructor
public enum ConfigKeyEnum{
        QQ("qq"),
        PASSWORD("password"),
        GROUPS("groups"),
        MIRAI_PROTOCOL("mirai.protocol"),
        //翻译
        TRANSLATE_BAIDU_APPID("translate.baidu.appid"),
        TRANSLATE_BAIDU_SECRET("translate.baidu.secret"),
        ;
        private String key;

    }