# 基础脚手架后端 - zl-backend

## 项目简介

`zl-backend` 是一套企业级后端基础脚手架，基于 Spring Boot 构建。该项目采用模块化设计，旨在提供一个可扩展、易维护的后端开发基础架构，适用于快速搭建企业级应用系统。
项目提供了完整的安全认证、多模块管理、扩展功能支持等特性，可帮助开发团队快速启动新项目开发。

## 技术栈

- **核心框架**：Spring Boot 3.4.5
- **持久层**：MyBatis 3.0.4、PageHelper 2.1.0
- **数据库**：MySQL 8.3.0
- **数据库连接池**：Druid 1.2.24
- **缓存**：Redis、Redisson 3.13.6
- **安全认证**：Spring Security、JWT 0.12.6
- **API文档**：Knife4j 4.5.0（基于OpenAPI 3）
- **构建工具**：Maven
- **Java 版本**：Java 17+
- **模块管理**：Maven 多模块项目结构
- **工具库**：
    - Hutool 5.8.0.M3（Java工具库）
    - Fastjson2 2.0.53（JSON处理）
    - EasyExcel 3.3.2（Excel处理）
    - Lombok 1.18.20（代码简化工具）
    - Jackson 2.18.0（JSON处理）
    - BouncyCastle 1.70（加密算法）
- **扩展技术**：
    - Netty 4.1.79.Final（高性能网络通信）
    - MinIO 8.5.3（对象存储）
    - OSHI 6.6.6（系统信息采集）
    - Spring AI 1.0.0-M6（AI集成）
    - LangChain4j 1.0.1-beta6（大语言模型应用）
    - XXL-JOB 3.2.0（分布式任务调度）
    - RabbitMQ（消息队列）
    - EMQ MQTT（物联网消息协议）

## 项目结构

```bash
zl-backend/
├── .gitignore       # Git忽略文件配置
├── README.md        # 项目说明文档
├── doc/             # 文档目录
│   ├── 依赖关系.drawio
│   ├── 大文件处理方案/
│   └── 权限认证设计/
├── sql/             # SQL脚本目录
│   ├── zl.sql       # 初始化数据库脚本
│   └── test_learning_lesson_data.sql
├── pom.xml          # 项目父POM文件
├── zl-common/       # 通用工具类和异常处理模块
├── zl-model/        # 数据模型定义模块
├── zl-framework/    # 框架核心模块，包含基础配置和通用组件
├── zl-security/     # 安全认证与授权模块
├── zl-web/          # Web 层模块，包含控制器和入口类
└── zl-extend/       # 扩展功能模块
    ├── zl-file/     # 文件处理相关模块
    │   ├── zl-file-starter  # 文件服务启动器
    │   └── zl-minio         # MinIO 文件存储实现
    ├── zl-netty/    # Netty 网络通信模块
    ├── zl-oshi/     # 系统信息采集模块（基于 OSHI）
    ├── zl-springai/ # Spring AI 集成模块
    ├── zl-langchain4j # LangChain4j 集成模块
    ├── zl-websocket/ # WebSocket 通信模块
    ├── zl-rabbitmq/ # RabbitMQ 消息队列集成模块
    ├── zl-emqx/     # EMQ MQTT 物联网服务模块
    └── zl-xxl-job/  # XXL-JOB 分布式任务调度模块
    
```

## 模块说明

### 核心模块

- **zl-common**：提供通用工具类、异常处理、常量定义等基础功能
- **zl-model**：定义数据实体、DTO、VO等数据模型
- **zl-framework**：框架核心配置，包含异步任务管理、安全配置等
- **zl-security**：认证授权机制，基于Spring Security和JWT实现
- **zl-web**：Web层入口，包含控制器、启动类等

### 扩展模块

- **zl-file**：文件处理模块，提供基于MinIO和阿里云OSS的文件存储服务，支持大文件分片上传和断点续传
- **zl-netty**：高性能网络通信模块，基于Netty框架实现，支持TCP/UDP协议和自定义心跳检测
- **zl-oshi**：系统信息采集模块，可获取服务器CPU、内存、磁盘等硬件信息，支持系统监控
- **zl-springai**：AI功能集成，基于Spring AI框架，支持多种大模型接入和向量数据库
- **zl-langchain4j**：LangChain4j集成，提供大语言模型应用能力，支持RAG和文档解析
- **zl-websocket**：WebSocket通信模块，支持实时消息推送和双向通信
- **zl-rabbitmq**：消息队列集成，支持异步消息处理和延迟消息
- **zl-emqx**：EMQ MQTT 物联网服务模块，支持设备连接和消息订阅发布
- **zl-xxl-job**：分布式任务调度模块，支持定时任务和分片广播任务

## 功能特性

### 安全认证
- 基于JWT的身份认证机制
- RBAC权限控制模型（用户-角色-资源）
- 细粒度的API权限控制
- 支持Token过期刷新机制
- 密码加密存储（BCrypt）
- 登录验证码支持

### 数据访问
- 基于MyBatis的ORM映射
- 分页查询支持（PageHelper）
- 事务管理
- 数据源监控（Druid）
- Redis缓存支持
- Redisson分布式锁

### API文档
- 基于Knife4j的OpenAPI 3规范API文档
- 自动生成接口文档
- 在线接口测试
- 接口分组管理

### 扩展能力
- 文件存储与管理（支持MinIO和阿里云OSS）
- 大文件分片上传和断点续传
- 系统监控（CPU、内存、磁盘等信息）
- AI功能集成（支持通义千问等多种大模型）
- 向量数据库支持（Redis）
- WebSocket实时通信
- 高性能网络通信（Netty）
- 消息队列支持（RabbitMQ）
- 物联网服务支持（EMQ MQTT）
- 分布式任务调度（XXL-JOB）
- PDF导出功能
- Excel导入导出

## 配置说明

### 主要配置文件
- **application.yml**：核心配置文件，包含服务器、安全、MyBatis等基础配置
- **application-dev.yml**：开发环境配置，包含数据库、Redis、AI服务等配置
- **application-file.yml**：文件服务配置，支持MinIO和阿里云OSS
- **application-extend.yml**：扩展功能配置，包含AI、MQTT、XXL-JOB等配置

### 核心配置项
- 服务器端口：默认8080
- 数据源配置：MySQL连接信息（Druid连接池）
- Redis配置：缓存服务配置，支持Redisson分布式锁
- JWT配置：Token有效期（默认30分钟）、密钥等
- 安全配置：忽略URL、默认密码等
- 文件存储：支持MinIO和阿里云OSS两种存储方式
- AI服务：支持通义千问等多种大模型
- 消息队列：RabbitMQ和MQTT配置
- 任务调度：XXL-JOB分布式任务调度配置

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 部署步骤
1. 克隆项目代码
2. 导入MySQL脚本（sql/zl.sql）
3. 配置数据库连接信息（application-dev.yml）
4. 配置Redis连接信息（application-dev.yml）
5. 根据需要配置文件存储（MinIO或阿里云OSS）
6. 根据需要配置AI服务、消息队列等扩展功能
7. 执行Maven构建：`mvn clean package`
8. 运行应用：`java -jar zl-web/target/zl-web-1.0-SNAPSHOT.jar`

### 访问API文档
启动成功后，访问以下地址查看API文档：
`http://localhost:8080/doc.html`

### 默认账号
- 用户名：admin
- 密码：123456

## 开发指南

### 模块依赖关系
- **zl-web** 依赖于所有其他模块
- **zl-security** 依赖于 **zl-framework**
- **zl-framework** 依赖于 **zl-common** 和 **zl-model**
- **zl-extend** 下的各模块依赖于 **zl-common** 和 **zl-framework**

### 编码规范
- 遵循Java编码规范
- 使用Lombok简化代码
- 分层架构：Controller -> Service -> Repository
- 异常处理统一使用全局异常处理器
- 使用统一的返回结果封装（Result、PageResult）
- 使用ThreadLocal存储用户上下文信息

### 扩展开发
- 新增扩展模块请在zl-extend下创建
- 遵循Spring Boot自动配置规范
- 提供配置属性类和自动配置类
- 在META-INF下创建spring.factories或AutoConfiguration.imports文件

## 注意事项
- 项目处于持续开发阶段，功能会不断更新完善
- 使用前请确保环境配置正确
- 生产环境部署时请修改默认密码和密钥
- 扩展模块可根据实际需求选择性引入
- 部分扩展功能需要额外配置第三方服务（如MinIO、RabbitMQ等）

## 联系方式
- 项目地址：[zl-backend](https://github.com/your-repo/zl-backend)
- 问题反馈：[Issues](https://github.com/your-repo/zl-backend/issues)
