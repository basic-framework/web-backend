package com.zl.framework.exception.service;

import com.zl.framework.exception.base.BaseServiceException;
import com.zl.framework.exception.base.ErrorCode;

/**
 * 认证异常类
 * @Author GuihaoLv
 */
public class AuthException extends BaseServiceException {

    private static final long serialVersionUID = 1L;

    public AuthException(String message) {
        super(ErrorCode.UNAUTHORIZED.getCode(), message);
    }

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    public AuthException(ErrorCode errorCode, Object[] args) {
        super(errorCode, args);
    }
}
