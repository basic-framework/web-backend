package com.zl.framework.exception.file;

import com.zl.framework.exception.base.BaseServiceException;
import com.zl.framework.exception.base.ErrorCode;
import java.io.Serial;

/**
 * 文件信息异常类
 *
 * @Author GuihaoLv
 */
public class FileException extends BaseServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args) {
        super(ErrorCode.FILE_NOT_FOUND.getCode(), code, args, null, "file");
    }

    public FileException(ErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage(), null, null, "file");
    }

    public FileException(ErrorCode errorCode, Object[] args) {
        super(errorCode.getCode(), errorCode.getMessage(), args, null, "file");
    }

    public FileException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message, null, null, "file");
    }
}
