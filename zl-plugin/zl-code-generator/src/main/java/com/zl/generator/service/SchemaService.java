package com.zl.generator.service;

import com.zl.generator.domain.Schema;
import com.zl.generator.domain.SchemaField;

import java.util.List;
import java.util.Map;

/**
 * 数据模型 Service 接口
 *
 * @author code-generator
 * @date 2026-01-23
 */
public interface SchemaService {

    /**
     * 查询所有模型列表
     *
     * @return 模型列表
     */
    List<Schema> findAll();

    /**
     * 根据ID查询模型
     *
     * @param id 模型ID
     * @return 模型对象
     */
    Schema findById(Long id);

    /**
     * 根据表名查询模型
     *
     * @param tableName 表名
     * @return 模型对象
     */
    Schema findByTableName(String tableName);

    /**
     * 根据分组ID查询模型列表
     *
     * @param schemaGroupId 分组ID
     * @return 模型列表
     */
    List<Schema> findByGroupId(Long schemaGroupId);

    /**
     * 创建模型
     *
     * @param schema 模型对象
     * @return 是否成功
     */
    Boolean create(Schema schema);

    /**
     * 更新模型
     *
     * @param schema 模型对象
     * @return 是否成功
     */
    Boolean update(Schema schema);

    /**
     * 根据ID删除模型
     *
     * @param id 模型ID
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    /**
     * 从数据库表同步字段信息
     *
     * @param schemaId 模型ID
     * @return 是否成功
     */
    Boolean syncFieldsFromTable(Long schemaId);

    /**
     * 根据表名获取代码生成元数据
     *
     * @param tableName 表名
     * @return 包含模型和字段的Map
     */
    Map<String, Object> getGenerateMetaData(String tableName);

    /**
     * 获取数据库中的所有表
     *
     * @return 表名列表
     */
    List<Map<String, String>> getDatabaseTables();

    /**
     * 导入数据库表并自动创建模型和字段配置
     *
     * @param tableName 表名
     * @param schemaGroupId 分组ID
     * @return 是否成功
     */
    Boolean importTable(String tableName, Long schemaGroupId);

    /**
     * 清理重复的字段数据
     *
     * @param schemaId 模型ID
     * @return 清理结果
     */
    Map<String, Object> cleanDuplicateFields(Long schemaId);
}
