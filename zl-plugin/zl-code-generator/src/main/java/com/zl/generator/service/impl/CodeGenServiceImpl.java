package com.zl.generator.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.zl.generator.config.GenConfig;
import com.zl.generator.domain.Schema;
import com.zl.generator.domain.SchemaField;
import com.zl.generator.dto.CodeGenerateDto;
import com.zl.generator.service.CodeGenService;
import com.zl.generator.service.SchemaFieldService;
import com.zl.generator.service.SchemaService;
import com.zl.generator.util.VelocityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成 Service 实现
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
@Slf4j
@Service
public class CodeGenServiceImpl implements CodeGenService {

    @Autowired
    private GenConfig genConfig;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private SchemaFieldService schemaFieldService;

    @Override
    public Map<String, Object> batchGenCode(CodeGenerateDto codeGenerateDto) {
        String tableNameStr = codeGenerateDto.getTableNameStr();
        if (StrUtil.isBlank(tableNameStr)) {
            throw new RuntimeException("表名不能为空");
        }

        String[] tableNames = tableNameStr.split(",");
        List<Map<String, Object>> results = new ArrayList<>();

        for (String tableName : tableNames) {
            tableName = tableName.trim();
            if (StrUtil.isBlank(tableName)) {
                continue;
            }

            try {
                // 生成单个表的代码（会自动包含 DTO）
                generateCodeByTableName(tableName);
                results.add(createResultMap(tableName, true, "生成成功"));
            } catch (Exception e) {
                log.error("生成表 {} 的代码失败", tableName, e);
                results.add(createResultMap(tableName, false, e.getMessage()));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", tableNames.length);
        result.put("success", results.stream().filter(r -> (Boolean) r.get("success")).count());
        result.put("details", results);
        return result;
    }

    @Override
    public Map<String, Object> batchGenFrontendCode(CodeGenerateDto codeGenerateDto) {
        // 前端代码生成通过外部命令执行
        String workPath = codeGenerateDto.getWorkPath();
        String previewCode = codeGenerateDto.getPreviewCode();

        if (StrUtil.isBlank(workPath) || StrUtil.isBlank(previewCode)) {
            throw new RuntimeException("前端项目路径和生成命令不能为空");
        }

        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.directory(new File(workPath));

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                pb.command("cmd", "/c", previewCode);
            } else {
                pb.command("sh", "-c", previewCode);
            }

            Process process = pb.start();
            int exitCode = process.waitFor();

            Map<String, Object> result = new HashMap<>();
            result.put("success", exitCode == 0);
            result.put("message", exitCode == 0 ? "前端代码生成成功" : "前端代码生成失败，退出码: " + exitCode);
            return result;

        } catch (Exception e) {
            log.error("生成前端代码失败", e);
            throw new RuntimeException("生成前端代码失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> previewCode(String tableName) {
        // 从数据库获取配置的 Schema 信息
        Schema schema = schemaService.findByTableName(tableName);
        if (schema == null) {
            log.warn("表 {} 的配置不存在，使用默认配置", tableName);
            schema = buildSchemaFromTable(tableName);
        }

        // 从数据库获取配置的字段信息
        List<SchemaField> fields;
        try {
            fields = schemaFieldService.findBySchemaId(schema.getId());
            if (fields == null || fields.isEmpty()) {
                log.warn("表 {} 的字段配置为空，从数据库读取字段信息", tableName);
                fields = buildFieldsFromTable(tableName);
            }
        } catch (Exception e) {
            log.error("获取字段配置失败，从数据库读取字段信息", e);
            fields = buildFieldsFromTable(tableName);
        }

        // 如果是主表，加载子表信息
        if ("MASTER".equals(schema.getTableType()) && schema.getId() != null) {
            List<Schema> detailTables = schemaService.findByMasterTableId(schema.getId());
            if (detailTables != null && !detailTables.isEmpty()) {
                // 为每个子表设置className等必要信息
                for (Schema detailTable : detailTables) {
                    String detailClassName = convertToClassName(detailTable.getTableName());
                    detailTable.setClassName(detailClassName);

                    // 设置小写类名（用于变量名）
                    detailTable.setBusinessName(StrUtil.lowerFirst(detailClassName));

                    // 设置功能名称（如果没有的话）
                    if (StrUtil.isBlank(detailTable.getFunctionName())) {
                        detailTable.setFunctionName(detailTable.getName() != null ? detailTable.getName() : detailClassName);
                    }

                    // 设置子表的主表信息
                    detailTable.setMasterTableName(schema.getTableName());
                    detailTable.setMasterClassName(schema.getClassName());
                }
                schema.setDetailTables(detailTables);
                log.info("加载到 {} 个子表", detailTables.size());
            }
        }

        // 准备 Velocity 上下文
        Map<String, Object> context = VelocityUtils.prepareContext(schema, fields, genConfig);

        // 根据表类型获取模板列表
        String tableType = schema.getTableType() != null ? schema.getTableType() : "SINGLE";
        Map<String, String> templates = VelocityUtils.getTemplateList(tableType);
        Map<String, Object> preview = new HashMap<>();

        for (Map.Entry<String, String> entry : templates.entrySet()) {
            String templateName = entry.getKey();
            String templatePath = entry.getValue();
            try {
                String content = VelocityUtils.render(templatePath, context);
                preview.put(templateName, content);
            } catch (Exception e) {
                log.error("预览模板 {} 失败", templateName, e);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("schema", schema);
        result.put("preview", preview);
        return result;
    }

    /**
     * 根据表名生成代码（自动包含 DTO 文件）
     *
     * @param tableName 表名
     */
    private void generateCodeByTableName(String tableName) {
        // 从数据库获取配置的 Schema 信息
        Schema schema = schemaService.findByTableName(tableName);
        if (schema == null) {
            log.warn("表 {} 的配置不存在，使用默认配置", tableName);
            schema = buildSchemaFromTable(tableName);
        }

        // 从数据库获取配置的字段信息
        List<SchemaField> fields;
        try {
            fields = schemaFieldService.findBySchemaId(schema.getId());
            if (fields == null || fields.isEmpty()) {
                log.warn("表 {} 的字段配置为空，从数据库读取字段信息", tableName);
                fields = buildFieldsFromTable(tableName);
            }
        } catch (Exception e) {
            log.error("获取字段配置失败，从数据库读取字段信息", e);
            fields = buildFieldsFromTable(tableName);
        }

        // 如果是主表，加载子表信息
        if ("MASTER".equals(schema.getTableType()) && schema.getId() != null) {
            List<Schema> detailTables = schemaService.findByMasterTableId(schema.getId());
            if (detailTables != null && !detailTables.isEmpty()) {
                // 为每个子表设置className等必要信息
                for (Schema detailTable : detailTables) {
                    String detailClassName = convertToClassName(detailTable.getTableName());
                    detailTable.setClassName(detailClassName);

                    // 设置子表的主表信息
                    detailTable.setMasterTableName(schema.getTableName());
                    detailTable.setMasterClassName(schema.getClassName());
                }
                schema.setDetailTables(detailTables);
                log.info("加载到 {} 个子表", detailTables.size());
            }
        }

        // 准备 Velocity 上下文
        Map<String, Object> context = VelocityUtils.prepareContext(schema, fields, genConfig);

        // 根据表类型获取模板列表
        String tableType = schema.getTableType() != null ? schema.getTableType() : "SINGLE";
        Map<String, String> templates = VelocityUtils.getTemplateList(tableType);

        for (Map.Entry<String, String> entry : templates.entrySet()) {
            String templateName = entry.getKey();
            String templatePath = entry.getValue();

            try {
                // 渲染模板
                String content = VelocityUtils.render(templatePath, context);

                // 生成文件路径
                String outputPath = VelocityUtils.getOutputPath(templateName, context, genConfig);

                // 写入文件（自动创建 dto 父目录）
                File file = new File(outputPath);
                FileUtil.mkParentDirs(file);
                FileUtil.writeString(content, file, "UTF-8");

                log.info("生成文件: {}", outputPath);
            } catch (Exception e) {
                log.error("生成模板 {} 失败", templateName, e);
                throw new RuntimeException("生成文件失败: " + e.getMessage());
            }
        }
    }

    /**
     * 创建结果Map
     *
     * @param tableName 表名
     * @param success   是否成功
     * @param message   消息
     * @return 结果Map
     */
    private Map<String, Object> createResultMap(String tableName, boolean success, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableName);
        map.put("success", success);
        map.put("message", message);
        return map;
    }

    @Override
    public byte[] downloadCode(String tableName) {
        // 从数据库获取配置的 Schema 信息
        Schema schema = schemaService.findByTableName(tableName);
        if (schema == null) {
            log.warn("表 {} 的配置不存在，使用默认配置", tableName);
            schema = buildSchemaFromTable(tableName);
        }

        // 从数据库获取配置的字段信息
        List<SchemaField> fields;
        try {
            fields = schemaFieldService.findBySchemaId(schema.getId());
            if (fields == null || fields.isEmpty()) {
                log.warn("表 {} 的字段配置为空，从数据库读取字段信息", tableName);
                fields = buildFieldsFromTable(tableName);
            }
        } catch (Exception e) {
            log.error("获取字段配置失败，从数据库读取字段信息", e);
            fields = buildFieldsFromTable(tableName);
        }

        // 如果是主表，加载子表信息
        if ("MASTER".equals(schema.getTableType()) && schema.getId() != null) {
            List<Schema> detailTables = schemaService.findByMasterTableId(schema.getId());
            if (detailTables != null && !detailTables.isEmpty()) {
                // 为每个子表设置className等必要信息
                for (Schema detailTable : detailTables) {
                    String detailClassName = convertToClassName(detailTable.getTableName());
                    detailTable.setClassName(detailClassName);

                    // 设置小写类名（用于变量名）
                    detailTable.setBusinessName(StrUtil.lowerFirst(detailClassName));

                    // 设置功能名称（如果没有的话）
                    if (StrUtil.isBlank(detailTable.getFunctionName())) {
                        detailTable.setFunctionName(detailTable.getName() != null ? detailTable.getName() : detailClassName);
                    }

                    // 设置子表的主表信息
                    detailTable.setMasterTableName(schema.getTableName());
                    detailTable.setMasterClassName(schema.getClassName());
                }
                schema.setDetailTables(detailTables);
                log.info("加载到 {} 个子表", detailTables.size());
            }
        }

        // 准备 Velocity 上下文
        Map<String, Object> context = VelocityUtils.prepareContext(schema, fields, genConfig);

        // 创建 ZIP 输出流
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteOutputStream)) {
            // 根据表类型获取模板列表
            String tableType = schema.getTableType() != null ? schema.getTableType() : "SINGLE";
            Map<String, String> templates = VelocityUtils.getTemplateList(tableType);

            for (Map.Entry<String, String> entry : templates.entrySet()) {
                String templateName = entry.getKey();
                String templatePath = entry.getValue();

                try {
                    // 渲染模板
                    String content = VelocityUtils.render(templatePath, context);

                    // 生成文件路径
                    String outputPath = VelocityUtils.getOutputPath(templateName, context, genConfig);

                    // 统一路径分隔符
                    String zipPath = outputPath.replace("\\", "/");

                    // 添加到 ZIP（包含 DTO 文件）
                    ZipEntry zipEntry = new ZipEntry(zipPath);
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
                    zipOutputStream.closeEntry();

                    log.info("添加文件到ZIP: {}", zipPath);
                } catch (Exception e) {
                    log.error("处理模板 {} 失败", templateName, e);
                }
            }

            zipOutputStream.finish();
        } catch (IOException e) {
            log.error("创建ZIP文件失败", e);
            throw new RuntimeException("创建ZIP文件失败: " + e.getMessage());
        }

        return byteOutputStream.toByteArray();
    }

    /**
     * 从数据库表构建Schema对象
     *
     * @param tableName 表名
     * @return Schema对象
     */
    private Schema buildSchemaFromTable(String tableName) {
        Schema schema = new Schema();
        schema.setTableName(tableName);

        // 将表名转换为类名（去掉前缀，转换为驼峰）
        String className = toClassName(tableName);
        schema.setClassName(className);
        schema.setFunctionName(className);

        // 设置模块名和业务名
        schema.setModuleName(genConfig.getModuleName());
        schema.setBusinessName(StrUtil.lowerFirst(className));

        // 设置权限前缀
        schema.setPermissionPrefix(genConfig.getModuleName() + ":" + StrUtil.lowerFirst(className));

        // 设置编码和名称
        schema.setCode(className);
        schema.setName(className + " " + tableName);

        return schema;
    }

    /**
     * 从数据库表构建字段列表
     *
     * @param tableName 表名
     * @return 字段列表
     */
    private List<SchemaField> buildFieldsFromTable(String tableName) {
        List<SchemaField> fields = new ArrayList<>();

        // BaseEntity中已有的字段，需要过滤掉（对应数据库字段名）
        Set<String> baseEntityFields = new HashSet<>(Arrays.asList(
            "id", "create_time", "update_time", "create_by", "update_by", "remark", "status"
        ));

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            // 获取主键信息
            Set<String> primaryKeys = new HashSet<>();
            try (ResultSet pkRs = metaData.getPrimaryKeys(conn.getCatalog(), null, tableName)) {
                while (pkRs.next()) {
                    primaryKeys.add(pkRs.getString("COLUMN_NAME"));
                }
            }

            // 获取列信息
            try (ResultSet colRs = metaData.getColumns(conn.getCatalog(), null, tableName, null)) {
                while (colRs.next()) {
                    String columnName = colRs.getString("COLUMN_NAME");

                    // 过滤掉BaseEntity中已有的字段
                    if (baseEntityFields.contains(columnName.toLowerCase())) {
                        log.debug("跳过BaseEntity字段: {}", columnName);
                        continue;
                    }

                    SchemaField field = new SchemaField();

                    field.setColumnName(columnName);

                    // 转换为Java字段名（驼峰命名）
                    String javaField = toCamelCase(columnName);
                    field.setJavaField(javaField);
                    field.setCode(javaField);
                    field.setName(javaField);

                    // 设置字段类型
                    String dbType = colRs.getString("TYPE_NAME");
                    String javaType = convertDbTypeToJavaType(dbType);
                    field.setType(dbType);
                    field.setJavaType(javaType);

                    // 设置注释
                    String remark = colRs.getString("REMARKS");
                    field.setComment(StrUtil.isNotBlank(remark) ? remark : columnName);

                    // 设置是否主键
                    field.setIsPk(primaryKeys.contains(columnName) ? "1" : "0");

                    // 设置是否必填
                    String nullable = colRs.getString("IS_NULLABLE");
                    field.setIsRequired("YES".equalsIgnoreCase(nullable) ? "0" : "1");

                    // 设置默认标志
                    field.setIsInsert("1");
                    field.setIsEdit("1");
                    field.setIsList("1");
                    field.setIsQuery("0");

                    // 设置显示类型
                    if (primaryKeys.contains(columnName)) {
                        field.setHtmlType("input");
                        field.setIsEdit("0");  // 主键不可编辑
                    } else if (javaType.equals("Date") || javaType.equals("LocalDateTime")) {
                        field.setHtmlType("datetime");
                    } else if (javaType.equals("Integer")) {
                        field.setHtmlType("input");
                    } else {
                        field.setHtmlType("input");
                    }

                    // 设置查询方式
                    field.setQueryType("EQ");

                    // 设置排序
                    field.setSortNo(fields.size());

                    fields.add(field);
                }
            }

        } catch (SQLException e) {
            log.error("获取表 {} 的字段信息失败", tableName, e);
            throw new RuntimeException("获取表字段信息失败: " + e.getMessage());
        }

        log.info("从表 {} 读取到 {} 个字段（已过滤BaseEntity字段）", tableName, fields.size());
        return fields;
    }

    /**
     * 将表名转换为类名
     * 去掉表前缀（如 t_, sys_, dev_ 等），转换为帕斯卡命名
     *
     * @param tableName 表名
     * @return 类名
     */
    private String toClassName(String tableName) {
        // 去掉常见表前缀
        String name = tableName.replaceAll("^(t_|sys_|dev_|biz_)", "");

        // 转换为帕斯卡命名
        return toPascalCase(name);
    }

    /**
     * 转换为帕斯卡命名（首字母大写的驼峰）
     *
     * @param str 字符串
     * @return 帕斯卡命名字符串
     */
    private String toPascalCase(String str) {
        if (StrUtil.isBlank(str)) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }

        return result.toString();
    }

    /**
     * 转换为驼峰命名
     *
     * @param str 字符串
     * @return 驼峰命名字符串
     */
    private String toCamelCase(String str) {
        String pascalCase = toPascalCase(str);
        return StrUtil.lowerFirst(pascalCase);
    }




    /**
     * 将表名转换为类名
     *
     * @param tableName 表名
     * @return 类名
     */
    private String convertToClassName(String tableName) {
        return toPascalCase(tableName);
    }

    /**
     * 数据库类型转换为Java类型
     *
     * @param dbType 数据库类型
     * @return Java类型
     */
    private String convertDbTypeToJavaType(String dbType) {
        if (dbType == null) {
            return "String";
        }

        String type = dbType.toLowerCase();

        if (type.contains("char") || type.contains("text") || type.contains("json")) {
            return "String";
        } else if (type.contains("bigint")) {
            return "Long";
        } else if (type.contains("int") || type.contains("tinyint")) {
            return "Integer";
        } else if (type.contains("decimal") || type.contains("double") || type.contains("float")) {
            return "Double";
        } else if (type.contains("date") || type.contains("timestamp") || type.contains("datetime")) {
            return "LocalDateTime";
        } else if (type.contains("bit")) {
            return "Boolean";
        } else {
            return "String";
        }
    }
}