package com.zl.model.annonation;

import java.lang.annotation.*;

 /**
 * 标识加密字段注解
 * @Author GuihaoLv
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EncryptField {
}