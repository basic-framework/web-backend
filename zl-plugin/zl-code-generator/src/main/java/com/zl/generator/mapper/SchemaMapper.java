package com.zl.generator.mapper;

import com.zl.generator.domain.Schema;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据模型 Mapper 接口
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
@Mapper
public interface SchemaMapper {

    /**
     * 查询所有模型列表
     *
     * @return 模型列表
     */
    List<Schema> selectAll();

    /**
     * 根据ID查询模型
     *
     * @param id 模型ID
     * @return 模型对象
     */
    Schema selectById(Long id);

    /**
     * 根据表名查询模型
     *
     * @param tableName 表名
     * @return 模型对象
     */
    Schema selectByTableName(@Param("tableName") String tableName);

    /**
     * 根据分组ID查询模型列表
     *
     * @param schemaGroupId 分组ID
     * @return 模型列表
     */
    List<Schema> selectByGroupId(@Param("schemaGroupId") Long schemaGroupId);

    /**
     * 根据主表ID查询子表列表
     *
     * @param masterTableId 主表ID
     * @return 子表列表
     */
    List<Schema> selectByMasterTableId(@Param("masterTableId") Long masterTableId);

    /**
     * 插入模型
     *
     * @param schema 模型对象
     * @return 影响行数
     */
    int insert(Schema schema);

    /**
     * 更新模型
     *
     * @param schema 模型对象
     * @return 影响行数
     */
    int update(Schema schema);

    /**
     * 根据ID删除模型
     *
     * @param id 模型ID
     * @return 影响行数
     */
    int deleteById(Long id);
}
