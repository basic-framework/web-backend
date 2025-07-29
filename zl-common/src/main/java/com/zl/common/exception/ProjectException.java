package com.zl.common.exception;


import com.zl.model.enums.IBasicEnum;

/**
 * 自定义异常
 * @Auther GuihaoLv
 */
public class ProjectException extends RuntimeException {

    //错误编码
    private int code;

    //提示信息
    private String message;

    //异常接口
    private IBasicEnum basicEnumIntface;

    public ProjectException() {

    }
    public ProjectException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ProjectException(IBasicEnum errorCode) {
        setBasicMsg(errorCode);
    }


    public ProjectException(IBasicEnum errorCode, String throwMsg) {
        super(throwMsg);
        setBasicMsg(errorCode);
    }

    public ProjectException(IBasicEnum errorCode, Throwable throwable) {
        super(throwable);
        setBasicMsg(errorCode);
    }


    private void setBasicMsg(IBasicEnum basicEnumIntface) {
        this.code = basicEnumIntface.getCode();
        this.message = basicEnumIntface.getMsg();
        this.basicEnumIntface = basicEnumIntface;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public IBasicEnum getBasicEnumIntface() {
        return basicEnumIntface;
    }

    public void setBasicEnumIntface(IBasicEnum basicEnumIntface) {
        this.basicEnumIntface = basicEnumIntface;
    }

    @Override
    public String toString() {
        return "ProjectException{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", basicEnumIntface=" + basicEnumIntface +
                '}';
    }
}
