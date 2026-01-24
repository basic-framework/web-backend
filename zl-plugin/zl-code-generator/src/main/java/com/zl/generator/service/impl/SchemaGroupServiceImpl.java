package com.zl.generator.service.impl;

import com.zl.generator.domain.SchemaGroup;
import com.zl.generator.mapper.SchemaGroupMapper;
import com.zl.generator.service.SchemaGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据模型分组 Service 实现
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
@Service
public class SchemaGroupServiceImpl implements SchemaGroupService {

    @Autowired
    private SchemaGroupMapper schemaGroupMapper;

    @Override
    public List<SchemaGroup> findAll() {
        return schemaGroupMapper.selectAll();
    }

    @Override
    public SchemaGroup findById(Long id) {
        return schemaGroupMapper.selectById(id);
    }

    @Override
    public SchemaGroup findByCode(String code) {
        return schemaGroupMapper.selectByCode(code);
    }

    @Override
    public Boolean create(SchemaGroup schemaGroup) {
        int result = schemaGroupMapper.insert(schemaGroup);
        return result > 0;
    }

    @Override
    public Boolean update(SchemaGroup schemaGroup) {
        int result = schemaGroupMapper.update(schemaGroup);
        return result > 0;
    }

    @Override
    public Boolean deleteById(Long id) {
        int result = schemaGroupMapper.deleteById(id);
        return result > 0;
    }
}
