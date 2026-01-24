package com.zl.generator.mapper;

import com.zl.generator.domain.SchemaField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模型字段 Mapper 接口
 *
 * @author code-generator
 * @date 2026-01-23
 */
@Mapper
public interface SchemaFieldMapper {

    /**
     * 根据模型ID查询字段列表
     *
     * @param schemaId 模型ID
     * @return 字段列表
     */
    List<SchemaField> selectBySchemaId(@Param("schemaId") Long schemaId);

    /**
     * 根据ID查询字段
     *
     * @param id 字段ID
     * @return 字段对象
     */
    SchemaField selectById(Long id);

    /**
     * 批量插入字段
     *
     * @param schemaFieldList 字段列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<SchemaField> schemaFieldList);

    /**
     * 更新字段
     *
     * @param schemaField 字段对象
     * @return 影响行数
     */
    int update(SchemaField schemaField);

    /**
     * 根据ID删除字段
     *
     * @param id 字段ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据模型ID删除所有字段
     *
     * @param schemaId 模型ID
     * @return 影响行数
     */
    int deleteBySchemaId(@Param("schemaId") Long schemaId);
}
