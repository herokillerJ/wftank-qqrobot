package cn.wftank.qqrobot.app.finder;

import cn.wftank.qqrobot.app.model.vo.JsonProductVO;
import cn.wftank.qqrobot.common.util.OKHttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SCDataFinder {

    private List<IndexEntity> indexList = new CopyOnWriteArrayList<>();

    private static final String DEFAULT_VERSION = "latest";
    private static final String URL_PREFIX = "https://cdn.jsdelivr.net/gh/herokillerJ/starcitizen-data@";
    private final LoadingCache<String, JsonProductVO> productCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .build(path -> loadProductInfo(path));

    @Scheduled(fixedDelay = 1000*60*60)
    private void load(){
        String indexUrl = URL_PREFIX+getVersion()+"/index.json";
        String extIndexUrl = URL_PREFIX+getVersion()+"/ext_index.json";
        Index index = OKHttpUtil.get(indexUrl, new TypeReference<Index>() {});
        List<IndexEntity> mainList = index.getIndex();
        Index extIndex = OKHttpUtil.get(extIndexUrl, new TypeReference<Index>() {});
        mainList.addAll(extIndex.getIndex());
        indexList.clear();
        indexList.addAll(mainList);
    }

    public List<IndexEntity> search(String keyword){
        List<IndexEntity> result = new CopyOnWriteArrayList<>();
        boolean exact = false;
        if (keyword.startsWith("{") && keyword.endsWith("}")){
            exact = true;
            keyword = keyword.substring(1,keyword.length()-1);
        }
        boolean finalExact = exact;
        String finalKeyword = keyword;
        indexList.parallelStream().forEach(indexEntity -> {
            if (finalExact){
                if (indexEntity.getName().equalsIgnoreCase(finalKeyword.toLowerCase())
                        || indexEntity.getNameCn().equals(finalKeyword)){
                    result.add(indexEntity);
                }
            }else{
                if (indexEntity.getName().toLowerCase().contains(finalKeyword.toLowerCase())
                        || indexEntity.getNameCn().contains(finalKeyword)){
                    result.add(indexEntity);
                }
            }

        });
        return result;
    }

    public String getVersion(){
        try {
            File file = new File("./sc_database_version");
            if (!file.exists()){
                Files.createFile(file.toPath());
            }
            String version = Files.readString(file.toPath());
            if (StringUtils.isNotBlank(version)){
                return version;
            }
        }catch (IOException e){
            log.error("get sc_database_version ex"+ ExceptionUtils.getStackTrace(e));
        }
        return DEFAULT_VERSION;
    }

    public JsonProductVO getProductInfo(String path){
        return productCache.get(path);
    }

    public JsonProductVO loadProductInfo(String path){
        String url = URL_PREFIX+getVersion()+"/"+path;
        JsonProductVO productVO = OKHttpUtil.get(url, new TypeReference<JsonProductVO>() {
        });
        return productVO;
    }
}
