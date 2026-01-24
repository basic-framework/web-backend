-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: zl-basic
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `dev_schema`
--

DROP TABLE IF EXISTS `dev_schema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `table_type` varchar(20) DEFAULT 'SINGLE' COMMENT '表类型：SINGLE-单表,\r\n     MASTER-主表, DETAIL-子表, TREE-树表',
  `master_table_id` bigint DEFAULT NULL COMMENT '主表ID（子表使用）',
  `relation_field` varchar(64) DEFAULT NULL COMMENT '关联字段名（子表中指向主表的外键字段）',
  `tree_parent_field` varchar(64) DEFAULT 'parent_id' COMMENT '树表的父字段名（如：parent_id）',
  `tree_children_field` varchar(64) DEFAULT 'children' COMMENT '树表的子节点集合字段名（如：children）',
  PRIMARY KEY (`id`),
  KEY `idx_schema_group_id` (`schema_group_id`),
  KEY `idx_table_name` (`table_name`),
  KEY `idx_master_table_id` (`master_table_id`),
  KEY `idx_table_type` (`table_type`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据模型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dev_schema`
--

LOCK TABLES `dev_schema` WRITE;
/*!40000 ALTER TABLE `dev_schema` DISABLE KEYS */;
INSERT INTO `dev_schema` VALUES (19,1,'部门树表','Department','sys_department','部门树表','system','department','system:department',0,'2026-01-24 12:04:46','2026-01-24 12:05:44',NULL,NULL,NULL,'TREE',NULL,NULL,'parent_id','children');
/*!40000 ALTER TABLE `dev_schema` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dev_schema_field`
--

DROP TABLE IF EXISTS `dev_schema_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_schema_id` (`schema_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8927 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='模型字段表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dev_schema_field`
--

LOCK TABLES `dev_schema_field` WRITE;
/*!40000 ALTER TABLE `dev_schema_field` DISABLE KEYS */;
INSERT INTO `dev_schema_field` VALUES (8913,19,'部门ID','id','id','BIGINT','Integer','id','部门ID','1','1','1','1','1','0','EQ','input',NULL,1,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8914,19,'父部门ID（0表示根节点）','parentId','parent_id','BIGINT','Integer','parentId','父部门ID（0表示根节点）','0','0','1','1','1','0','EQ','input',NULL,2,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8915,19,'部门名称','deptName','dept_name','VARCHAR','String','deptName','部门名称','0','1','1','1','1','0','EQ','input',NULL,3,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8916,19,'部门编码','deptCode','dept_code','VARCHAR','String','deptCode','部门编码','0','0','1','1','1','0','EQ','input',NULL,4,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8917,19,'负责人','leader','leader','VARCHAR','String','leader','负责人','0','0','1','1','1','0','EQ','input',NULL,5,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8918,19,'联系电话','phone','phone','VARCHAR','String','phone','联系电话','0','0','1','1','1','0','EQ','input',NULL,6,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8919,19,'邮箱','email','email','VARCHAR','String','email','邮箱','0','0','1','1','1','0','EQ','input',NULL,7,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8920,19,'显示顺序','sortNo','sort_no','INT','Integer','sortNo','显示顺序','0','0','1','1','1','0','EQ','input',NULL,8,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8921,19,'状态（0正常 1停用）','status','status','INT','Integer','status','状态（0正常 1停用）','0','0','1','1','1','0','EQ','input',NULL,9,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8922,19,'创建时间','createTime','create_time','DATETIME','LocalDate','createTime','创建时间','0','0','1','1','1','0','EQ','input',NULL,10,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8923,19,'更新时间','updateTime','update_time','DATETIME','LocalDate','updateTime','更新时间','0','0','1','1','1','0','EQ','input',NULL,11,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8924,19,'创建人','createBy','create_by','BIGINT','Integer','createBy','创建人','0','0','1','1','1','0','EQ','input',NULL,12,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8925,19,'更新人','updateBy','update_by','BIGINT','Integer','updateBy','更新人','0','0','1','1','1','0','EQ','input',NULL,13,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL),(8926,19,'备注','remark','remark','VARCHAR','String','remark','备注','0','0','1','1','1','0','EQ','input',NULL,14,0,'2026-01-24 12:05:52','2026-01-24 12:06:07',NULL,NULL,NULL);
/*!40000 ALTER TABLE `dev_schema_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dev_schema_group`
--

DROP TABLE IF EXISTS `dev_schema_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据模型分组表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dev_schema_group`
--

LOCK TABLES `dev_schema_group` WRITE;
/*!40000 ALTER TABLE `dev_schema_group` DISABLE KEYS */;
INSERT INTO `dev_schema_group` VALUES (1,'system','系统管理',0,'2026-01-23 21:38:55','2026-01-23 21:38:55',NULL,NULL,'系统管理相关模块'),(2,'business','业务模块',0,'2026-01-23 21:38:55','2026-01-23 21:38:55',NULL,NULL,'业务相关模块');
/*!40000 ALTER TABLE `dev_schema_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_department`
--

DROP TABLE IF EXISTS `sys_department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_department` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门ID（0表示根节点）',
  `dept_name` varchar(100) NOT NULL COMMENT '部门名称',
  `dept_code` varchar(50) DEFAULT NULL COMMENT '部门编码',
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `sort_no` int DEFAULT '0' COMMENT '显示顺序',
  `status` int DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort_no` (`sort_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门树表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_department`
--

LOCK TABLES `sys_department` WRITE;
/*!40000 ALTER TABLE `sys_department` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT (now()) COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_ot` (`oper_time`),
  KEY `idx_sys_oper_log_s` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_resource`
--

DROP TABLE IF EXISTS `sys_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `resource_no` varchar(20) NOT NULL DEFAULT '0' COMMENT '资源编号',
  `parent_resource_no` varchar(20) NOT NULL DEFAULT '0' COMMENT '父资源编号',
  `resource_name` varchar(50) NOT NULL COMMENT '资源名称',
  `resource_type` varchar(5) NOT NULL DEFAULT '' COMMENT '资源类型：s平台 c目录 m菜单 r按钮',
  `request_path` varchar(200) NOT NULL DEFAULT '' COMMENT '请求地址',
  `label` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `sort_no` int DEFAULT '0' COMMENT '排序',
  `icon` varchar(100) DEFAULT '#' COMMENT '图标',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限资源表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_resource`
--

LOCK TABLES `sys_resource` WRITE;
/*!40000 ALTER TABLE `sys_resource` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `label` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `sort_no` int DEFAULT NULL COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '数据状态（0正常 1停用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_resource`
--

DROP TABLE IF EXISTS `sys_role_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色资源Id',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `resource_no` varchar(20) NOT NULL DEFAULT '0' COMMENT '资源编号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '数据状态（0正常 1停用）',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色资源关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_resource`
--

LOCK TABLES `sys_role_resource` WRITE;
/*!40000 ALTER TABLE `sys_role_resource` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_role_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `phone_number` varchar(20) DEFAULT '' COMMENT '手机号',
  `email` varchar(100) DEFAULT '' COMMENT '邮箱',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态（0-正常，1-禁用）',
  `create_by` varchar(20) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(20) DEFAULT NULL COMMENT '修改者',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `avatar` varchar(300) DEFAULT NULL COMMENT '头像',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'lgh','$2a$10$1a9QbQQG3DnnDhSoD28D7.d4ZXsbatJ/6gCkVRB.M5k/Nfz2XxPA2','','',0,NULL,NULL,NULL,'2026-01-04 16:50:54','2026-01-04 16:50:54',NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户角色关联Id',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '数据状态（0正常 1停用）',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-24 12:10:40
