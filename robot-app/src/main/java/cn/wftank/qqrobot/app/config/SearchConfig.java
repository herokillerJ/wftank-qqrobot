package cn.wftank.qqrobot.app.config;

import cn.wftank.qqrobot.app.finder.Index;
import cn.wftank.qqrobot.app.finder.IndexEntity;
import cn.wftank.qqrobot.app.finder.SCDataFinder;
import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.qqrobot.common.util.JsonUtil;
import cn.wftank.qqrobot.common.util.OKHttpUtil;
import cn.wftank.search.WFtankSearcher;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SearchConfig {


    @Bean
    public WFtankSearcher wFtankSearcher(){
        String indexUrl = SCDataFinder.URL_PREFIX+ GlobalConfig.getConfig(ConfigKeyEnum.SC_DB_VERSION) +"/index.json";
        String extIndexUrl = SCDataFinder.URL_PREFIX+GlobalConfig.getConfig(ConfigKeyEnum.SC_DB_VERSION)+"/ext_index.json";
        Index index = OKHttpUtil.get(indexUrl, new TypeReference<Index>() {});
        List<IndexEntity> indexList = index.getIndex();
        Index extIndex = OKHttpUtil.get(extIndexUrl, new TypeReference<Index>() {});
        indexList.addAll(extIndex.getIndex());
        String indexPath = GlobalConfig.getConfig(ConfigKeyEnum.INDEX_FILE_PATH);
        String analizerPath = GlobalConfig.getConfig(ConfigKeyEnum.ANALYZER_CONFIG_PATH);
        List<String> indexJsonList = indexList.stream().map(JsonUtil::toJson).collect(Collectors.toList());
        return new WFtankSearcher(indexPath , analizerPath,indexJsonList);
    }
}
