package com.zl.security.service;

import com.zl.model.vo.LoginVo;
import com.zl.model.vo.UserNavVo;

public interface UserService {
    /***
     *  查询用户构建对象（支持用户名或邮箱）
     * @param account 用户名或邮箱
     * @return
     * @return:
     */
    LoginVo findUserVoForLogin(String account);


    /**
     * 获取用户中心信息
     * @return
     */
    UserNavVo getUserNavInfo();


    /**
     * 重置密码
     * @param newPassword
     * @return
     */
    Boolean resetPassword(String newPassword);


    /**
     * 更新用户信息
     * @param userNavVo
     * @return
     */
    Boolean updateUserInfo(UserNavVo userNavVo);
}
