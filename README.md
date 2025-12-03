# 基础脚手架后端 - zl-backend

## 项目简介

`zl-backend` 是一套企业级后端基础脚手架，基于 Spring Boot 构建。该项目采用模块化设计，旨在提供一个可扩展、易维护的后端开发基础架构，适用于快速搭建企业级应用系统。
项目提供了完整的安全认证、多模块管理、扩展功能支持等特性，可帮助开发团队快速启动新项目开发。

## 技术栈

- **核心框架**：Spring Boot 3.4.5
- **持久层**：MyBatis 3.0.4、PageHelper 2.1.0
- **数据库**：MySQL 8.3.0
- **数据库连接池**：Druid 1.2.24
- **缓存**：Redis
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
    - ........

## 项目结构

```bash
zl-backend/
├── .gitignore       # Git忽略文件配置
├── README.md        # 项目说明文档
├── doc/             # 文档目录
│   ├── ThreadLocal.png
│   └── 后台登录验证流程 .jpg
├── sql/             # SQL脚本目录
│   └── zl.sql       # 初始化数据库脚本
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
    └── zl-rabbitmq/ # RabbitMQ 消息队列集成模块
```

## 模块说明

### 核心模块

- **zl-common**：提供通用工具类、异常处理、常量定义等基础功能
- **zl-model**：定义数据实体、DTO、VO等数据模型
- **zl-framework**：框架核心配置，包含异步任务管理、安全配置等
- **zl-security**：认证授权机制，基于Spring Security和JWT实现
- **zl-web**：Web层入口，包含控制器、启动类等

### 扩展模块

- **zl-file**：文件处理模块，提供基于MinIO的文件存储服务
- **zl-netty**：高性能网络通信模块，基于Netty框架实现
- **zl-oshi**：系统信息采集模块，可获取服务器硬件、系统信息
- **zl-springai**：AI功能集成，基于Spring AI框架
- **zl-langchain4j**：LangChain4j集成，提供大语言模型应用能力
- **zl-websocket**：WebSocket通信模块，支持实时消息推送
- **zl-rabbitmq**：消息队列集成，支持异步消息处理

## 功能特性

### 安全认证
- 基于JWT的身份认证机制
- RBAC权限控制模型（用户-角色-资源）
- 细粒度的API权限控制
- 支持Token过期刷新机制

### 数据访问
- 基于MyBatis的ORM映射
- 分页查询支持
- 事务管理
- 数据源监控（Druid）

### API文档
- 基于Knife4j的OpenAPI 3规范API文档
- 自动生成接口文档
- 在线接口测试

### 扩展能力
- 文件存储与管理（支持MinIO）
- 系统监控（CPU、内存、磁盘等信息）
- AI功能集成（支持多种大模型）
- WebSocket实时通信
- 高性能网络通信（Netty）
- 消息队列支持（RabbitMQ）

## 配置说明

### 主要配置文件
- **application.yml**：核心配置文件
- **application-dev.yml**：开发环境配置
- **application-file.yml**：文件服务配置
- **application-extend.yml**：扩展功能配置

### 核心配置项
- 服务器端口：默认8080
- 数据源配置：MySQL连接信息
- Redis配置：缓存服务配置
- JWT配置：Token有效期、密钥等
- 安全配置：忽略URL、默认密码等

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
5. 执行Maven构建：`mvn clean package`
6. 运行应用：`java -jar zl-web/target/zl-web-1.0-SNAPSHOT.jar`

### 访问API文档
启动成功后，访问以下地址查看API文档：
`http://localhost:8080/doc.html`W

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

## 注意事项
- 项目处于持续开发阶段，功能会不断更新完善
- 使用前请确保环境配置正确
- 生产环境部署时请修改默认密码和密钥
