package com.zl.idempotent.aspectj;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.zl.common.constant.GlobalConstants;
import com.zl.common.context.UserThreadLocal;
import com.zl.common.result.Result;
import com.zl.common.utils.jsonUtils.JsonUtils;
import com.zl.common.utils.springUtils.ServletUtils;
import com.zl.framework.exception.base.BaseServiceException;
import com.zl.framework.exception.base.ErrorCode;
import com.zl.idempotent.annotation.RepeatSubmit;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;

/**
 * 防止重复提交(参考美团GTIS防重系统)
 * @Author GuihaoLv
 */
@Aspect
@Component
@Slf4j
public class RepeatSubmitAspect {

    private static final ThreadLocal<String> KEY_CACHE = new ThreadLocal<>();
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Before("@annotation(repeatSubmit)")
    public void doBefore(JoinPoint point, RepeatSubmit repeatSubmit){
        // 1. 校验并转换间隔时间（最小1秒，避免配置过小）
        long interval = repeatSubmit.interval() > 0 ? repeatSubmit.timeUnit().toMillis(repeatSubmit.interval()) : 0;
        if (interval < 1000) {
            throw new BaseServiceException(ErrorCode.PARAM_ERROR, "重复提交间隔时间不能小于'1'秒");
        }

        // 2. 构建请求唯一标识（核心：用户标识+请求参数+接口地址）
        HttpServletRequest request = ServletUtils.getRequest();
        // 2.1 拼接请求参数（过滤MultipartFile、Request/Response等无效参数）
        String nowParams = argsArrayToString(point.getArgs());
        // 2.2 获取接口地址（作为key的一部分）
        String url = request.getRequestURI();
        // 2.3 获取用户唯一标识（从ThreadLocal中获取用户ID，确保不同用户隔离）
        Long userId = UserThreadLocal.getUserId();
        String submitKey = userId != null ? String.valueOf(userId) : "anonymous";
        // 2.4 生成唯一哈希值（避免参数明文存储，提高安全性）
        submitKey = SecureUtil.md5(submitKey + ":" + nowParams);
        // 2.5 构建Redis的key（前缀+接口地址+哈希值）
        String cacheRepeatKey = GlobalConstants.REPEAT_SUBMIT_KEY + url + submitKey;

        // 3. Redis判断是否重复提交
        String key = (String) redisTemplate.opsForValue().get(cacheRepeatKey);
        if (key == null) {
            // 首次请求：存入Redis，设置过期时间（间隔时间）
            redisTemplate.opsForValue().set(cacheRepeatKey, "", Duration.ofMillis(interval));
            KEY_CACHE.set(cacheRepeatKey); // 线程本地存储key，供后续操作使用
            log.debug("幂等性校验通过，key: {}", cacheRepeatKey);
        } else {
            // 重复请求：读取提示语（支持国际化）并抛出异常
            String message = repeatSubmit.message();
            if (StringUtils.startsWith(message, "{") && StringUtils.endsWith(message, "}")) {
                message = com.zl.common.utils.commonUtils.MessageUtils.message(
                    StringUtils.substring(message, 1, message.length() - 1));
            }
            log.warn("幂等性校验失败，重复提交，key: {}", cacheRepeatKey);
            throw new BaseServiceException(ErrorCode.REPEAT_SUBMIT, message);
        }
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(repeatSubmit)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, RepeatSubmit repeatSubmit, Object jsonResult) {
        if (jsonResult instanceof Result) {
            try {
                Result<?> result = (Result<?>) jsonResult;
                // 成功则不删除redis数据 保证在有效时间内无法重复提交
                if (result.getCode() != null && result.getCode() == 200) {
                    log.debug("幂等性校验：请求成功，保留key: {}", KEY_CACHE.get());
                    return;
                }
                // 失败则删除redis数据，允许重试
                redisTemplate.delete(KEY_CACHE.get());
                log.debug("幂等性校验：请求失败，删除key: {}", KEY_CACHE.get());
            } finally {
                KEY_CACHE.remove();
            }
        }
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(repeatSubmit)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, RepeatSubmit repeatSubmit, Exception e) {
        // 异常时删除redis数据，允许重试
        redisTemplate.delete(KEY_CACHE.get());
        KEY_CACHE.remove();
        log.debug("幂等性校验：发生异常，删除key: {}", KEY_CACHE.get());
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (ObjectUtil.isNotNull(o) && !isFilterObject(o)) {
                    try {
                        params.append(JsonUtils.toJsonString(o)).append(" ");
                    } catch (Exception e) {
                        log.warn("参数序列化失败", e);
                    }
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
