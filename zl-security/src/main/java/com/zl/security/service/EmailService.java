package com.zl.security.service;

import com.zl.model.dto.EmailRegisterDto;
import com.zl.model.dto.SendCodeDto;

/**
 * 邮件服务接口
 * @Author GuihaoLv
 */
public interface EmailService {

    /**
     * 发送注册验证码
     * @param sendCodeDto
     */
    void sendRegisterCode(SendCodeDto sendCodeDto);

    /**
     * 邮箱注册
     * @param registerDto
     * @return
     */
    Boolean registerWithEmail(EmailRegisterDto registerDto);
}
