package com.zl.model.entity.security;

import com.zl.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 /**
 * 角色实体类
 * @Auther: GuihaoLv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role extends BaseEntity {
    private String roleName;  // 角色名称
    private String label;     // 权限标识
    private Integer sortNo;   // 排序
}
