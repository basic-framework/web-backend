package com.zl.common.utils;
import java.util.Random;

 /**
 * 验证码生成工具类
 * @Author GuihaoLv
 */
public class VerifyCodeUtil {

     /**
     * 生成验证码
     * @param length 验证码长度
     * @return 验证码
     */
    public static String getVerifyCode(int length){
        StringBuilder sb=new StringBuilder();
        Random random=new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }



}