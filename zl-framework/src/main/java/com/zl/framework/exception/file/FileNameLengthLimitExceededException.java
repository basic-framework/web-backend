package com.zl.framework.exception.file;

import com.zl.framework.exception.base.ErrorCode;
import java.io.Serial;

/**
 * 文件名称超长限制异常类
 *
 * @Author GuihaoLv
 */
public class FileNameLengthLimitExceededException extends FileException {

    @Serial
    private static final long serialVersionUID = 1L;

    public FileNameLengthLimitExceededException(int defaultFileNameLength) {
        super(ErrorCode.FILE_NAME_LENGTH_EXCEED, new Object[]{defaultFileNameLength});
    }

    public FileNameLengthLimitExceededException(String message) {
        super(ErrorCode.FILE_NAME_LENGTH_EXCEED, message);
    }
}
