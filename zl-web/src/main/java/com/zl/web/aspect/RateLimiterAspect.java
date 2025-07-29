package com.zl.web.aspect;

/**
 * 限流机制 不直接限制 Redis 存储，而是利用 Redis 的 高性能计数 和 自动过期 特性，实现对应用请求频率的控制。
 * Redis 在此场景中作为 临时数据存储工具，其资源消耗可控且可优化，不会对存储系统造成实质性压力。
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zl.common.utils.IPUtil;
import com.zl.common.utils.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.reflect.MethodSignature;
import com.zl.model.annonation.RateLimiter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 限流处理切面
 * @Author GuihaoLv
 */
@Aspect
@Component
//确保仅当配置项 spring.cache.type=redis 时，切面才会生效
@ConditionalOnProperty(prefix = "spring.cache", name = { "type" }, havingValue = "redis", matchIfMissing = false)
public class RateLimiterAspect
{
    private static final Logger log = LoggerFactory.getLogger(RateLimiterAspect.class);

    private RedisTemplate<Object, Object> redisTemplate; //redis 操作模板，用于执行 Lua 脚本和 Redis 命令

    private RedisScript<Long> limitScript; //限流核心逻辑的 Lua 脚本

    @Autowired
    public void setRedisTemplate1(RedisTemplate<Object, Object> redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setLimitScript(RedisScript<Long> limitScript)
    {
        this.limitScript = limitScript;
    }
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) throws Throwable
    {
        //1. 获取注解参数
        int time = rateLimiter.time(); //时间窗口（秒）
        int count = rateLimiter.count();// 允许的请求次数

        //2. 生成唯一限流 Key
        String combineKey = getCombineKey(rateLimiter, point);
        List<Object> keys = Collections.singletonList(combineKey);
        try
        {
            //3. 以key为参数执行 Lua 脚本（原子性操作） keys:Redis 存储的 Key
            //Lua脚本会检查Key是否存在，如果不存在则创建并设置过期时间，如果存在则递增计数器。
            Long number = redisTemplate.execute(limitScript, keys, count, time);
            if (StringUtils.isEmpty(number) || number.intValue() > count)
            {
                throw new Exception("访问过于频繁，请稍候再试");
            }
            log.info("限制请求'{}',当前请求'{}',缓存key'{}'", count, number.intValue(), combineKey);
        } catch (RuntimeException e)
        {
            throw new RuntimeException("服务器限流异常，请稍候再试");
        } catch (Exception e)
        {
            throw e;
        }
    }


    //IP + 类名 + 方法名 的拼接方式，确保不同场景的Key不冲突。
    //Key结构清晰，便于调试和监控（如通过Redis直接查看计数器）。
    public String getCombineKey(RateLimiter rateLimiter, JoinPoint point)
    {
        StringBuffer stringBuffer = new StringBuffer(rateLimiter.key());

        switch (rateLimiter.limitType()) {
            case IP:
                try {
                    limitByIp(stringBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case USER:
                limitByUser(stringBuffer);
                break;
            case DEFAULT:
                limitByDefault(stringBuffer,point);
                break;
        }

        return stringBuffer.toString();
    }


    /**
     * 按IP限流
     * @param stringBuffer
     */
    private void limitByIp(StringBuffer stringBuffer) throws IOException, InterruptedException {
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String ip = IPUtil.getIpAddr(request);
            stringBuffer.append(":ip:").append(ip);
        }


    /**
     * 全局限流
     * @param stringBuffer
     * @param point
     */
    private void limitByDefault(StringBuffer stringBuffer, JoinPoint point) {
        //按方法限流：拼接类名 + 方法名
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        stringBuffer.append(targetClass.getName()).append("-").append(method.getName());

    }


    /**
     * 按用户限流
     * @param stringBuffer
     */
    private void limitByUser(StringBuffer stringBuffer) {
        //获取当前用户
        Long userId= UserUtil.getUserId();
        stringBuffer.append(":user:").append(userId);
    }


}