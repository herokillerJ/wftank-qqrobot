package cn.wftank.qqrobot.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.*;
import okhttp3.internal.Util;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: wftank
 * @create: 2021-01-07 16:59
 * @description: OKhttp工具类
 **/
public class OKHttpUtil {
    private static final OkHttpClient client;
    private static final int MAX_REQUEST_PER_HOST = 100;
    private static final int MAX_REQUEST = 100;
    private static final int REQUEST_TIMEOUT = 30;

    public static final MediaType JSON_MEDIA_TYPE
            = MediaType.parse("application/json; charset=utf-8");

    private static final Logger log = LoggerFactory.getLogger(OKHttpUtil.class);

    static{
        //媒体数量
        //异步请求线程池,同步不用这个
        ExecutorService executorService = new ThreadPoolExecutor(MAX_REQUEST_PER_HOST, MAX_REQUEST, 30, TimeUnit.SECONDS,
                new SynchronousQueue<>(), Util.threadFactory("Default-OkHttp-Dispatcher", false));

        Dispatcher dispatcher = new Dispatcher(executorService);
        dispatcher.setMaxRequests(MAX_REQUEST);
        dispatcher.setMaxRequestsPerHost(MAX_REQUEST_PER_HOST);
        client = new OkHttpClient.Builder()
//                .proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1",7890)))
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .build();
    }

    public static <T> T postJson(String url, Object jsonBody, TypeReference<T> typeReference) {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, JsonUtil.toJson(jsonBody)); // new
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String respStr = response.body().string();
           if (log.isDebugEnabled()){
               log.debug("resp:"+respStr);
           }
            return JsonUtil.parseJson(respStr,typeReference);
        } catch (IOException e) {
            log.error("request exception:"+ ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    public static <T> T post(String url, Map<String,String> bodyMap, TypeReference<T> typeReference) {
        FormBody.Builder builder = new FormBody.Builder();
        bodyMap.forEach((key,val) -> builder.add(key,val));
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String respStr = response.body().string();
            if (log.isDebugEnabled()){
                log.debug("resp:"+respStr);
            }
            return JsonUtil.parseJson(respStr,typeReference);
        } catch (IOException e) {
            log.error("request exception:"+ ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    public static String post(String url, Map<String,String> bodyMap) {
        FormBody.Builder builder = new FormBody.Builder();
        bodyMap.forEach((key,val) -> builder.add(key,val));
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String respStr = response.body().string();
            if (log.isDebugEnabled()){
                log.debug("resp:"+respStr);
            }
            return respStr;
        } catch (IOException e) {
            log.error("request exception:"+ ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    public static <T> T get(String url,TypeReference<T> typeReference) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String respStr = response.body().string();
            if (log.isDebugEnabled()){
                log.debug("resp:"+respStr);
            }
            return JsonUtil.parseJson(respStr,typeReference);
        } catch (IOException e) {
            log.error("request exception:"+ ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    public static File getFile(String url, String filePath) {
        File file = new File(filePath);
        if (file.exists()){
            log.warn("file:{} will be replace!",file.getAbsolutePath());
        }else if(file.isDirectory()) {
            throw new IllegalArgumentException("please support a file path");
        }else{
            try {
                File dir = file.getParentFile();
                if (!dir.exists()) dir.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                log.error("download file ex:"+ExceptionUtils.getStackTrace(e));
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            Files.copy(response.body().byteStream(),file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return file;
        } catch (IOException e) {
            log.error("request exception:"+ ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

}
