package com.zl.framework.exception.service;

import com.zl.framework.exception.base.BaseServiceException;
import com.zl.framework.exception.base.ErrorCode;

import java.io.Serial;

/**
 * 演示模式异常
 * @Author GuihaoLv
 */
public class DemoModeException extends BaseServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DemoModeException() {
        super(ErrorCode.DEMO_MODE_ERROR);
    }

    public DemoModeException(String message) {
        super(ErrorCode.DEMO_MODE_ERROR.getCode(), message);
    }
}
