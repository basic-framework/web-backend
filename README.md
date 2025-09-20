# 基础脚手架后端 - zl-backend

## 项目简介

`zl-backend` 是一套企业级后端基础脚手架，基于 Spring Boot 构建。该项目采用模块化设计，旨在提供一个可扩展、易维护的后端开发基础架构，适用于快速搭建企业级应用系统。
项目目前处于开发阶段，后续会根据需求进行功能完善和优化。

## 技术栈

- **核心框架**：Spring Boot 3.4.5
- **构建工具**：Maven
- **Java 版本**：Java 17+
- **模块管理**：Maven 多模块项目结构

## 项目结构

```bash
zl-backend/
├── zl-common         # 通用工具类和异常处理模块
├── zl-model          # 数据模型定义模块
├── zl-framework      # 框架核心模块，包含基础配置和通用组件
├── zl-security       # 安全认证与授权模块
├── zl-web            # Web 层模块，包含控制器和入口类
└── zl-extend         # 扩展功能模块
    ├── zl-file       # 文件处理相关模块
    │   ├── zl-file-starter  # 文件服务启动器
    │   └── zl-minio         # MinIO 文件存储实现
    ├── zl-netty      # Netty 网络通信模块
    ├── zl-oshi       # 系统信息采集模块（基于 OSHI）
    ├── zl-springai   # Spring AI 集成模块
    ├── zl-langchain4j # LangChain4j 集成模块
    └── zl-websocket  # WebSocket 通信模块


