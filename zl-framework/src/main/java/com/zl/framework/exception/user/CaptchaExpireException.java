package com.zl.framework.exception.user;

import com.zl.framework.exception.base.ErrorCode;
import java.io.Serial;

/**
 * 验证码失效异常类
 *
 * @Author GuihaoLv
 */
public class CaptchaExpireException extends UserException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CaptchaExpireException() {
        super(ErrorCode.USER_CAPTCHA_EXPIRE);
    }

    public CaptchaExpireException(String message) {
        super(ErrorCode.USER_CAPTCHA_EXPIRE, message);
    }
}
