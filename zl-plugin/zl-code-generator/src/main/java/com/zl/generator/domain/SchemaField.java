package com.zl.generator.domain;

import com.zl.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模型字段实体类
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchemaField extends BaseEntity {
    /**
     * 所属模型ID
     */
    private Long schemaId;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段编码
     */
    private String code;

    /**
     * 数据库列名
     */
    private String columnName;

    /**
     * 字段类型
     */
    private String type;

    /**
     * Java类型
     */
    private String javaType;

    /**
     * Java字段名
     */
    private String javaField;

    /**
     * 字段注释
     */
    private String comment;

    /**
     * 是否主键（0否 1是）
     */
    private String isPk;

    /**
     * 是否必填（0否 1是）
     */
    private String isRequired;

    /**
     * 是否插入字段（0否 1是）
     */
    private String isInsert;

    /**
     * 是否编辑字段（0否 1是）
     */
    private String isEdit;

    /**
     * 是否列表显示（0否 1是）
     */
    private String isList;

    /**
     * 是否查询字段（0否 1是）
     */
    private String isQuery;

    /**
     * 查询方式（EQ、NE、GT、LT、LIKE、BETWEEN）
     */
    private String queryType;

    /**
     * 显示类型（input、select、datetime、image、file等）
     */
    private String htmlType;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 排序
     */
    private Integer sortNo;
}
