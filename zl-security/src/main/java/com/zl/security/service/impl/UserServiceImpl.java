package com.zl.security.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zl.common.utils.authUtils.UserUtil;
import com.zl.model.dto.ResetPasswordDto;
import com.zl.model.entity.security.User;
import com.zl.model.vo.LoginVo;
import com.zl.model.vo.UserNavVo;

import com.zl.security.mapper.UserMapper;
import com.zl.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String EMAIL_CODE_PREFIX = "email:code:";


    /***
     *  查询用户构建对象（支持用户名或邮箱登录）
     * @param account 用户名或邮箱
     * @return
     */
    public LoginVo findUserVoForLogin(String account) {
        // 先尝试用用户名查询，如果不存在再用邮箱查询
        User user = userMapper.findUserVoForLogin(account);
        if (ObjectUtil.isEmpty(user)) {
            user = userMapper.findUserByEmail(account);
        }
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
     * @param resetPasswordDto 重置密码入参（包含邮箱、验证码、新密码）
     * @return 重置是否成功
     * @throws IllegalArgumentException 参数不合法时抛出
     * @throws RuntimeException        数据库操作或认证失败时抛出
     */
    public Boolean resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        // ========== 1. 入参非空校验 ==========
        if (resetPasswordDto == null) {
            throw new IllegalArgumentException("重置密码参数不能为空");
        }
        String email = resetPasswordDto.getEmail();
        String newPassword = resetPasswordDto.getNewPassword();
        String verifyCode=resetPasswordDto.getVerifyCode();

        // 校验邮箱、新密码、验证码非空
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (!StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("新密码不能为空");
        }
        if (!StringUtils.hasText(verifyCode)) {
            throw new IllegalArgumentException("验证码不能为空");
        }

        // ========== 3. 邮箱验证码认证（核心安全逻辑） ==========
        String codeKey = EMAIL_CODE_PREFIX + email;
        String savedCode = stringRedisTemplate.opsForValue().get(codeKey);
        if (savedCode == null) {
            throw new RuntimeException("验证码已过期或不存在");
        }
        if (!savedCode.equals(verifyCode)) {
            throw new RuntimeException("验证码错误");
        }
        // ========== 4. 查询用户ID并校验用户是否存在 ==========
        Long userId = userMapper.getUserIdByEmail(email);
        if (userId == null || userId <= 0) {
            throw new RuntimeException("该邮箱未注册");
        }

        // ========== 5. 加密新密码 ==========
        String encodedPassword = passwordEncoder.encode(newPassword);

        Boolean result=userMapper.updatePasswordByUsername(userId, encodedPassword);
        if(Boolean.TRUE.equals(result)){
            stringRedisTemplate.delete(codeKey);
        }
        return result;
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
