package com.zl.sensitive.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zl.sensitive.core.SensitiveStrategy;
import com.zl.sensitive.handler.SensitiveHandler;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据脱敏注解（标记需要脱敏的字段）
 * @Author: GuihaoLV
 */
@Retention(RetentionPolicy.RUNTIME)  // 运行时生效，允许反射解析
@Target(ElementType.FIELD)           // 仅作用于实体类字段
@JacksonAnnotationsInside            // 告诉Jackson：这是一个内部注解，需解析其绑定的序列化器
@JsonSerialize(using = SensitiveHandler.class)  // 指定该字段的序列化器为SensitiveHandler
public @interface Sensitive {
    SensitiveStrategy strategy();    // 必选：指定脱敏策略（手机号/身份证等）
}