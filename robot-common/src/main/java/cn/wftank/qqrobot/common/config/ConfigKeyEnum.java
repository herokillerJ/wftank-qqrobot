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
        SC_DB_VERSION("sc.database.version"),
        MIRAI_PROTOCOL("mirai.protocol"),
        MIRAI_PROTOCOL_VERSION("mirai.protocol.version"),
        //翻译
        TRANSLATE_BAIDU_APPID("translate.baidu.appid"),
        TRANSLATE_BAIDU_SECRET("translate.baidu.secret"),
        //discord,
        DISCORD_CHANNEL_IDS("discord.channel.ids"),
        //搜索引擎
        INDEX_FILE_PATH("search.index.dir"),
        ANALYZER_CONFIG_PATH("search.analyzer.dir"),
        COMMAND_DIR("command.dir"),
        ;
        private String key;

    }