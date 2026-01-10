package com.zl.framework.exception.service;

import com.zl.framework.exception.base.BaseServiceException;
import com.zl.framework.exception.base.ErrorCode;

import java.io.Serial;

/**
 * 工具类异常
 * @Author GuihaoLv
 */
public class UtilException extends BaseServiceException {

    @Serial
    private static final long serialVersionUID = 8247610319171014183L;

    public UtilException(Throwable e) {
        super(ErrorCode.UTIL_ERROR.getCode(), e.getMessage(), null, null, null);
        this.initCause(e);
    }

    public UtilException(String message) {
        super(ErrorCode.UTIL_ERROR.getCode(), message);
    }

    public UtilException(String message, Throwable throwable) {
        super(ErrorCode.UTIL_ERROR.getCode(), message, null, null, null);
        this.initCause(throwable);
    }

    public UtilException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UtilException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }
}
