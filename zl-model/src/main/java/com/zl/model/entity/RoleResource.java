package com.zl.model.entity;

import com.zl.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 /**
 * 角色资源关联实体类
 * @Auther: GuihaoLv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResource extends BaseEntity {
    private Long roleId;      // 角色ID
    private String resourceNo; // 资源编号
}