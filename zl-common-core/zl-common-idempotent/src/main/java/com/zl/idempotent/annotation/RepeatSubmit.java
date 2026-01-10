package com.zl.idempotent.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 自定义注解防止表单重复提交
 * @Author GuihaoLv
 */
@Inherited // 允许子类继承该注解
@Target(ElementType.METHOD) // 仅作用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时生效（AOP需读取）
@Documented // 生成文档时包含该注解
public @interface RepeatSubmit {
    // 防重间隔时间（默认5000ms），小于该时间的重复请求会被拦截
    int interval() default 5000;
    // 时间单位（默认毫秒），配合interval使用
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
    // 重复提交提示语（支持国际化，格式{code}）
    String message() default "{repeat.submit.message}";
}