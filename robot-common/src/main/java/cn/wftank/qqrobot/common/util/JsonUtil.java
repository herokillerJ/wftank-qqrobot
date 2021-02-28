package cn.wftank.qqrobot.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    public static final ObjectMapper MAPPER;

    static {
        MAPPER = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false)
                .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .build();
    }

    public static String toJson(Object obj){
        if (obj != null){
            try {
               return  MAPPER.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    public static <T> T parseJson(String json, TypeReference<T> typeReference){
        if (StringUtils.isNoneBlank(json)){
            try {
                return MAPPER.readValue(json,typeReference);
            } catch (JsonProcessingException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    public static <T> T parseJson(String json, Class<T> clazz){
        if (StringUtils.isNoneBlank(json)){
            try {
                return MAPPER.readValue(json,clazz);
            } catch (JsonProcessingException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }
}
