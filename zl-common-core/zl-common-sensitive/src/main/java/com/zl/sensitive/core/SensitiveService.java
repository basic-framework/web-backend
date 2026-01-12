package com.zl.sensitive.core;


/**
 * 脱敏服务
 * 默认管理员不过滤
 * 需自行根据业务重写实现
 * @Author: GuihaoLv
 */
public interface SensitiveService {

    /**
     * 是否脱敏
     */
    boolean isSensitive();

}
