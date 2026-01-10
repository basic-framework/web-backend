package com.zl.framework.exception.user;

import com.zl.framework.exception.base.ErrorCode;
import java.io.Serial;

/**
 * 用户密码不正确或不符合规范异常类
 * @Author GuihaoLv
 */
public class UserPasswordNotMatchException extends UserException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserPasswordNotMatchException() {
        super(ErrorCode.USER_PASSWORD_NOT_MATCH);
    }

    public UserPasswordNotMatchException(String message) {
        super(ErrorCode.USER_PASSWORD_NOT_MATCH, message);
    }
}
