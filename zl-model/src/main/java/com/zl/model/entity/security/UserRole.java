package com.zl.model.entity.security;

import com.zl.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 /**
 * 用户角色关联实体类
 * @Auther: GuihaoLv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRole extends BaseEntity {
    private Long userId;  // 用户ID
    private Long roleId;  // 角色ID
}