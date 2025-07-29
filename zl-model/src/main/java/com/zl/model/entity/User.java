package com.zl.model.entity;

import com.zl.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 /**
 * 用户实体类
 * @Auther: GuihaoLv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String avatar;
}
