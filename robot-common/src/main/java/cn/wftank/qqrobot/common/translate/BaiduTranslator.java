package cn.wftank.qqrobot.common.translate;

import cn.wftank.qqrobot.common.config.ConfigKeyEnum;
import cn.wftank.qqrobot.common.config.GlobalConfig;
import cn.wftank.qqrobot.common.model.vo.translate.BaiduTranslateResp;
import cn.wftank.qqrobot.common.model.vo.translate.TransResultItem;
import cn.wftank.qqrobot.common.util.JsonUtil;
import cn.wftank.qqrobot.common.util.OKHttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: wftank
 * @create: 2021-03-11 14:27
 * @description: 百度翻译
 **/
@Component
@Slf4j
public class BaiduTranslator implements TranslatorApi {

    private static final String URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    private static final String LINE_SEPARATOR = "➹";

    @Override
    public String translate(String source, String from, String to) {
        String result = "";
        //判断是否需要翻译
        if (StringUtils.isBlank(GlobalConfig.getConfig(ConfigKeyEnum.TRANSLATE_BAIDU_APPID))
        || StringUtils.isBlank(GlobalConfig.getConfig(ConfigKeyEnum.TRANSLATE_BAIDU_SECRET))){
            return result;
        }

        if (log.isDebugEnabled()){
            log.debug("translate source:{},from:{},to:{}",source,from,to);
        }
        Map<String, String> map = buildRequestBody(source, from, to);
        if (log.isDebugEnabled()){
            log.debug("translate source:{} request body:{}",JsonUtil.toPrettyJson(map));
        }
        //百度翻译会以换行符分隔,返回list,这里再拼回换行符
        BaiduTranslateResp resp = OKHttpUtil.post(URL, map, new TypeReference<BaiduTranslateResp>() {
        });
        if (null != resp.getTransResult() && !resp.getTransResult().isEmpty()){
            result = resp.getTransResult().stream().map(TransResultItem::getDst).collect(Collectors.joining("\n"));
        }
        return result;
    }

    @Override
    public List<String> batchTranslate(List<String> sourceList, String from, String to) {
        List result = new ArrayList();
        //判断是否需要翻译
        if (StringUtils.isBlank(GlobalConfig.getConfig(ConfigKeyEnum.TRANSLATE_BAIDU_APPID))
                || StringUtils.isBlank(GlobalConfig.getConfig(ConfigKeyEnum.TRANSLATE_BAIDU_SECRET))){
            return result;
        }
        if (log.isDebugEnabled()){
            log.debug("translate source:{},from:{},to:{}",sourceList,from,to);
        }
        Map<String, String> map = buildBatchRequestBody(sourceList, from, to);
        if (log.isDebugEnabled()){
            log.debug("translate source:{} request body:{}",JsonUtil.toPrettyJson(map));
        }
        //百度翻译会以换行符分隔,返回list,这里再拼回换行符
        BaiduTranslateResp resp = OKHttpUtil.post(URL, map, new TypeReference<BaiduTranslateResp>() {
        });
        if (null != resp.getTransResult() && !resp.getTransResult().isEmpty()){
            List<TransResultItem> transResult = resp.getTransResult();
            result = transResult.stream().map(transResultItem -> transResultItem.getDst().replaceAll(LINE_SEPARATOR,"\n"))
                    .collect(Collectors.toList());
        }
        return result;
    }

    private Map<String, String> buildBatchRequestBody(List<String> sourceList, String from, String to) {
        String formatStr = sourceList
                .stream()
                .map(str -> str.replaceAll("\n", LINE_SEPARATOR))
                .collect(Collectors.joining("\n"));
        return buildRequestBody(formatStr,from,to);
    }

    private Map<String, String> buildRequestBody(String source, String from, String to) {
        Map<String, String> map = new HashMap<>();
        String appId = GlobalConfig.getConfig(ConfigKeyEnum.TRANSLATE_BAIDU_APPID);
        String secret = GlobalConfig.getConfig(ConfigKeyEnum.TRANSLATE_BAIDU_SECRET);
        String salt = String.valueOf(System.currentTimeMillis());
        String signSource = appId+ source +salt+secret;
        String sign = DigestUtils.md5Hex(signSource).toLowerCase();
        map.put("q", source);
        map.put("from", from);
        map.put("to", to);
        map.put("appid",appId);
        map.put("salt",salt);
        map.put("sign",sign);
        map.put("action","1");
        return map;
    }

}
