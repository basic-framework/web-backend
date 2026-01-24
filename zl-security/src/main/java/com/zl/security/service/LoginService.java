package com.zl.security.service;

import com.zl.model.dto.LoginDto;
import com.zl.model.vo.LoginVo;


public interface  LoginService {
    /**
     * 用户登录
     * @param loginDto
     * @return
     */
    LoginVo login(LoginDto loginDto);

    /**
     * 用户退出
     * @param
     * @return
     */
    Boolean logout();
}
