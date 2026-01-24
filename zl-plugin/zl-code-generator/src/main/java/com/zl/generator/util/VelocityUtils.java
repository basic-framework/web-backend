package com.zl.generator.util;

import cn.hutool.core.util.StrUtil;
import com.zl.generator.config.GenConfig;
import com.zl.generator.domain.Schema;
import com.zl.generator.domain.SchemaField;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Velocity 模板工具类
 *
 * @author GuihaoLv
 * @date 2026-01-23
 */
public class VelocityUtils {

    private static VelocityEngine velocityEngine;

    static {
        try {
            // 初始化 Velocity 引擎
            Properties props = new Properties();
            props.put("resource.loaders", "class");
            props.put("resource.loader.class.description", "Velocity Classpath Resource Loader");
            props.put("resource.loader.class.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            props.put("input.encoding", "UTF-8");
            props.put("output.encoding", "UTF-8");

            velocityEngine = new VelocityEngine(props);
            velocityEngine.init();
        } catch (Exception e) {
            throw new RuntimeException("Velocity 初始化失败", e);
        }
    }

    /**
     * 获取模板列表（根据表类型选择不同模板）
     *
     * @param tableType 表类型：SINGLE-单表, MASTER-主表, DETAIL-子表, TREE-树表
     * @return 模板列表
     */
    public static Map<String, String> getTemplateList(String tableType) {
        Map<String, String> templates = new LinkedHashMap<>();

        if ("MASTER".equals(tableType)) {
            // 主表模板
            templates.put("domain.java.vm", "vm/java/domainMaster.java.vm");
            templates.put("vo.java.vm", "vm/java/vo.java.vm");
            templates.put("bo.java.vm", "vm/java/bo.java.vm");
            templates.put("dto.java.vm", "vm/java/dto.java.vm");
            templates.put("mapper.java.vm", "vm/java/mapper.java.vm");
            templates.put("mapper.xml.vm", "vm/xml/mapper.xml.vm");
            templates.put("service.java.vm", "vm/java/service.java.vm");
            templates.put("serviceImpl.java.vm", "vm/java/serviceImplMaster.java.vm");
            templates.put("controller.java.vm", "vm/java/controller.java.vm");
        } else if ("DETAIL".equals(tableType)) {
            // 子表模板
            templates.put("domain.java.vm", "vm/java/domainDetail.java.vm");
            templates.put("vo.java.vm", "vm/java/vo.java.vm");
            templates.put("bo.java.vm", "vm/java/bo.java.vm");
            templates.put("dto.java.vm", "vm/java/dto.java.vm");
            templates.put("mapper.java.vm", "vm/java/mapperDetail.java.vm");
            templates.put("mapper.xml.vm", "vm/xml/mapperDetail.xml.vm");
            templates.put("service.java.vm", "vm/java/service.java.vm");
            templates.put("serviceImpl.java.vm", "vm/java/serviceImpl.java.vm");
            // 子表不需要独立的Controller
        } else if ("TREE".equals(tableType)) {
            // 树表模板
            templates.put("domain.java.vm", "vm/java/domainTree.java.vm");
            templates.put("vo.java.vm", "vm/java/vo.java.vm");
            templates.put("bo.java.vm", "vm/java/bo.java.vm");
            templates.put("dto.java.vm", "vm/java/dto.java.vm");
            templates.put("mapper.java.vm", "vm/java/mapperTree.java.vm");
            templates.put("mapper.xml.vm", "vm/xml/mapperTree.xml.vm");
            templates.put("service.java.vm", "vm/java/serviceTree.java.vm");
            templates.put("serviceImpl.java.vm", "vm/java/serviceImplTree.java.vm");
            templates.put("controller.java.vm", "vm/java/controllerTree.java.vm");
        } else {
            // 默认单表模板
            templates.put("domain.java.vm", "vm/java/domain.java.vm");
            templates.put("vo.java.vm", "vm/java/vo.java.vm");
            templates.put("bo.java.vm", "vm/java/bo.java.vm");
            templates.put("dto.java.vm", "vm/java/dto.java.vm");
            templates.put("mapper.java.vm", "vm/java/mapper.java.vm");
            templates.put("service.java.vm", "vm/java/service.java.vm");
            templates.put("serviceImpl.java.vm", "vm/java/serviceImpl.java.vm");
            templates.put("controller.java.vm", "vm/java/controller.java.vm");
            templates.put("mapper.xml.vm", "vm/xml/mapper.xml.vm");
        }

        return templates;
    }

    /**
     * 获取模板列表（保持向后兼容，默认单表）
     *
     * @return 模板列表
     */
    public static Map<String, String> getTemplateList() {
        return getTemplateList("SINGLE");
    }

    /**
     * 准备 Velocity 上下文
     *
     * @param schema    模型
     * @param fields    字段列表
     * @param genConfig 生成器配置
     * @return Velocity 上下文
     */
    public static Map<String, Object> prepareContext(Schema schema, List<SchemaField> fields, GenConfig genConfig) {
        Map<String, Object> context = new HashMap<>();

        // 基础信息
        context.put("tableName", schema.getTableName());
        context.put("tableComment", schema.getRemark());
        context.put("functionName", schema.getFunctionName());
        context.put("author", genConfig.getAuthor());
        context.put("datetime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        context.put("packageName", genConfig.getPackageName());

        // 类名处理
        String className = convertClassName(schema.getTableName(), genConfig);
        schema.setClassName(className);
        context.put("ClassName", className);
        context.put("classname", StrUtil.lowerFirst(className));

        // 模块和业务名称
        context.put("moduleName", StrUtil.isNotBlank(schema.getModuleName()) ? schema.getModuleName() : "system");
        context.put("businessName", StrUtil.isNotBlank(schema.getBusinessName()) ? schema.getBusinessName() : StrUtil.lowerFirst(className));

        // 权限前缀
        context.put("permissionPrefix", StrUtil.isNotBlank(schema.getPermissionPrefix()) ? schema.getPermissionPrefix() : context.get("moduleName") + ":" + context.get("businessName"));

        // 表类型相关信息
        context.put("tableType", schema.getTableType() != null ? schema.getTableType() : "SINGLE");

        // 主子表相关
        if (schema.getMasterTableName() != null) {
            context.put("masterTableName", schema.getMasterTableName());
        }
        if (schema.getMasterClassName() != null) {
            context.put("masterClassName", schema.getMasterClassName());
        }
        if (schema.getRelationField() != null) {
            context.put("relationField", schema.getRelationField());
        }
        if (schema.getDetailTables() != null && !schema.getDetailTables().isEmpty()) {
            context.put("detailTables", schema.getDetailTables());
        }

        // 树表相关
        if (schema.getTreeParentField() != null) {
            context.put("treeParentField", schema.getTreeParentField());
        }
        if (schema.getTreeChildrenField() != null) {
            context.put("treeChildrenField", schema.getTreeChildrenField());
        } else {
            context.put("treeChildrenField", "children");
        }

        // 处理字段信息
        List<Map<String, Object>> columns = new ArrayList<>();
        Set<String> importList = new HashSet<>();
        Map<String, Object> pkColumn = null;

        for (SchemaField field : fields) {
            Map<String, Object> column = new HashMap<>();
            column.put("columnName", field.getColumnName());
            column.put("columnComment", field.getComment());
            column.put("javaField", field.getJavaField());
            column.put("javaType", field.getJavaType());
            column.put("isPk", field.getIsPk());
            column.put("isRequired", field.getIsRequired());
            column.put("isInsert", field.getIsInsert());
            column.put("isEdit", field.getIsEdit());
            column.put("isList", field.getIsList());
            column.put("isQuery", field.getIsQuery());
            column.put("queryType", field.getQueryType());
            column.put("htmlType", field.getHtmlType());
            column.put("dictType", field.getDictType());

            // 收集导入包
            addImport(importList, field.getJavaType());

            if ("1".equals(field.getIsPk())) {
                pkColumn = column;
            }

            columns.add(column);
        }

        context.put("columns", columns);
        context.put("pkColumn", pkColumn);
        context.put("importList", importList);

        return context;
    }

    /**
     * 渲染模板
     *
     * @param templatePath 模板路径
     * @param context      上下文
     * @return 渲染后的内容
     */
    public static String render(String templatePath, Map<String, Object> context) {
        try {
            Template template = velocityEngine.getTemplate(templatePath, "UTF-8");
            VelocityContext velocityContext = new VelocityContext(context);
            StringWriter writer = new StringWriter();
            template.merge(velocityContext, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("模板渲染失败: " + templatePath, e);
        }
    }

    /**
     * 获取输出路径（已包含 dto.java.vm 的路径逻辑）
     *
     * @param templateName 模板名称
     * @param context      上下文
     * @param genConfig    生成器配置
     * @return 输出路径
     */
    public static String getOutputPath(String templateName, Map<String, Object> context, GenConfig genConfig) {
        String packageName = (String) context.get("packageName");
        String moduleName = (String) context.get("moduleName");
        String ClassName = (String) context.get("ClassName");
        String classname = (String) context.get("classname");

        String basePath = genConfig.getOutputDir();
        String packagePath = packageName.replace(".", "/");

        String outputPath;
        switch (templateName) {
            case "domain.java.vm":
                outputPath = basePath + "/" + packagePath + "/domain/" + ClassName + ".java";
                break;
            case "vo.java.vm":
                outputPath = basePath + "/" + packagePath + "/vo/" + ClassName + "Vo.java";
                break;
            case "dto.java.vm":
                outputPath = basePath + "/" + packagePath + "/dto/" + ClassName + "Dto.java";
                break;
            case "bo.java.vm":
                outputPath = basePath + "/" + packagePath + "/bo/" + ClassName + "Bo.java";
                break;
            case "mapper.java.vm":
                outputPath = basePath + "/" + packagePath + "/mapper/" + ClassName + "Mapper.java";
                break;
            case "mapper.xml.vm":
                outputPath = genConfig.getXmlOutputDir() + "/" + moduleName + "/" + ClassName + "Mapper.xml";
                break;
            case "service.java.vm":
                outputPath = basePath + "/" + packagePath + "/service/" + ClassName + "Service.java";
                break;
            case "serviceImpl.java.vm":
                outputPath = basePath + "/" + packagePath + "/service/impl/" + ClassName + "ServiceImpl.java";
                break;
            case "controller.java.vm":
                outputPath = basePath + "/" + packagePath + "/controller/" + ClassName + "Controller.java";
                break;
            default:
                outputPath = basePath + "/" + templateName;
        }

        return outputPath;
    }

    /**
     * 转换表名为类名
     *
     * @param tableName 表名
     * @param genConfig 生成器配置
     * @return 类名
     */
    private static String convertClassName(String tableName, GenConfig genConfig) {
        // 去除表前缀
        if (genConfig.getAutoRemovePre() && StrUtil.isNotBlank(genConfig.getTablePrefix())) {
            String[] prefixes = genConfig.getTablePrefix().split(",");
            for (String prefix : prefixes) {
                if (tableName.startsWith(prefix)) {
                    tableName = tableName.substring(prefix.length());
                    break;
                }
            }
        }

        // 下划线转驼峰
        return toPascalCase(tableName);
    }

    /**
     * 下划线转帕斯卡命名（首字母大写）
     *
     * @param str 下划线字符串
     * @return 帕斯卡命名
     */
    private static String toPascalCase(String str) {
        if (StrUtil.isBlank(str)) {
            return str;
        }

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
     * 添加导入包
     *
     * @param importList 导入列表
     * @param javaType   Java类型
     */
    private static void addImport(Set<String> importList, String javaType) {
        if (StrUtil.isBlank(javaType)) {
            return;
        }

        switch (javaType) {
            case "LocalDate":
                importList.add("java.time.LocalDate");
                break;
            case "LocalDateTime":
                importList.add("java.time.LocalDateTime");
                break;
            case "BigDecimal":
                importList.add("java.math.BigDecimal");
                break;
            case "List":
                importList.add("java.util.List");
                break;
            case "Map":
                importList.add("java.util.Map");
                break;
        }
    }
}