package com.zl.model.annonation;



import com.zl.model.enums.LimitType;

import java.lang.annotation.*;

/**
 * 限流注解
 * @Author GuihaoLv
 */
//实例 @RateLimiter(time = 60, count = 5, limitType = LimitType.IP) 效果：同一IP 60秒内最多允许5次登录尝试。
@Target(ElementType.METHOD) //表示该注解仅能标注在方法上，用于对具体方法进行限流控制。
@Retention(RetentionPolicy.RUNTIME) //注解在运行时保留，可通过反射机制读取注解信息，实现动态限流逻辑。
@Documented //注解信息会包含在生成的 JavaDoc 中
public @interface RateLimiter {
    public static final String RATE_LIMIT_KEY = "rate_limit";

    /**
     * 限流key
     */
    public String key() default RATE_LIMIT_KEY;
    /**
     * 限流时间,单位秒
     */
    public int time() default 60;

    /**
     * 限流次数
     */
    public int count() default 100;

    /**
     * 限流类型
     */
    public LimitType limitType() default LimitType.DEFAULT;

}