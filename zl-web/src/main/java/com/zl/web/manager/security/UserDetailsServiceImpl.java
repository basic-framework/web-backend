package com.zl.web.manager.security;

import com.zl.model.vo.LoginVo;
import com.zl.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

 /**
 * Security 自定义 UserDetailsService 实现
 * @Author GuihaoLv
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //这里我们默认使用账号密码登录,对于多种登录方式如何处理-->字符串分割
        LoginVo userVo = userService.findUserVoForLogin(username);
        return new UserAuth(userVo);
    }
}
