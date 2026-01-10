package com.zl.framework.exception.base;

/**
 * 统一错误码枚举
 * @Author GuihaoLv
 */
public enum ErrorCode {

    // 通用错误码 10000-19999
    SUCCESS(200, "操作成功"),
    SYSTEM_ERROR(10000, "系统异常"),
    PARAM_ERROR(10001, "参数错误"),
    PARAM_MISSING(10002, "缺少必要参数"),
    PARAM_FORMAT_ERROR(10003, "参数格式错误"),
    REQUEST_METHOD_ERROR(10004, "请求方法错误"),
    MEDIA_TYPE_ERROR(10005, "媒体类型错误"),
    DATA_NOT_FOUND(10006, "数据不存在"),
    DATA_ALREADY_EXISTS(10007, "数据已存在"),
    OPERATION_FAILED(10008, "操作失败"),
    
    // 认证授权错误码 20000-29999
    UNAUTHORIZED(20001, "未认证"),
    FORBIDDEN(20002, "无权限"),
    TOKEN_INVALID(20003, "Token无效"),
    TOKEN_EXPIRED(20004, "Token已过期"),
    LOGIN_FAILED(20005, "登录失败"),
    USER_DISABLED(20006, "用户已禁用"),
    USER_LOCKED(20007, "用户已锁定"),
    
    // 用户相关错误码 30000-39999
    USER_NOT_FOUND(30001, "用户不存在"),
    USER_PASSWORD_NOT_MATCH(30002, "密码不正确"),
    USER_PASSWORD_RETRY_LIMIT_EXCEED(30003, "密码错误次数超过限制"),
    USER_CAPTCHA_ERROR(30004, "验证码错误"),
    USER_CAPTCHA_EXPIRE(30005, "验证码已过期"),
    
    // 文件相关错误码 40000-49999
    FILE_NOT_FOUND(40001, "文件不存在"),
    FILE_UPLOAD_FAILED(40002, "文件上传失败"),
    FILE_SIZE_EXCEED(40003, "文件大小超过限制"),
    FILE_NAME_LENGTH_EXCEED(40004, "文件名长度超过限制"),
    FILE_TYPE_NOT_SUPPORTED(40005, "文件类型不支持"),
    
    // 业务错误码 50000-59999
    BUSINESS_ERROR(50001, "业务异常"),
    DEMO_MODE_ERROR(50002, "演示模式，不允许操作"),
    UTIL_ERROR(50003, "工具类异常"),
    
    // 幂等性错误码 60000-69999
    IDEMPOTENT_ERROR(60001, "幂等性校验失败"),
    REPEAT_SUBMIT(60002, "重复提交"),
    
    // 限流错误码 70000-79999
    RATE_LIMIT_ERROR(70001, "请求频率过高，请稍后再试");
    
    private final Integer code;
    private final String message;
    
    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    /**
     * 根据错误码获取错误信息
     * @param code 错误码
     * @return 错误信息
     */
    public static String getMessage(Integer code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode.getMessage();
            }
        }
        return SYSTEM_ERROR.getMessage();
    }
    
    /**
     * 根据错误码获取枚举
     * @param code 错误码
     * @return 错误码枚举
     */
    public static ErrorCode getByCode(Integer code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return SYSTEM_ERROR;
    }
}