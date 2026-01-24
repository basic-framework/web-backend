package com.zl.generator.domain;

import com.zl.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据模型实体类
 *
 * @author code-generator
 * @date 2026-01-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Schema extends BaseEntity {
    /**
     * 分组ID
     */
    private Long schemaGroupId;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型编码
     */
    private String code;

    /**
     * 数据库表名
     */
    private String tableName;

    /**
     * 类名（用于代码生成）
     */
    private transient String className;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 业务名称
     */
    private String businessName;

    /**
     * 权限前缀
     */
    private String permissionPrefix;

    /**
     * 分组名称（非数据库字段）
     */
    private transient String groupName;
}
