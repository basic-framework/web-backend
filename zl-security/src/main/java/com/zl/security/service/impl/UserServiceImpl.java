package com.zl.security.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zl.common.utils.authUtils.UserUtil;
import com.zl.model.entity.security.User;
import com.zl.model.vo.LoginVo;
import com.zl.model.vo.UserNavVo;

import com.zl.security.mapper.UserMapper;
import com.zl.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /***
     *  查询用户构建对象
     * @param username
     * @return
     */
    public LoginVo findUserVoForLogin(String username) {
        User userVo = LoginVo.builder()
                .username(username)
                .build();
        User user = userMapper.findUserVoForLogin(username);
        if (!ObjectUtil.isEmpty(user)){
            return BeanUtil.toBean(user,LoginVo.class);
        }
        throw new RuntimeException("用户名或密码错误");
    }

    /**
     * 获取用户中心信息
     * @return
     */
    public UserNavVo getUserNavInfo() {
        Long id= UserUtil.getUserId();
        UserNavVo userNavVo=userMapper.getUserNavInfo(id);
        userNavVo.setUserId(id);
        return userNavVo;
    }




    /**
     * 重置密码
     * @param newPassword
     * @return
     */
    @Override
    public Boolean resetPassword(String newPassword) {
        // 1. 获取当前登录用户信息
        Long userId= UserUtil.getUserId();
        // 2. 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);
        // 3. 更新数据库中的密码
        return userMapper.updatePasswordByUsername(userId, encodedPassword);
    }




    /**
     * 更新用户信息
     * @param userNavVo
     * @return
     */
    public Boolean updateUserInfo(UserNavVo userNavVo) {
        Long userId= UserUtil.getUserId();
        User user=User.builder()
               .username(userNavVo.getUsername())
               .avatar(userNavVo.getAvatar())
               .phoneNumber(userNavVo.getPhoneNumber())
              .email(userNavVo.getEmail())
              .build();
        user.setId(userId);
        Boolean flag=userMapper.updateUserInfo(user);
        return flag;
    }


}
