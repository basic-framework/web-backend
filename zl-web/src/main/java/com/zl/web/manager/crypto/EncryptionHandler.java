package com.zl.web.manager.crypto;

import com.zl.model.annonation.EncryptField;
import com.zl.common.utils.encrypUtils.AESUtil;
import java.lang.reflect.Field;


/**
 * 加解密处理器
 * @Author GuihaoLv
 */
public class EncryptionHandler {


    public static void encrypt(Object object) {
        handle(object, true);
    }


    public static void decrypt(Object object) {
        handle(object, false);
    }



    private static void handle(Object object, boolean encrypt) {
        if (object == null) return;

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(EncryptField.class) && field.getType() == String.class) {
                field.setAccessible(true);
                try {
                    String value = (String) field.get(object);
                    if (value != null) {
                        String processed = encrypt ? AESUtil.encrypt(value) : AESUtil.decrypt(value);
                        field.set(object, processed);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("字段加解密失败", e);
                }
            }
        }
    }




}
