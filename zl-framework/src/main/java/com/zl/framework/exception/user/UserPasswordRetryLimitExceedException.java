package com.zl.framework.exception.user;

import com.zl.framework.exception.base.ErrorCode;
import java.io.Serial;

/**
 * 用户错误最大次数异常类
 *
 * @Author GuihaoLv
 */
public class UserPasswordRetryLimitExceedException extends UserException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserPasswordRetryLimitExceedException(int retryLimitCount, int lockTime) {
        super(ErrorCode.USER_PASSWORD_RETRY_LIMIT_EXCEED, new Object[]{retryLimitCount, lockTime});
    }

    public UserPasswordRetryLimitExceedException(String message) {
        super(ErrorCode.USER_PASSWORD_RETRY_LIMIT_EXCEED, message);
    }
}
