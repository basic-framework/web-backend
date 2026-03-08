# 基础脚手架后端 - zl-backend

## 项目简介

`zl-backend` 是一套企业级后端基础脚手架，基于 Spring Boot 构建。该项目采用模块化设计，旨在提供一个可扩展、易维护的后端开发基础架构，适用于快速搭建企业级应用系统。
项目提供了完整的安全认证、多模块管理、扩展功能支持等特性，可帮助开发团队快速启动新项目开发。

## 技术栈

### 核心技术
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

### 工具库
- **Hutool 5.8.0.M3**：Java工具库，提供丰富的工具方法
- **Fastjson2 2.0.53**：高性能JSON处理库
- **EasyExcel 3.3.2**：Excel导入导出工具
- **Lombok 1.18.20**：代码简化工具，减少样板代码
- **Jackson 2.18.0**：JSON序列化和反序列化
- **BouncyCastle 1.70**：加密算法实现
- **UserAgentUtils 1.21**：客户端信息解析
- **iTextPDF 5.5.13.3**：PDF导出功能
- **PDFBox 2.0.30**：PDF处理工具

### 扩展技术
- **Netty 4.1.79.Final**：高性能网络通信框架
- **MinIO 8.5.3**：对象存储服务
- **OSHI 6.6.6**：系统信息采集库
- **Spring AI 1.0.0-M6**：AI集成框架，支持多种大模型
- **LangChain4j 1.0.1-beta6**：大语言模型应用开发框架
- **XXL-JOB 3.2.0**：分布式任务调度平台
- **RabbitMQ**：消息队列中间件
- **EMQ MQTT**：物联网消息协议支持
- **WebFlux**：响应式编程支持
- **Jedis**：Redis客户端

## 项目结构

```bash
zl-backend/
├── .gitignore                    # Git忽略文件配置
├── README.md                     # 项目说明文档
├── pom.xml                       # 项目父POM文件
├── docs/                         # 文档目录
│   ├── 大文件处理方案/            # 大文件处理相关文档
│   ├── 权限认证设计/             # 权限认证相关文档
│   ├── 通用功能/                 # 通用功能文档
│   │   ├── 幂等控制方案/         # 幂等性控制设计文档
│   │   ├── 数据脱敏方案/         # 数据脱敏模块详解
│   │   ├── 日志方案/             # 日志系统使用指南
│   │   └── 异常处理设计/         # 全局异常处理设计文档
│   ├── 系统架构/                 # 系统架构文档
│   ├── 扩展插件/                 # 扩展插件文档
│   │   └── 代码生成器/           # 代码生成器文档
│   └── LLM应用/                   # 大语言模型应用文档
├── sql/                          # SQL脚本目录
│   └── zl.sql                   # 初始化数据库脚本
├── zl-common/                    # 通用工具类和异常处理模块
├── zl-common-core/               # 通用核心功能模块
│   ├── zl-common-idempotent/     # 幂等性控制模块
│   ├── zl-common-log/            # AOP日志收集模块
│   └── zl-common-sensitive/      # 数据脱敏模块
├── zl-model/                     # 数据模型定义模块
├── zl-framework/                 # 框架核心模块，包含基础配置和通用组件
├── zl-security/                  # 安全认证与授权模块
├── zl-web/                       # Web 层模块，包含控制器和入口类
├── zl-plugin/                    # 插件模块
│   └── zl-code-generator/        # 代码生成器插件
└── zl-extend/                    # 扩展功能模块
    ├── zl-file/                  # 文件处理相关模块
    │   ├── zl-file-starter       # 文件服务启动器
    │   └── zl-minio              # MinIO 文件存储实现
    ├── zl-netty/                 # Netty 网络通信模块
    ├── zl-oshi/                  # 系统信息采集模块（基于 OSHI）
    ├── zl-springai/              # Spring AI 集成模块
    ├── zl-langchain4j/           # LangChain4j 集成模块
    ├── zl-websocket/            # WebSocket 通信模块
    ├── zl-rabbitmq/             # RabbitMQ 消息队列集成模块
    ├── zl-emqx/                 # EMQ MQTT 物联网服务模块
    └── zl-xxl-job/              # XXL-JOB 分布式任务调度模块
```

## 模块说明

### 核心模块

- **zl-common**：提供通用工具类、异常处理、常量定义等基础功能
- **zl-common-core**：通用核心功能模块，包含幂等性控制、日志收集、数据脱敏等核心功能，已封装为自定义Starter
  - **zl-common-idempotent**：基于AOP的幂等性控制实现，支持注解式防重复提交
  - **zl-common-log**：基于AOP的自定义日志收集模块，支持操作日志记录和查询，采用异步日志记录
  - **zl-common-sensitive**：基于Jackson序列化的数据脱敏模块，支持多种脱敏策略
- **zl-model**：定义数据实体、DTO、VO等数据模型，包含邮箱注册、密码重置等DTO
- **zl-framework**：框架核心配置，包含异步任务管理、安全配置、全局异常处理等
- **zl-security**：认证授权机制，基于Spring Security和JWT实现，支持RBAC权限模型
  - 支持用户名和邮箱双重登录方式
  - 提供邮箱验证码注册和密码重置功能
  - 集成邮件发送服务
- **zl-web**：Web层入口，包含控制器、启动类等

### 插件模块

- **zl-code-generator**：代码生成器插件，支持单表、主子表、树表等多种表类型的代码生成
  - 基于Velocity模板引擎的代码生成
  - 可视化Web配置界面
  - 支持批量生成和代码预览
  - 智能识别主键、字段类型、关联关系
  - 自动过滤BaseEntity基础字段
  - 支持主子表级联操作和树表结构操作

### 扩展模块

- **zl-file**：文件处理模块，提供基于MinIO的文件存储服务，支持大文件分片上传和断点续传
  - **zl-file-starter**：文件服务自动配置启动器
  - **zl-minio**：MinIO对象存储实现
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
- 支持用户名和邮箱双重登录方式
- RBAC权限控制模型（用户-角色-资源）
- 细粒度的API权限控制
- 支持Token过期刷新机制
- 密码加密存储（BCrypt）
- 登录验证码支持
- ThreadLocal用户上下文管理
- 三组件协同认证流程（JwtAuthenticationFilter、UserTokenInterceptor、JwtAuthorizationManager）
- 邮箱验证码功能
  - 邮箱验证码注册：支持通过邮箱验证码完成用户注册
  - 邮箱验证码重置密码：支持通过邮箱验证码重置用户密码
  - 发送频率限制：60秒内只能发送一次验证码
  - 验证码有效期：5分钟
  - 验证码存储：基于Redis存储，支持过期自动清理

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

### 系统功能
- **操作日志**：基于AOP的自定义日志收集模块，支持注解式操作日志记录
  - 记录用户操作行为（增删改查）
  - 支持模块、功能、操作人类别配置
  - 支持请求参数和响应参数的记录
  - 支持排除敏感参数
  - 异步日志记录，不影响业务性能
- **幂等性控制**：基于AOP的注解式防重复提交，支持自定义间隔时间和提示信息
- **数据脱敏**：基于Jackson序列化的数据脱敏功能，支持手机号、身份证、邮箱等多种脱敏策略
- **全局异常处理**：统一的异常处理机制，支持错误码管理和国际化
- **日志系统**：基于SLF4J+Logback的分级日志系统，支持按时间+大小的滚动日志分片配置
  - 滚动日志模式：按时间和大小对日志进行分片
  - 支持多环境日志级别控制
  - 异步日志输出，避免I/O阻塞
- **参数校验**：基于Spring Validation的参数校验框架
- **统一响应格式**：标准化的API响应格式，支持分页和错误信息
- **线程池优化**：核心线程数=CPU核心数+1，最大线程数=2*核心线程数

### 扩展能力
- **代码生成器 V2.0**：可视化代码生成工具，大幅提升开发效率
  - 支持单表、主子表、树表等多种表类型
  - 智能识别字段类型和关联关系
  - 可视化配置界面，支持代码预览
  - 批量生成和下载功能
  - 自动过滤BaseEntity基础字段
  - 支持主子表级联操作和树表结构操作
  - 访问地址：`http://localhost:8080/generator.html`
- 文件存储与管理（支持MinIO）
- 大文件分片上传和断点续传（基于MD5的文件命名冲突解决方案）
- 系统监控（CPU、内存、磁盘等信息）
- AI功能集成（支持通义千问等多种大模型）
  - Spring AI集成：支持Function Calling和向量数据库
  - LangChain4j集成：支持RAG和文档解析
  - MCP协议支持：标准化的AI模型与外部工具交互
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
- **application-file.yml**：文件服务配置，支持MinIO
- **application-extend.yml**：扩展功能配置，包含AI、MQTT、XXL-JOB等配置
- **logback-spring.xml**：日志系统配置，支持多环境日志级别控制

### 核心配置项
- **服务器配置**：默认端口8080，支持自定义上下文路径
- **数据源配置**：MySQL连接信息（Druid连接池），支持连接池监控
- **Redis配置**：缓存服务配置，支持Redisson分布式锁
- **JWT配置**：Token有效期（默认30分钟）、密钥等
- **安全配置**：忽略URL、默认密码、跨域配置等
- **邮件服务配置**：
  - SMTP服务器地址和端口
  - 发件人邮箱账号和授权密码
  - 验证码有效期（默认5分钟）
  - 发送频率限制（默认60秒）
- **文件存储**：支持MinIO对象存储，配置分片上传和断点续传
- **AI服务配置**：
  - Spring AI：支持通义千问等多种大模型
  - LangChain4j：支持OpenAI等模型和向量数据库
  - MCP协议：标准化AI模型与外部工具交互
- **消息队列配置**：RabbitMQ和MQTT配置
- **任务调度配置**：XXL-JOB分布式任务调度配置
- **日志配置**：支持按功能和级别分类输出，可配置输出路径和滚动策略
- **幂等性配置**：支持自定义防重提交间隔时间和提示信息

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
5. 配置邮件服务信息（application-dev.yml），如需使用邮箱验证码功能
6. 根据需要配置文件存储（MinIO）
7. 根据需要配置AI服务、消息队列等扩展功能
8. 执行Maven构建：`mvn clean package`
9. 运行应用：`java -jar zl-web/target/zl-web-1.0-SNAPSHOT.jar`

### 访问API文档
启动成功后，访问以下地址查看API文档：
`http://localhost:8080/doc.html`

### 默认账号
- 用户名：admin
- 密码：123456

### 功能测试
项目提供了多个测试接口，可用于验证各项功能：
- **代码生成器**：访问 `http://localhost:8080/generator.html` 使用可视化代码生成工具
- **邮箱注册测试**：`POST /web/email/send-code` 发送注册验证码，`POST /web/email/register` 完成邮箱注册
- **邮箱登录测试**：`POST /web/login` 支持使用邮箱进行登录
- **密码重置测试**：`POST /web/user/email/resetPw/code` 发送重置密码验证码，`POST /web/user/resetPassword` 完成密码重置
- **操作日志测试**：在带有`@Log`注解的接口上操作，查看系统操作日志记录
- **日志测试**：`GET /web/some-method` - 基础日志测试
- **权限测试**：登录后访问需要权限的接口，验证RBAC权限控制
- **幂等性测试**：在带有`@RepeatSubmit`注解的接口上重复提交，验证防重功能
- **文件上传测试**：使用文件上传接口测试大文件分片上传功能
- **AI功能测试**：配置AI服务后，测试Spring AI或LangChain4j集成功能

## 开发指南

### 模块依赖关系
- **zl-web** 依赖于所有其他模块
- **zl-security** 依赖于 **zl-framework**
- **zl-framework** 依赖于 **zl-common** 和 **zl-model**
- **zl-common-core** 依赖于 **zl-framework**
- **zl-extend** 下的各模块依赖于 **zl-common** 和 **zl-framework**

### 编码规范
- 遵循Java编码规范
- 使用Lombok简化代码
- 分层架构：Controller -> Service -> Repository
- 异常处理统一使用全局异常处理器
- 使用统一的返回结果封装（Result、PageResult）
- 使用ThreadLocal存储用户上下文信息

### 功能开发指南

#### 权限控制开发
```java
// 在Controller方法上添加权限注解
@PreAuthorize("hasAuthority('GET/api/user/list')")
@GetMapping("/list")
public Result<List<User>> getUserList() {
    // 业务逻辑
}

// 获取当前用户信息
LoginVo currentUser = UserUtil.getUser();
Long userId = UserUtil.getUserId();
```

#### 邮箱验证码功能使用
```java
// 发送注册验证码
POST /web/email/send-code
{
    "email": "user@example.com"
}

// 邮箱注册
POST /web/email/register
{
    "email": "user@example.com",
    "username": "testuser",
    "password": "password123",
    "verifyCode": "123456"
}

// 发送密码重置验证码
POST /web/user/email/resetPw/code
{
    "email": "user@example.com"
}

// 重置密码
POST /web/user/resetPassword
{
    "email": "user@example.com",
    "newPassword": "newPassword123",
    "verifyCode": "654321"
}

// 邮箱登录（支持用户名或邮箱登录）
POST /web/login
{
    "account": "user@example.com",  // 可以是用户名或邮箱
    "password": "password123"
}
```

#### 幂等性控制开发
```java
// 在需要防重提交的方法上添加注解
@RepeatSubmit(interval = 5000, timeUnit = TimeUnit.MILLISECONDS)
@PostMapping("/save")
public Result<Void> saveUser(@RequestBody UserDTO userDTO) {
    // 业务逻辑
}
```

#### 数据脱敏开发
```java
// 在实体类字段上添加脱敏注解
public class UserInfo {
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phoneNumber;
    
    @Sensitive(strategy = SensitiveStrategy.ID_CARD)
    private String idCard;
    
    @Sensitive(strategy = SensitiveStrategy.EMAIL)
    private String email;
}

// 实现自定义脱敏控制服务
@Service
public class CustomSensitiveService implements SensitiveService {
    @Override
    public boolean isSensitive() {
        // 根据用户角色或其他条件决定是否脱敏
        UserContext user = UserContext.getCurrent();
        return !user.isAdmin();
    }
}
```

#### 操作日志使用指南
```java
// 在Controller方法上添加操作日志注解
@Log(title = "用户管理", businessType = BusinessType.INSERT)
@PostMapping("/save")
public Result<Void> saveUser(@RequestBody UserDTO userDTO) {
    // 业务逻辑
    return Result.success();
}

// 排除敏感参数
@Log(title = "用户管理", businessType = BusinessType.UPDATE,
     excludeParamNames = {"password", "oldPassword"})
@PutMapping("/update")
public Result<Void> updateUser(@RequestBody UserDTO userDTO) {
    // 业务逻辑
    return Result.success();
}
```

#### 日志使用指南
```java
@Slf4j
@Service
public class UserService {
    public void createUser(String username) {
        log.info("创建用户开始, username: {}", username);
        try {
            // 业务逻辑
            log.info("用户创建成功: {}", username);
        } catch (Exception e) {
            log.error("创建用户失败", e);
        }
    }
}
```

#### 异常处理指南
```java
// 抛出业务异常
if (passwordNotMatch) {
    throw new UserPasswordNotMatchException();
}

// 自定义异常
public class CustomException extends BaseServiceException {
    public CustomException() {
        super(ErrorCode.CUSTOM_ERROR);
    }
}
```

### 扩展开发
- 新增扩展模块请在zl-extend下创建
- 遵循Spring Boot自动配置规范
- 提供配置属性类和自动配置类
- 在META-INF下创建spring.factories或AutoConfiguration.imports文件

### AI功能开发
```java
// Spring AI Function Calling
@Tool(description = "获取指定城市的天气信息")
public String getWeather(@ToolParam(description = "城市名称") String city) {
    return weatherService.getWeather(city);
}

// LangChain4j集成
@Service
public class AIService {
    @Inject
    private ChatLanguageModel model;
    
    public String chat(String message) {
        return model.generate(message);
    }
}
```

## 注意事项
- 项目处于持续开发阶段，功能会不断更新完善
- 使用前请确保环境配置正确
- 生产环境部署时请修改默认密码和密钥
- 扩展模块可根据实际需求选择性引入
- 部分扩展功能需要额外配置第三方服务（如MinIO、RabbitMQ等）

### 功能使用注意事项
- **权限系统**：确保三组件（JwtAuthenticationFilter、UserTokenInterceptor、JwtAuthorizationManager）正确配置，避免ThreadLocal时序问题
- **邮箱功能**：使用邮箱验证码功能前需要配置邮件服务（SMTP服务器、发件人账号等）；验证码存储在Redis中，确保Redis服务正常运行
- **幂等性控制**：注意在Controller方法上添加@RepeatSubmit注解，并合理设置间隔时间
- **操作日志**：使用@Log注解记录操作，注意排除敏感参数；操作日志采用异步记录，不影响业务性能
- **数据脱敏**：脱敏功能仅在JSON序列化时生效，不影响数据库存储；需要实现SensitiveService接口来控制脱敏行为
- **日志系统**：生产环境建议调整日志级别为INFO，避免过多DEBUG日志影响性能；使用按时间+大小的滚动日志分片配置
- **代码生成器**：首次使用需要导入数据库表结构，支持单表、主子表、树表等多种类型的代码生成
- **文件上传**：大文件上传需要确保MinIO服务正常运行，并合理设置分片大小
- **AI功能**：使用AI功能前需要配置相应的API密钥和服务地址
- **全局异常处理**：自定义异常需要继承BaseServiceException，并定义相应的ErrorCode

### 性能优化建议
- 合理使用Redis缓存，减少数据库查询
- 大文件上传时调整分片大小，平衡上传速度和内存占用
- 生产环境启用日志异步输出，避免I/O阻塞
- 定期清理过期的幂等性控制Key，避免Redis内存占用过高

## 联系方式
- 项目地址：[zl-backend](https://github.com/your-repo/zl-backend)
- 问题反馈：[Issues](https://github.com/your-repo/zl-backend/issues)
