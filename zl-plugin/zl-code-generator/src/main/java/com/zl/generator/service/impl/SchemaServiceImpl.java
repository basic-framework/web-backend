package com.zl.generator.service.impl;

import cn.hutool.core.util.StrUtil;
import com.zl.generator.domain.Schema;
import com.zl.generator.domain.SchemaField;
import com.zl.generator.mapper.SchemaMapper;
import com.zl.generator.service.SchemaFieldService;
import com.zl.generator.service.SchemaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * 数据模型 Service 实现
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
@Service
@Slf4j
public class SchemaServiceImpl implements SchemaService {

    @Autowired
    private SchemaMapper schemaMapper;

    @Autowired
    private SchemaFieldService schemaFieldService;

    @Autowired
    private DataSource dataSource;

    @Override
    public List<Schema> findAll() {
        return schemaMapper.selectAll();
    }

    @Override
    public Schema findById(Long id) {
        return schemaMapper.selectById(id);
    }

    @Override
    public Schema findByTableName(String tableName) {
        return schemaMapper.selectByTableName(tableName);
    }

    @Override
    public List<Schema> findByGroupId(Long schemaGroupId) {
        return schemaMapper.selectByGroupId(schemaGroupId);
    }

    @Override
    public List<Schema> findByMasterTableId(Long masterTableId) {
        return schemaMapper.selectByMasterTableId(masterTableId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(Schema schema) {
        int result = schemaMapper.insert(schema);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(Schema schema) {
        int result = schemaMapper.update(schema);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteById(Long id) {
        // 先删除关联的字段
        schemaFieldService.deleteBySchemaId(id);
        // 再删除模型
        int result = schemaMapper.deleteById(id);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncFieldsFromTable(Long schemaId) {
        Schema schema = findById(schemaId);
        if (schema == null || StrUtil.isBlank(schema.getTableName())) {
            throw new RuntimeException("模型不存在或表名为空");
        }

        // 先删除旧字段（确保清理干净）
        Boolean deletedCount = schemaFieldService.deleteBySchemaId(schemaId);
        log.info("删除模型 {} 的旧字段，共 {} 条", schemaId, deletedCount);

        // 从数据库表读取字段信息
        List<SchemaField> fields = fetchTableColumns(schema.getTableName(), schemaId);
        if (fields == null || fields.isEmpty()) {
            throw new RuntimeException("无法获取表字段信息");
        }

        log.info("从表 {} 读取到 {} 个字段", schema.getTableName(), fields.size());

        // 批量插入字段
        Boolean result = schemaFieldService.batchInsert(fields);
        if (result) {
            log.info("成功为模型 {} 插入 {} 个字段", schemaId, fields.size());
        }

        // 自动清理可能存在的重复字段（双重保险）
        autoCleanDuplicateFields(schemaId);

        return result;
    }

    @Override
    public Map<String, Object> getGenerateMetaData(String tableName) {
        Schema schema = findByTableName(tableName);
        if (schema == null) {
            throw new RuntimeException("表 " + tableName + " 的模型配置不存在");
        }

        List<SchemaField> fields = schemaFieldService.findBySchemaId(schema.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("schema", schema);
        result.put("fields", fields);
        return result;
    }

    /**
     * 从数据库表读取列信息
     *
     * @param tableName 表名
     * @param schemaId  模型ID
     * @return 字段列表
     */
    private List<SchemaField> fetchTableColumns(String tableName, Long schemaId) {
        List<SchemaField> fieldList = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            // 获取主键信息
            Set<String> primaryKeySet = new HashSet<>();
            try (ResultSet pkRs = metaData.getPrimaryKeys(null, null, tableName)) {
                while (pkRs.next()) {
                    primaryKeySet.add(pkRs.getString("COLUMN_NAME"));
                }
            }

            // 获取列信息
            try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
                int sortNo = 1;
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    SchemaField field = SchemaField.builder()
                            .schemaId(schemaId)
                            .name(rs.getString("REMARKS"))
                            .code(toCamelCase(columnName))
                            .columnName(columnName)
                            .type(rs.getString("TYPE_NAME"))
                            .javaField(toCamelCase(columnName))
                            .javaType(convertToJavaType(rs.getString("TYPE_NAME"), rs.getInt("COLUMN_SIZE")))
                            .comment(rs.getString("REMARKS"))
                            .isPk(primaryKeySet.contains(columnName) ? "1" : "0")
                            .isRequired("NO".equalsIgnoreCase(rs.getString("IS_NULLABLE")) ? "1" : "0")
                            .isInsert("1")
                            .isEdit("1")
                            .isList("1")
                            .isQuery("0")
                            .queryType("EQ")
                            .htmlType("input")
                            .sortNo(sortNo++)
                            .build();

                    fieldList.add(field);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("获取表字段信息失败: " + e.getMessage(), e);
        }

        return fieldList;
    }

    /**
     * 下划线转驼峰
     *
     * @param str 下划线字符串
     * @return 驼峰字符串
     */
    private String toCamelCase(String str) {
        if (StrUtil.isBlank(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        String[] parts = str.split("_");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                result.append(parts[i].toLowerCase());
            } else {
                if (!parts[i].isEmpty()) {
                    result.append(Character.toUpperCase(parts[i].charAt(0)));
                    if (parts[i].length() > 1) {
                        result.append(parts[i].substring(1).toLowerCase());
                    }
                }
            }
        }
        return result.toString();
    }

    @Override
    public List<Map<String, String>> getDatabaseTables() {
        List<Map<String, String>> tableList = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            // 获取当前数据库的所有表
            try (ResultSet rs = metaData.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    Map<String, String> tableInfo = new HashMap<>();
                    String tableName = rs.getString("TABLE_NAME");
                    tableInfo.put("tableName", tableName);
                    tableInfo.put("tableComment", StrUtil.blankToDefault(rs.getString("REMARKS"), tableName));

                    tableList.add(tableInfo);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("获取数据库表列表失败: " + e.getMessage(), e);
        }

        return tableList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean importTable(String tableName, Long schemaGroupId) {
        try {
            // 检查表是否已导入
            Schema existingSchema = schemaMapper.selectByTableName(tableName);
            if (existingSchema != null) {
                throw new RuntimeException("表 " + tableName + " 已经导入过了，请不要重复导入");
            }

            try (Connection conn = dataSource.getConnection()) {
                DatabaseMetaData metaData = conn.getMetaData();

                // 获取表注释
                String tableComment = tableName;
                try (ResultSet rs = metaData.getTables(conn.getCatalog(), null, tableName, new String[]{"TABLE"})) {
                    if (rs.next()) {
                        tableComment = StrUtil.blankToDefault(rs.getString("REMARKS"), tableName);
                    }
                }

                // 生成类名和业务名称
                String className = toPascalCase(tableName);
                String businessName = StrUtil.lowerFirst(className);

                // 创建 Schema
                Schema schema = Schema.builder()
                        .schemaGroupId(schemaGroupId)
                        .name(tableComment)
                        .code(className)
                        .tableName(tableName)
                        .functionName(tableComment)
                        .moduleName("system")
                        .businessName(businessName)
                        .permissionPrefix("system:" + businessName)
                        .build();

                // 保存 Schema
                if (!create(schema)) {
                    throw new RuntimeException("创建模型失败");
                }

                // 同步字段信息
                syncFieldsFromTable(schema.getId());

                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("导入表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 转换为帕斯卡命名
     *
     * @param str 字符串
     * @return 帕斯卡命名
     */
    private String toPascalCase(String str) {
        if (StrUtil.isBlank(str)) {
            return str;
        }

        // 去除表前缀
        String[] prefixes = {"sys_", "dev_", "tbl_", "t_"};
        for (String prefix : prefixes) {
            if (str.startsWith(prefix)) {
                str = str.substring(prefix.length());
                break;
            }
        }

        // 下划线转驼峰
        StringBuilder result = new StringBuilder();
        String[] parts = str.split("_");
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }

        return result.toString();
    }

    /**
     * 数据库类型转Java类型
     *
     * @param dbType  数据库类型
     * @param columnSize 列大小
     * @return Java类型
     */
    private String convertToJavaType(String dbType, int columnSize) {
        if (dbType == null) {
            return "String";
        }

        String type = dbType.toLowerCase();
        if (type.contains("int") || type.contains("tinyint")) {
            return "Integer";
        } else if (type.contains("bigint")) {
            return "Long";
        } else if (type.contains("decimal") || type.contains("numeric") || type.contains("double") || type.contains("float")) {
            return "BigDecimal";
        } else if (type.contains("date")) {
            return "LocalDate";
        } else if (type.contains("datetime") || type.contains("timestamp")) {
            return "LocalDateTime";
        } else if (type.contains("bit") || type.contains("boolean")) {
            return "Boolean";
        } else {
            return "String";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> cleanDuplicateFields(Long schemaId) {
        Map<String, Object> result = new HashMap<>();

        // 获取当前所有字段
        List<SchemaField> allFields = schemaFieldService.findBySchemaId(schemaId);

        if (allFields == null || allFields.isEmpty()) {
            result.put("message", "没有需要清理的字段");
            result.put("deletedCount", 0);
            result.put("remainingCount", 0);
            return result;
        }

        // 按列名分组，找出重复的字段
        Map<String, List<SchemaField>> fieldGroups = new HashMap<>();
        for (SchemaField field : allFields) {
            String columnName = field.getColumnName();
            if (!fieldGroups.containsKey(columnName)) {
                fieldGroups.put(columnName, new ArrayList<>());
            }
            fieldGroups.get(columnName).add(field);
        }

        // 找出重复的字段（保留每组中ID最小的）
        List<Long> idsToDelete = new ArrayList<>();
        for (List<SchemaField> group : fieldGroups.values()) {
            if (group.size() > 1) {
                // 按 ID 排序，保留最小的，删除其他的
                group.sort((f1, f2) -> Long.compare(f1.getId(), f2.getId()));
                for (int i = 1; i < group.size(); i++) {
                    idsToDelete.add(group.get(i).getId());
                }
            }
        }

        // 删除重复的字段
        int deletedCount = 0;
        for (Long id : idsToDelete) {
            schemaFieldService.deleteById(id);
            deletedCount++;
        }

        // 获取清理后的字段数量
        int remainingCount = schemaFieldService.findBySchemaId(schemaId).size();

        result.put("message", String.format("清理完成，删除了 %d 个重复字段，剩余 %d 个字段", deletedCount, remainingCount));
        result.put("deletedCount", deletedCount);
        result.put("remainingCount", remainingCount);

        log.info("清理模型 {} 的重复字段，删除 {} 个，剩余 {} 个", schemaId, deletedCount, remainingCount);

        return result;
    }

    /**
     * 自动清理重复字段（内部使用，不返回结果）
     *
     * @param schemaId 模型ID
     */
    private void autoCleanDuplicateFields(Long schemaId) {
        try {
            // 获取当前所有字段
            List<SchemaField> allFields = schemaFieldService.findBySchemaId(schemaId);

            if (allFields == null || allFields.isEmpty()) {
                return;
            }

            // 按列名分组，找出重复的字段
            Map<String, List<SchemaField>> fieldGroups = new HashMap<>();
            for (SchemaField field : allFields) {
                String columnName = field.getColumnName();
                if (!fieldGroups.containsKey(columnName)) {
                    fieldGroups.put(columnName, new ArrayList<>());
                }
                fieldGroups.get(columnName).add(field);
            }

            // 找出重复的字段（保留每组中ID最小的）
            List<Long> idsToDelete = new ArrayList<>();
            for (List<SchemaField> group : fieldGroups.values()) {
                if (group.size() > 1) {
                    // 按 ID 排序，保留最小的，删除其他的
                    group.sort((f1, f2) -> Long.compare(f1.getId(), f2.getId()));
                    for (int i = 1; i < group.size(); i++) {
                        idsToDelete.add(group.get(i).getId());
                    }
                }
            }

            // 删除重复的字段
            int deletedCount = 0;
            for (Long id : idsToDelete) {
                schemaFieldService.deleteById(id);
                deletedCount++;
            }

            if (deletedCount > 0) {
                log.warn("自动清理模型 {} 的 {} 个重复字段", schemaId, deletedCount);
            }
        } catch (Exception e) {
            log.error("自动清理重复字段时发生错误", e);
            // 不抛出异常，避免影响主流程
        }
    }
}
