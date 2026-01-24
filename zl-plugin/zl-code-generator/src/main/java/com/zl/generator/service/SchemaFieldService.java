package com.zl.generator.service;

import com.zl.generator.domain.SchemaField;

import java.util.List;

/**
 * 模型字段 Service 接口
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
public interface SchemaFieldService {

    /**
     * 根据模型ID查询字段列表
     *
     * @param schemaId 模型ID
     * @return 字段列表
     */
    List<SchemaField> findBySchemaId(Long schemaId);

    /**
     * 根据ID查询字段
     *
     * @param id 字段ID
     * @return 字段对象
     */
    SchemaField findById(Long id);

    /**
     * 批量插入字段
     *
     * @param schemaFieldList 字段列表
     * @return 是否成功
     */
    Boolean batchInsert(List<SchemaField> schemaFieldList);

    /**
     * 更新字段
     *
     * @param schemaField 字段对象
     * @return 是否成功
     */
    Boolean update(SchemaField schemaField);

    /**
     * 批量更新字段
     *
     * @param schemaFieldList 字段列表
     * @return 是否成功
     */
    Boolean batchUpdate(List<SchemaField> schemaFieldList);

    /**
     * 根据ID删除字段
     *
     * @param id 字段ID
     * @return 是否成功
     */
    Boolean deleteById(Long id);

    /**
     * 根据模型ID删除所有字段
     *
     * @param schemaId 模型ID
     * @return 是否成功
     */
    Boolean deleteBySchemaId(Long schemaId);
}
