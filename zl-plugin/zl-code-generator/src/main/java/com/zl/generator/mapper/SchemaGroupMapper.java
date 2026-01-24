package com.zl.generator.mapper;

import com.zl.generator.domain.SchemaGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 数据模型分组 Mapper 接口
 *
 * @author code-generator
 * @date 2026-01-23
 */
@Mapper
public interface SchemaGroupMapper {

    /**
     * 查询所有分组列表
     *
     * @return 分组列表
     */
    List<SchemaGroup> selectAll();

    /**
     * 根据ID查询分组
     *
     * @param id 分组ID
     * @return 分组对象
     */
    SchemaGroup selectById(Long id);

    /**
     * 根据编码查询分组
     *
     * @param code 分组编码
     * @return 分组对象
     */
    SchemaGroup selectByCode(String code);

    /**
     * 插入分组
     *
     * @param schemaGroup 分组对象
     * @return 影响行数
     */
    int insert(SchemaGroup schemaGroup);

    /**
     * 更新分组
     *
     * @param schemaGroup 分组对象
     * @return 影响行数
     */
    int update(SchemaGroup schemaGroup);

    /**
     * 根据ID删除分组
     *
     * @param id 分组ID
     * @return 影响行数
     */
    int deleteById(Long id);
}
