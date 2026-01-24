package com.zl.generator.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 代码生成器表自动创建器
 * 在应用启动时检查并创建所需的数据库表
 *
 * @author code-generator
 * @date 2026-01-23
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeneratorTableAutoCreator implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            // 检查并创建 dev_schema_group 表
            createSchemaGroupTable();

            // 检查并创建 dev_schema 表
            createSchemaTable();

            // 检查并创建 dev_schema_field 表
            createSchemaFieldTable();

            // 检查并插入默认分组
            insertDefaultGroups();

            log.info("代码生成器表检查完成");
        } catch (Exception e) {
            log.error("代码生成器表初始化失败", e);
        }
    }

    /**
     * 检查表是否存在
     */
    private boolean tableExists(String tableName) {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
        return count != null && count > 0;
    }

    /**
     * 创建 dev_schema_group 表
     */
    private void createSchemaGroupTable() {
        if (tableExists("dev_schema_group")) {
            log.info("表 dev_schema_group 已存在，跳过创建");
            return;
        }

        String sql = """
            CREATE TABLE `dev_schema_group` (
              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
              `code` varchar(50) NOT NULL COMMENT '分组编码',
              `name` varchar(100) NOT NULL COMMENT '分组名称',
              `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
              `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
              `create_by` bigint DEFAULT NULL COMMENT '创建者',
              `update_by` bigint DEFAULT NULL COMMENT '更新者',
              `remark` varchar(500) DEFAULT NULL COMMENT '备注',
              PRIMARY KEY (`id`),
              UNIQUE KEY `uk_code` (`code`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据模型分组表'
            """;

        jdbcTemplate.execute(sql);
        log.info("成功创建表 dev_schema_group");
    }

    /**
     * 创建 dev_schema 表
     */
    private void createSchemaTable() {
        if (tableExists("dev_schema")) {
            log.info("表 dev_schema 已存在，跳过创建");
            return;
        }

        String sql = """
            CREATE TABLE `dev_schema` (
              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
              `schema_group_id` bigint DEFAULT NULL COMMENT '分组ID',
              `name` varchar(100) NOT NULL COMMENT '模型名称',
              `code` varchar(100) NOT NULL COMMENT '模型编码',
              `table_name` varchar(100) NOT NULL COMMENT '数据库表名',
              `function_name` varchar(100) DEFAULT NULL COMMENT '功能名称',
              `module_name` varchar(50) DEFAULT NULL COMMENT '模块名称',
              `business_name` varchar(50) DEFAULT NULL COMMENT '业务名称',
              `permission_prefix` varchar(100) DEFAULT NULL COMMENT '权限前缀',
              `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
              `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
              `create_by` bigint DEFAULT NULL COMMENT '创建者',
              `update_by` bigint DEFAULT NULL COMMENT '更新者',
              `remark` varchar(500) DEFAULT NULL COMMENT '备注',
              PRIMARY KEY (`id`),
              KEY `idx_schema_group_id` (`schema_group_id`),
              KEY `idx_table_name` (`table_name`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据模型表'
            """;

        jdbcTemplate.execute(sql);
        log.info("成功创建表 dev_schema");
    }

    /**
     * 创建 dev_schema_field 表
     */
    private void createSchemaFieldTable() {
        if (!tableExists("dev_schema_field")) {
            String sql = """
                CREATE TABLE `dev_schema_field` (
                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                  `schema_id` bigint NOT NULL COMMENT '所属模型ID',
                  `name` varchar(100) NOT NULL COMMENT '字段名称',
                  `code` varchar(100) NOT NULL COMMENT '字段编码',
                  `column_name` varchar(100) NOT NULL COMMENT '数据库列名',
                  `type` varchar(50) NOT NULL COMMENT '字段类型',
                  `java_type` varchar(100) DEFAULT NULL COMMENT 'Java类型',
                  `java_field` varchar(100) DEFAULT NULL COMMENT 'Java字段名',
                  `comment` varchar(500) DEFAULT NULL COMMENT '字段注释',
                  `is_pk` char(1) DEFAULT '0' COMMENT '是否主键（0否 1是）',
                  `is_required` char(1) DEFAULT '0' COMMENT '是否必填（0否 1是）',
                  `is_insert` char(1) DEFAULT '1' COMMENT '是否插入字段（0否 1是）',
                  `is_edit` char(1) DEFAULT '1' COMMENT '是否编辑字段（0否 1是）',
                  `is_list` char(1) DEFAULT '1' COMMENT '是否列表显示（0否 1是）',
                  `is_query` char(1) DEFAULT '0' COMMENT '是否查询字段（0否 1是）',
                  `query_type` varchar(10) DEFAULT 'EQ' COMMENT '查询方式',
                  `html_type` varchar(20) DEFAULT 'input' COMMENT '显示类型',
                  `dict_type` varchar(100) DEFAULT NULL COMMENT '字典类型',
                  `sort_no` int DEFAULT 0 COMMENT '排序',
                  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                  `create_by` bigint DEFAULT NULL COMMENT '创建者',
                  `update_by` bigint DEFAULT NULL COMMENT '更新者',
                  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                  PRIMARY KEY (`id`),
                  KEY `idx_schema_id` (`schema_id`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模型字段表'
                """;

            jdbcTemplate.execute(sql);
            log.info("成功创建表 dev_schema_field");
        } else {
            // 检查并添加缺失的 remark 列
            addColumnIfNotExists("dev_schema_field", "remark", "varchar(500) DEFAULT NULL COMMENT '备注'");
        }
    }

    /**
     * 检查并添加列（如果不存在）
     */
    private void addColumnIfNotExists(String tableName, String columnName, String columnDefinition) {
        try {
            String checkSql = "SELECT COUNT(*) FROM information_schema.columns " +
                             "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, tableName, columnName);

            if (count == null || count == 0) {
                String alterSql = "ALTER TABLE `" + tableName + "` ADD COLUMN `" + columnName + "` " + columnDefinition;
                jdbcTemplate.execute(alterSql);
                log.info("成功为表 {} 添加列 {}", tableName, columnName);
            }
        } catch (Exception e) {
            log.warn("添加列 {} 到表 {} 失败: {}", columnName, tableName, e.getMessage());
        }
    }

    /**
     * 插入默认分组
     */
    private void insertDefaultGroups() {
        String checkSql = "SELECT COUNT(*) FROM dev_schema_group WHERE code = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, "system");

        if (count != null && count > 0) {
            log.info("默认分组已存在，跳过插入");
            return;
        }

        String sql = """
            INSERT INTO dev_schema_group (code, name, remark) VALUES
            ('system', '系统管理', '系统管理相关模块'),
            ('business', '业务模块', '业务相关模块')
            """;

        jdbcTemplate.update(sql);
        log.info("成功插入默认分组数据");
    }
}
