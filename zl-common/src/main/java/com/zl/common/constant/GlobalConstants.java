package com.zl.common.constant;

public class GlobalConstants {

    /**
     * 全局 redis key (业务无关的key)
     */
    public static final String GLOBAL_REDIS_KEY = "global:";
    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = GLOBAL_REDIS_KEY + "repeat_submit:";


//
//    /**
//     * 验证码 redis key
//     */
//    String CAPTCHA_CODE_KEY = GLOBAL_REDIS_KEY + "captcha_codes:";
//
//    /**
//     * 限流 redis key
//     */
//    String RATE_LIMIT_KEY = GLOBAL_REDIS_KEY + "rate_limit:";
//
//    /**
//     * 登录账户密码错误次数 redis key
//     */
//    String PWD_ERR_CNT_KEY = GLOBAL_REDIS_KEY + "pwd_err_cnt:";


}
