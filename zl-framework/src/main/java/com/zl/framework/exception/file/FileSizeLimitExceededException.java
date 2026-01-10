package com.zl.framework.exception.file;

import com.zl.framework.exception.base.ErrorCode;
import java.io.Serial;

/**
 * 文件名大小限制异常类
 *
 * @Author GuihaoLv
 */
public class FileSizeLimitExceededException extends FileException {

    @Serial
    private static final long serialVersionUID = 1L;

    public FileSizeLimitExceededException(long defaultMaxSize) {
        super(ErrorCode.FILE_SIZE_EXCEED, new Object[]{defaultMaxSize});
    }

    public FileSizeLimitExceededException(String message) {
        super(ErrorCode.FILE_SIZE_EXCEED, message);
    }
}
