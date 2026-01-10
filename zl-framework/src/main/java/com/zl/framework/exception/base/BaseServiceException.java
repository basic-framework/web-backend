package com.zl.framework.exception.base;

import com.zl.common.utils.commonUtils.MessageUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import java.io.Serial;

/**
 * 统一业务异常基类
 * @Author GuihaoLv
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 错误码对应的参数
     */
    private Object[] args;

    /**
     * 详细错误信息，内部调试使用
     */
    private String detailMessage;

    /**
     * 所属模块
     */
    private String module;

    public BaseServiceException(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage(), null, null, null);
    }

    public BaseServiceException(ErrorCode errorCode, Object[] args) {
        this(errorCode.getCode(), errorCode.getMessage(), args, null, null);
    }

    public BaseServiceException(ErrorCode errorCode, String message) {
        this(errorCode.getCode(), message, null, null, null);
    }

    public BaseServiceException(Integer code, String message) {
        this(code, message, null, null, null);
    }

    public BaseServiceException(Integer code, String message, Object[] args) {
        this(code, message, args, null, null);
    }

    public BaseServiceException(Integer code, String message, Object[] args, String detailMessage) {
        this(code, message, args, detailMessage, null);
    }

    public BaseServiceException(Integer code, String message, Object[] args, String detailMessage, String module) {
        this.code = code;
        this.message = message;
        this.args = args;
        this.detailMessage = detailMessage;
        this.module = module;
    }

    public BaseServiceException(String message) {
        this(ErrorCode.SYSTEM_ERROR.getCode(), message, null, null, null);
    }

    public BaseServiceException(String message, Throwable cause) {
        this(ErrorCode.SYSTEM_ERROR.getCode(), message, null, null, null);
        this.initCause(cause);
    }

    public BaseServiceException(ErrorCode errorCode, Throwable cause) {
        this(errorCode.getCode(), errorCode.getMessage(), null, null, null);
        this.initCause(cause);
    }

    /**
     * 获取格式化后的错误消息
     * @return 格式化后的错误消息
     */
    @Override
    public String getMessage() {
        if (StringUtils.isNotEmpty(message)) {
            return message;
        }
        return ErrorCode.SYSTEM_ERROR.getMessage();
    }

    /**
     * 获取国际化错误消息
     * @return 国际化错误消息
     */
    public String getI18nMessage() {
        if (args != null && args.length > 0) {
            return MessageUtils.message(String.valueOf(code), args);
        }
        return getMessage();
    }

    /**
     * 设置错误码
     * @param code 错误码
     * @return 当前异常实例
     */
    public BaseServiceException setCode(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * 设置错误消息
     * @param message 错误消息
     * @return 当前异常实例
     */
    public BaseServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 设置错误参数
     * @param args 错误参数
     * @return 当前异常实例
     */
    public BaseServiceException setArgs(Object[] args) {
        this.args = args;
        return this;
    }

    /**
     * 设置详细错误信息
     * @param detailMessage 详细错误信息
     * @return 当前异常实例
     */
    public BaseServiceException setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }

    /**
     * 设置所属模块
     * @param module 所属模块
     * @return 当前异常实例
     */
    public BaseServiceException setModule(String module) {
        this.module = module;
        return this;
    }
}