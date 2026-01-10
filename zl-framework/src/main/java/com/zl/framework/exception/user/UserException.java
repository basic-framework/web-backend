package com.zl.framework.exception.user;

import com.zl.framework.exception.base.BaseServiceException;
import com.zl.framework.exception.base.ErrorCode;
import java.io.Serial;

/**
 * 用户信息异常类
 *
 * @Author GuihaoLv
 */
public class UserException extends BaseServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object... args) {
        super(ErrorCode.USER_NOT_FOUND.getCode(), code, args, null, "user");
    }

    public UserException(ErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage(), null, null, "user");
    }

    public UserException(ErrorCode errorCode, Object[] args) {
        super(errorCode.getCode(), errorCode.getMessage(), args, null, "user");
    }

    public UserException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message, null, null, "user");
    }
}
