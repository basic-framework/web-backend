package com.zl.generator.vo;

import lombok.Data;

/**
 * 数据模型 VO
 *
 * @author code-generator
 * @date 2026-01-23
 */
@Data
public class SchemaVo {
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
     * 分组名称
     */
    private String groupName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private String createTime;
}
