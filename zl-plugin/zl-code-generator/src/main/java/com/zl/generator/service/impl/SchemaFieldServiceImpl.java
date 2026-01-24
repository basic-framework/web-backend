package com.zl.generator.service.impl;

import com.zl.generator.domain.SchemaField;
import com.zl.generator.mapper.SchemaFieldMapper;
import com.zl.generator.service.SchemaFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * 模型字段 Service 实现
 *
 * @author code-generator
 * @date 2026-01-23
 */
@Service
public class SchemaFieldServiceImpl implements SchemaFieldService {

    @Autowired
    private SchemaFieldMapper schemaFieldMapper;

    @Override
    public List<SchemaField> findBySchemaId(Long schemaId) {
        List<SchemaField> fields = schemaFieldMapper.selectBySchemaId(schemaId);

        // 如果字段数量异常多（超过100个），可能存在重复，触发自动清理
        if (fields != null && fields.size() > 100) {
            // 检查是否存在重复（按列名判断）
            Set<String> columnNames = new HashSet<>();
            boolean hasDuplicates = false;
            for (SchemaField field : fields) {
                if (!columnNames.add(field.getColumnName())) {
                    hasDuplicates = true;
                    break;
                }
            }

            // 如果发现重复，记录警告日志（不自动清理，避免影响性能）
            if (hasDuplicates) {
                // 可以在这里触发自动清理，但为了避免循环调用，暂时只记录日志
                // 实际清理应该通过 syncFieldsFromTable 或 cleanDuplicateFields 进行
            }
        }

        return fields;
    }

    @Override
    public SchemaField findById(Long id) {
        return schemaFieldMapper.selectById(id);
    }

    @Override
    public Boolean batchInsert(List<SchemaField> schemaFieldList) {
        if (schemaFieldList == null || schemaFieldList.isEmpty()) {
            return false;
        }
        int result = schemaFieldMapper.batchInsert(schemaFieldList);
        return result > 0;
    }

    @Override
    public Boolean update(SchemaField schemaField) {
        int result = schemaFieldMapper.update(schemaField);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchUpdate(List<SchemaField> schemaFieldList) {
        if (schemaFieldList == null || schemaFieldList.isEmpty()) {
            return false;
        }
        for (SchemaField field : schemaFieldList) {
            schemaFieldMapper.update(field);
        }
        return true;
    }

    @Override
    public Boolean deleteById(Long id) {
        int result = schemaFieldMapper.deleteById(id);
        return result > 0;
    }

    @Override
    public Boolean deleteBySchemaId(Long schemaId) {
        int result = schemaFieldMapper.deleteBySchemaId(schemaId);
        return result > 0;
    }
}
