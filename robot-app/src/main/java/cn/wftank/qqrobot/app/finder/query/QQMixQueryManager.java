package cn.wftank.qqrobot.app.finder.query;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author: wftank
 * @create: 2021-06-23 16:58
 * @description: QQ符合查询管理器
 **/
@Component
public class QQMixQueryManager {

    private final Long expireSecond = 5L;

    /**
     * 通过cache控制session的过期时间等
     */
    private final Cache<Long, QQMixQuerySession> sessionCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(expireSecond, TimeUnit.MINUTES)
            .build();

    /**
     * 获取QQ号当前的查询会话
     * 如果不存在则返回null
     * @param qq
     * @return
     */
    public QQMixQuerySession get(Long qq){
        return sessionCache.getIfPresent(qq);
    }

    /**
     * 给对应QQ添加查询会话
     * @param qq
     * @param querySession
     */
    public void put(Long qq, QQMixQuerySession querySession){
        sessionCache.put(qq,querySession);
    }

    /**
     * 清除qq对应的查询会话
     * @param qq
     * @return
     */
    public QQMixQuerySession remove(Long qq){
        QQMixQuerySession session = sessionCache.getIfPresent(qq);
        if (null != session){
            sessionCache.invalidate(qq);
        }
        return session;
    }

}
