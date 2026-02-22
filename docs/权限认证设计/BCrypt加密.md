你这段代码中使用的 `BCryptPasswordEncoder` 是基于 **BCrypt 哈希算法** 实现的密码加密方式，这也是 Spring Security 中推荐的、业界主流的密码加密方案。我会帮你把这种加密方式的原理、特点和代码中的具体使用讲清楚。

### 一、核心答案：加密方式是 BCrypt

`BCryptPasswordEncoder.encode(password)` 方法的底层是 **BCrypt 哈希算法**，它不是简单的单向哈希（如 MD5），而是一种专门为密码设计的**自适应哈希函数**，核心特性是 “慢” 且 “带盐（Salt）”，能有效抵御暴力破解。

### 二、BCrypt 加密的核心原理

#### 1. 什么是 “盐（Salt）”？

- 盐是随机生成的一串字符，BCrypt 会自动为每个密码生成**唯一的随机盐**（不需要你手动存储盐）。
- 对比传统 MD5：如果直接用 MD5 加密密码（如 MD5 ("123456")），黑客可以通过彩虹表（预计算的哈希值库）快速破解；而 BCrypt 会把盐和密码混合后再哈希，且每个密码的盐都不同，彩虹表失效。

#### 2. 自适应哈希（强度可调）

BCrypt 的加密强度（`strength`）可以配置（对应代码中的`int strength`参数），取值范围 4~31，默认 10。

- 强度越高，哈希计算的迭代次数越多（迭代次数 = 2^strength），加密 / 解密越慢。
- 比如强度 10，迭代次数是 1024 次；强度 12，迭代次数是 4096 次。这种 “慢” 的特性可以让黑客的暴力破解成本指数级增加。

#### 3. BCrypt 加密后的字符串结构

加密后的密码字符串格式如下（共 60 位）：

```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
├──┬─┴─┬─┴────────────────┬───────────────────────────────────
│  │   │                    │
│  │   │                    加密后的密码哈希（31位）
│  │   │
│  │   随机生成的盐（22位）
│  │
│  加密强度（10）
│
BCrypt版本（2a/2b/2y）
```

从这个结构能看出来：**盐和加密强度都直接存储在最终的加密字符串中**，所以你只需要存这一个字符串到数据库，验证时就能从中解析出盐和强度，无需额外存储盐。

### 三、代码中的具体实现解析

#### 1. 构造方法（默认配置）

你代码中注入的 `BCryptPasswordEncoder` 如果是无参构造（`new BCryptPasswordEncoder()`），会使用默认配置：

- 版本：`$2A`（BCryptVersion.$2A）
- 强度：10（如果传 - 1，会用默认值 10）
- 随机数生成器：自动创建`SecureRandom`

#### 2. 加密与验证流程

```
// 1. 加密（注册时）
String rawPassword = "123456"; // 用户输入的原始密码
String encodedPassword = passwordEncoder.encode(rawPassword); // 加密后的值，如上面的60位字符串

// 2. 验证（登录时）
String inputPassword = "123456"; // 用户登录输入的密码
// matches方法会自动从encodedPassword中解析盐和强度，和输入密码对比
boolean isMatch = passwordEncoder.matches(inputPassword, encodedPassword); 
```

- 验证时不需要手动解密（BCrypt 是不可逆哈希），而是用相同的盐和强度，对输入密码重新哈希，再对比哈希结果是否一致。

#### 3. 代码中构建用户的逻辑

```
String encodedPassword = passwordEncoder.encode(password);
User newUser = User.builder()
        .username(username)
        .password(encodedPassword) // 只存加密后的字符串，不存原始密码
        .email(email)
        .build();
```

这是标准且安全的做法：**数据库中只存储 BCrypt 加密后的 60 位字符串，永远不存储原始密码**，即使数据库泄露，黑客也无法直接获取用户密码。

### 四、BCrypt 的核心优势（为什么推荐用）

1. **自动加盐**：无需手动管理盐，每个密码的盐唯一，避免盐泄露或复用的风险。
2. **自适应哈希**：可以通过调整强度适配硬件升级（比如未来硬件性能提升，可提高强度，保持破解难度）。
3. **不可逆**：无法从加密后的字符串反推原始密码，即使开发者也无法知道用户的原始密码。
4. **Spring Security 原生支持**：和 Spring 生态无缝集成，验证逻辑简单。

### 总结

1. 你代码中使用的是 **BCrypt 哈希算法** 进行密码加密，由 Spring Security 的`BCryptPasswordEncoder`实现。
2. BCrypt 的核心是**自动生成随机盐 + 自适应迭代哈希**，解决了传统 MD5/SHA1 等哈希算法易被彩虹表破解的问题。
3. 使用时只需存储加密后的 60 位字符串，验证时通过`matches`方法对比，无需手动处理盐和哈希逻辑。