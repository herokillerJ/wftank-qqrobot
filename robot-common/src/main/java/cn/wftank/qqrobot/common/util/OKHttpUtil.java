package cn.wftank.qqrobot.common.util;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.internal.Util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: jiawei
 * @create: 2021-01-07 16:59
 * @description: OKhttp工具类
 **/
public class OKHttpUtil {
    private static final OkHttpClient okHttpClient;
    private static final int MAX_REQUEST_PER_HOST = 100;
    private static final int MAX_REQUEST = 100;
    private static final int REQUEST_TIMEOUT = 10;

    static{
        //媒体数量
        //异步请求线程池,同步不用这个
        ExecutorService executorService = new ThreadPoolExecutor(MAX_REQUEST_PER_HOST, MAX_REQUEST, 30, TimeUnit.SECONDS,
                new SynchronousQueue<>(), Util.threadFactory("MediaOauth-OkHttp-Dispatcher", false));

        Dispatcher dispatcher = new Dispatcher(executorService);
        dispatcher.setMaxRequests(MAX_REQUEST);
        dispatcher.setMaxRequestsPerHost(MAX_REQUEST_PER_HOST);

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .build();
    }


}
