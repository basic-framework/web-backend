package com.zl.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * AES加密工具类
 * @Author GuihaoLv
 */
public class AESUtil {
    private static final String KEY = "1234567890abcdef"; // 16位秘钥
    private static final String ALGORITHM = "AES";

    /**
     * 加密
     * @param content
     * @return
     */
    public static String encrypt(String content) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] result = cipher.doFinal(content.getBytes());
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception ex) {
            throw new RuntimeException("加密失败", ex);
        }
    }

    /**
     * 解密
     * @param content
     * @return
     */
    public static String decrypt(String content) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] result = cipher.doFinal(Base64.getDecoder().decode(content));
            return new String(result);
        } catch (Exception ex) {
            throw new RuntimeException("解密失败", ex);
        }
    }
}
