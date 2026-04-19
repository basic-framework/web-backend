package com.zl.security.entity;

import cn.hutool.core.util.ObjectUtil;
import com.zl.model.vo.LoginVo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

 /**
 * 自定认证用户
 * @GuihaoLv
 */
@Data
@NoArgsConstructor
public class UserAuth implements UserDetails {

    private String id;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;
     /**
      * 权限内置
      */
     private Collection<SimpleGrantedAuthority> authorities;


     /**
      * 用户邮箱
      */
     private String email;


     /**
      * 手机号码
      */
     private String phoneNumber;

     /**
      * 头像
      */
     private String avatar;


     /**
      * 创建者
      */
     private Long createBy;

     /**

     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;

     private String dataState;

    public UserAuth(LoginVo userVo) {
        this.setId(userVo.getId().toString());
        this.setUsername(userVo.getUsername());
        this.setPassword(userVo.getPassword());
        this.setAvatar(userVo.getAvatar());
        if (!ObjectUtil.isEmpty(userVo.getResourcePaths())) {
            authorities = new ArrayList<>();
            userVo.getResourcePaths().forEach(resourceRequestPath -> authorities.add(new SimpleGrantedAuthority(resourceRequestPath)));
        }

        this.setEmail(userVo.getEmail());
        this.setPhoneNumber(userVo.getPhoneNumber());
        this.setCreateTime(userVo.getCreateTime());
        this.setCreateBy(userVo.getCreateBy());
        this.setUpdateTime(userVo.getUpdateTime());
        this.setUpdateBy(userVo.getUpdateBy());
        this.setRemark(userVo.getRemark());

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
