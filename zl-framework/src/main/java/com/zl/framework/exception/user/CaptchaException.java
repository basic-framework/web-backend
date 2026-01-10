package com.zl.framework.exception.user;

import com.zl.framework.exception.base.ErrorCode;
import java.io.Serial;

/**
 * 验证码错误异常类
 *
 * @Author GuihaoLv
 */
public class CaptchaException extends UserException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CaptchaException() {
        super(ErrorCode.USER_CAPTCHA_ERROR);
    }

    public CaptchaException(String message) {
        super(ErrorCode.USER_CAPTCHA_ERROR, message);
    }
}
