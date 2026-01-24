package com.zl.generator.dto;

import lombok.Data;

/**
 * 数据模型 DTO
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
@Data
public class SchemaDto {
    /**
     * 主键ID
     */
    private Long id;

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
     * 备注
     */
    private String remark;
}
