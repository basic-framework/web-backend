package com.zl.common.utils.validateUtils;

 /**
 * 正则表达式校验工具类
 * @Author GuihaoLv
 */
public class ValidatorUtil {

    /**
     * 校验邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * 校验手机号格式
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        // 简单校验，仅允许11位数字
        String phoneRegex = "^1[3-9]\\d{9}$";
        return phone.matches(phoneRegex);
    }
}