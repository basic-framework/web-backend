package com.zl.generator.service;

import com.zl.generator.domain.SchemaGroup;

import java.util.List;

/**
 * 数据模型分组 Service 接口
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
public interface SchemaGroupService {

    /**
     * 查询所有分组列表
     *
     * @return 分组列表
     */
    List<SchemaGroup> findAll();

    /**
     * 根据ID查询分组
     *
     * @param id 分组ID
     * @return 分组对象
     */
    SchemaGroup findById(Long id);

    /**
     * 根据编码查询分组
     *
     * @param code 分组编码
     * @return 分组对象
     */
    SchemaGroup findByCode(String code);

    /**
     * 创建分组
     *
     * @param schemaGroup 分组对象
     * @return 是否成功
     */
    Boolean create(SchemaGroup schemaGroup);

    /**
     * 更新分组
     *
     * @param schemaGroup 分组对象
     * @return 是否成功
     */
    Boolean update(SchemaGroup schemaGroup);

    /**
     * 根据ID删除分组
     *
     * @param id 分组ID
     * @return 是否成功
     */
    Boolean deleteById(Long id);
}
