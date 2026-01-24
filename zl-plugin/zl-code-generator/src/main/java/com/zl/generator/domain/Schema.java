package com.zl.generator.domain;

import com.zl.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据模型实体类
 *
 * @author GuihaoLv
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

    /**
     * 表类型：SINGLE-单表, MASTER-主表, DETAIL-子表, TREE-树表
     */
    private String tableType;

    /**
     * 主表ID（子表使用）
     */
    private Long masterTableId;

    /**
     * 主表名称（子表使用，非数据库字段）
     */
    private transient String masterTableName;

    /**
     * 主表类名（子表使用，非数据库字段）
     */
    private transient String masterClassName;

    /**
     * 关联字段名（子表中指向主表的外键字段）
     */
    private String relationField;

    /**
     * 树表的父字段名（如：parent_id）
     */
    private String treeParentField;

    /**
     * 树表的子节点集合字段名（如：children）
     */
    private String treeChildrenField;

    /**
     * 子表列表（主表使用，非数据库字段）
     */
    private transient java.util.List<Schema> detailTables;
}
