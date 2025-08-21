package com.zl.model.entity.security;

import com.zl.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限资源实体类
 * @Auther: GuihaoLv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Resource extends BaseEntity {
    private String resourceNo;        // 资源编号
    private String parentResourceNo;  // 父资源编号
    private String resourceName;      // 资源名称
    private String resourceType;      // 资源类型
    private String requestPath;       // 请求地址
    private String label;             // 权限标识
    private Integer sortNo;           // 排序
    private String icon;              // 图标
}