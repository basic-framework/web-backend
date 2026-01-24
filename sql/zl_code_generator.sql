-- ====================================================
-- 代码生成器模块数据库表设计
-- ====================================================

-- ----------------------------
-- Table structure for dev_schema_group
-- ----------------------------
DROP TABLE IF EXISTS `dev_schema_group`;
CREATE TABLE `dev_schema_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(50) NOT NULL COMMENT '分组编码',
  `name` varchar(100) NOT NULL COMMENT '分组名称',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据模型分组表';

-- ----------------------------
-- Table structure for dev_schema
-- ----------------------------
DROP TABLE IF EXISTS `dev_schema`;
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
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_schema_group_id` (`schema_group_id`),
  KEY `idx_table_name` (`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据模型表';

-- ----------------------------
-- Table structure for dev_schema_field
-- ----------------------------
DROP TABLE IF EXISTS `dev_schema_field`;
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
  `query_type` varchar(10) DEFAULT 'EQ' COMMENT '查询方式（EQ、NE、GT、LT、LIKE、BETWEEN）',
  `html_type` varchar(20) DEFAULT 'input' COMMENT '显示类型（input、select、datetime、image、file等）',
  `dict_type` varchar(100) DEFAULT NULL COMMENT '字典类型',
  `sort_no` int DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`),
  KEY `idx_schema_id` (`schema_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='模型字段表';

-- ----------------------------
-- 初始化数据示例
-- ----------------------------
-- 插入分组示例
INSERT INTO `dev_schema_group` (`code`, `name`, `remark`) VALUES
('system', '系统管理', '系统管理相关模块'),
('business', '业务模块', '业务相关模块');

-- 插入模型示例（可选，用于测试）
-- INSERT INTO `dev_schema` (`schema_group_id`, `name`, `code`, `table_name`, `function_name`, `module_name`, `business_name`, `permission_prefix`, `remark`) VALUES
-- (1, '用户管理', 'User', 'sys_user', '用户', 'system', 'user', 'system:user', '用户信息管理');
