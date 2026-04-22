## D1. 设计目标与原则
+ **统一规范**：强制统一所有微服务的依赖版本、响应格式、异常处理和编码风格。
+ **开箱即用**：基于 Spring Boot Starter 机制，引入依赖即自动生效，减少重复配置。
+ **屏蔽底层**：将技术复杂度（如分布式锁、数据权限SQL拼接、TraceID透传）封装在底层，让业务开发专注于业务逻辑。
+ **模块解耦**：采用“8+N”模块化设计，业务按需引入，拒绝“上帝依赖”。

---

## 2. 工程结构设计
### 2.1 顶级工程 (`trust-cloud-framework`)
+ **定位**：Maven Root Project (Packaging = POM)。
+ **职责**：
    - 管理所有子模块。
    - **BOM (Bill of Materials)**：在 `<dependencyManagement>` 中锁定 Spring Boot, Spring Cloud, MyBatis Plus, Hutool ，Lombok、apollo等所有第三方库的版本号。

### 2.2 核心模块拆分 (Module List)
```plain
trust-cloud-framework (Root POM / BOM)
 ├── trust-common-core (基础定义、工具类、异常、TTL) --> core
 │── trust-common-log (日志、SkyWalking、审计) --> core
 │── trust-common-config (Apollo、动态配置) --> core + Apollo Client
 │
 ├── trust-common-redis (Redis、Redisson锁) --> core
 ├── trust-common-db (MyBatis Plus、数据脱敏、多数据源) --> core
 ├── trust-common-mq (RabbitMQ/RocketMQ 增强) --> core
 ├── trust-common-file (S3/MinIO) --> core
 │
 ├── trust-common-web (WebMvc配置、Knife4j、全局异常) --> core
 │── trust-common-rpc (Feign、负载均衡、熔断)
 │── trust-common-security (Security、数据权限、JWT) --> redis
```

  
 

| 模块名称 | 定位 | 依赖关系 | 核心功能描述 |
| :--- | :--- | :--- | :--- |
| `trust-common-core` | **基础底座** | 无 | 1. **全链路上下文** (`UserContextHolder` 基于 TTL)。   2. 统一响应体 (`R<T>`) 与全局枚举。   3. JWT 解析工具、基础 Utils (Hutool)。 |
| `trust-common-log` | 日志组件 | core | 1. 统一 `logback-spring.xml` 模板。   2. 集成 SkyWalking TraceID。   3. **审计日志切面** (`@AuditLog`)。 |
| `trust-common-config` | 配置中心组件 | core、apollo-client | 1. 自动注入默认的 Namespaces（例如 application, TEST.common 等），确保所有微服务都能读取到公共配置（如 Redis 地址、MQ 地址），无需每个服务重复配置   2. 动态刷新: 确保 @RefreshScope 或 Apollo 的 ContextRefresher 机制生效，使得日志级别、限流阈值等变更能实时生效。 |
| `trust-common-redis` | **缓存组件** | core | 1. `RedisTemplate` 序列化配置 (JSON)。   2. **分布式锁** (`Redisson` 自动配置)。   3. 缓存工具类。 |
| `trust-common-db` | **数据组件** | core | 1. **MyBatis Plus** 分页、防全表更新插件。   2. `MetaObjectHandler` (自动填充创建人/时间)。   3. 多数据源配置。 |
| `trust-common-mq` | **消息组件** | core | 1. RabbitMQ 序列化 (JSON)。   2. 消息可靠投递回调封装。   3.  ContextPostProcessor (发送端注入)    4.ContextAspect (消费端恢复) |
| `trust-common-file` | **文件组件** | core | 1. 封装 MinIO/S3 标准接口 (`OssTemplate`)。   2. 统一文件上传/下载/预览逻辑。 |
| `trust-common-web` | **Web增强** | core | 1. **精度控制** (Long/BigDecimal 转 String)。   2. **全局异常拦截** (统一返回 JSON)。   3. 接口文档 (Knife4j) 自动配置。   4. ContextFilter (Web入口上下文注入), |
| `trust-common-security` | **安全组件** | web, redis | 1. Spring Security 过滤器链。   2. **数据权限拦截器** (`@DataScope`)。   3. Token 校验与踢人下线逻辑。 |
| `trust-common-rpc` | **RPC组件** | core | 1. OpenFeign 配置。   2. **Header透传拦截器** (自动传递 UserId, TraceId) ContextFeignInterceptor (RPC出口上下文透传)，。 |


## 3. 核心技术细节
### 3.1 全链路上下文透传
在微服务架构中，确保 **UserID (用户ID)**、**TraceID (链路追踪ID)** 等核心上下文信息，在 **HTTP请求 -> 线程池子线程 -> Feign调用 -> MQ异步消息** 的整个流转过程中，**永不丢失，永不串号**。

#### 0. 内功心法：底层容器与线程池 (Foundation)
_模块位置：_`trust-common-core`

#### 0.1 容器选型：TTL
严禁使用 JDK 原生 `ThreadLocal`，必须使用阿里开源的 `TransmittableThreadLocal`。

```java
public class UserContextHolder {
    // 核心：使用 TTL，支持父子线程传递
    private static final ThreadLocal<UserContext> CONTEXT = new TransmittableThreadLocal<>();

    public static void setContext(UserContext ctx) { CONTEXT.set(ctx); }
    public static UserContext getContext() { return CONTEXT.get(); }
    public static void clear() { CONTEXT.remove(); }
}
```

#### 0.2 线程池自动接管 (Auto-Wrapping)
业务开发最容易忘记包装线程池。脚手架通过 `@Primary` 强制接管 Spring 的默认异步线程池。

```java
@Configuration
public class ContextAutoConfiguration {
    @Bean
    @Primary
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // ...配置核心参数...
        executor.initialize();
        // 核心：使用 TTL 装饰器包装，让 @Async 自动具备上下文传递能力
        return TtlExecutors.getTtlExecutor(executor);
    }
}
```

---

#### 1. 第一板斧：Web 入口与出口 (HTTP In/Out)
_模块位置：_`trust-common-web`

负责将 HTTP Header 中的信息“搬运”到 TTL 中，请求结束时“打扫战场”。

+ **组件**：`TrustContextFilter` (实现 `Filter` 接口，优先级 `Ordered.HIGHEST_PRECEDENCE`)
+ **逻辑**：

```java
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
    HttpServletRequest request = (HttpServletRequest) req;
    try {
        // 1. 从 Header 提取
        String userId = request.getHeader("x-user-id");
        String traceId = request.getHeader("x-trace-id"); // 或对接 SkyWalking 上下文
        
        // 2. 注入 TTL (子线程也能读到了)
        UserContextHolder.setContext(new UserContext(userId, traceId));
        
        // 3. 放行
        chain.doFilter(req, res);
    } finally {
        // 4. 必须清理！防止 Tomcat 线程复用导致串号
        UserContextHolder.clear();
    }
}
```

---

#### 2. 第二板斧：RPC 传递 (Feign Out)
_模块位置：_`trust-common-rpc`

负责在发起微服务调用时，从 TTL 中取出信息，“塞回”下游请求的 Header 中。

+ **组件**：`ContextFeignInterceptor` (实现 `RequestInterceptor`)
+ **逻辑**：

```java
public void apply(RequestTemplate template) {
    // 从 TTL 读取（得益于 TTL，这里即使在 @Async 线程里也能读到）
    UserContext ctx = UserContextHolder.getContext();
    if (ctx != null) {
        // 传递给下游服务
        template.header("x-user-id", ctx.getUserId());
        template.header("x-trace-id", ctx.getTraceId());
    }
}
```

---

#### 3. 第三板斧：MQ 异步传递 (RabbitMQ)
_模块位置：_`trust-common-mq`

最难的环节。MQ 消息脱离了当前线程，必须把上下文序列化到消息体元数据中。

##### 3.1 发送端 (Producer)
+ **组件**：`ContextMessagePostProcessor` (实现 `MessagePostProcessor`)
+ **配置**：在 `RabbitTemplate` Bean 初始化时注入 `setBeforePublishPostProcessors`。
+ **逻辑**：

```java
public Message postProcessMessage(Message message) {
    UserContext ctx = UserContextHolder.getContext();
    if (ctx != null) {
        // 将上下文写入 RabbitMQ 的 Header (Properties)
        message.getMessageProperties().setHeader("x-user-id", ctx.getUserId());
        message.getMessageProperties().setHeader("x-trace-id", ctx.getTraceId());
    }
    return message;
}
```

##### 3.2 消费端 (Consumer)
+ **组件**：`RabbitListenerAspect` (AOP 切面)
+ **切点**：`@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)`
+ **逻辑**：

```java
@Around("rabbitListenerPointcut()")
public Object cut(ProceedingJoinPoint point) {
    Object arg = point.getArgs()[0];
    // 兼容 Message 对象和业务对象
    if (arg instanceof Message) {
        Message msg = (Message) arg;
        // 1. 从 MQ Header 恢复上下文到 TTL
        String userId = msg.getMessageProperties().getHeader("x-user-id");
        UserContextHolder.setContext(new UserContext(userId));
    }
    
    try {
        return point.proceed();
    } finally {
        // 2. 消费结束必须清理，因为消费者线程池也是复用的
        UserContextHolder.clear();
    }
}
```

---

#### 总结
这套方案通过 **TTL 解决线程间传递**，通过 **Filter/Interceptor/Aspect 解决进程间传递**，形成了一个完美的闭环。无论业务逻辑多复杂（A调B，B异步发MQ，MQ消费者再调C），UserContext 始终如影随形。

### 3.2 精度丢失终极解决方案 (`trust-common-web`)
**痛点**：Java 的 `Long` (19位) 传给前端 JS (17位) 会自动截断；`BigDecimal` 传给前端容易变成不准确的浮点数。  
**方案**：利用 Jackson 自动配置。

```java
@Bean
public Jackson2ObjectMapperBuilderCustomizer customizer() {
    return builder -> {
        // 全局强制序列化为 String
        builder.serializerByType(Long.class, ToStringSerializer.instance);
        builder.serializerByType(BigDecimal.class, ToStringSerializer.instance);
    };
}
```

### 3.3 数据权限“无感”拦截 (`trust-common-security`)
**痛点**：每个查询都要手写 `where dept_id = ?`，容易漏写导致数据泄露。  
**方案**：

+ 定义注解 `@DataScope(deptAlias="d", userAlias="u")`。
+ 实现 MyBatis Plus `DataPermissionHandler` 接口。
+ 解析 Token 中的角色数据范围（本部门/本人/全公司），**在 SQL 执行前动态拼接 WHERE 条件**。

---

## 5. 业务应用使用指南
**角色：业务开发人员 (外包/正式)**

### 第一步：继承 Parent (强制)**
新建微服务（如 `fixed-income-service`）时，`pom.xml` 必须继承：

```xml
<parent>
    <groupId>com.trust</groupId>
    <artifactId>trust-cloud-framework</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</parent>

```

### 第二步：按需引入模块
**场景 A：标准的业务服务（需要API、连库、鉴权）**

```xml
<dependencies>
    <dependency>
        <groupId>com.trust</groupId>
        <artifactId>trust-common-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.trust</groupId>
        <artifactId>trust-common-security</artifactId>
    </dependency>
    <dependency>
        <groupId>com.trust</groupId>
        <artifactId>trust-common-db</artifactId>
    </dependency>
    <dependency>
        <groupId>com.trust</groupId>
        <artifactId>trust-common-rpc</artifactId>
    </dependency>
</dependencies>

```

**场景 B：简单的计算/工具服务（无Web，无库）**

```xml
<dependencies>
    <dependency>
        <groupId>com.trust</groupId>
        <artifactId>trust-common-core</artifactId>
    </dependency>
</dependencies>

```

### 第三步：配置与启动
在 `application.yml` 中配置必要的参数（因使用了 AutoConfiguration，大部分配置已有默认值）：

```yaml
trust:
  security:
    ignore-urls: /auth/login, /public/** # 扩展配置
  swagger:
    title: "固收业务服务"
```

---

## 6. 开发规范红线 (Governance)
1. **严禁手动引入版本号**：所有业务 POM 中，引入 `spring-boot` 或 `hutool` 等依赖时，**绝对不允许**写 `<version>` 标签，必须由脚手架统一管理。
2. **严禁私自封装工具类**：如果发现 `trust-common-core` 缺少某个 Util，**提 PR 给架构组**，严禁在业务项目中复制粘贴 `DateUtil`。
3. **日志规范**：严禁使用 `System.out.println`，必须使用 `@Slf4j`。

## 7.架构治理机制 (防止腐化)
### 1. 谁有权修改脚手架？
**原则：集权管理，开源共建。**

+ **唯一发布权 (Owner)**：
    - **平台工程中心·基础架构维护人员** 
    - **权限**：只有他们权限执行 `mvn deploy` 推送到 Nexus 私服的 Release 仓库。其他人的账号只有 Read 权限。
+ **修改提议权 (Contributor)**：
    - 所有开发人员（含外包）发现 Bug 或需要新工具类时，**严禁私自修改**。
    - **流程**：在 Gitlab 提交 **Merge Request (MR)** -> 关联 Jira 需求 -> 指定架构师 Code Review。

### 2. PR/MR 审核标准 (Code Review Checklist)
架构师在点击 "Merge" 按钮前，必须核对以下红线：

1. **无业务侵入**：脚手架里绝不能包含“固收”、“客户”等具体业务逻辑。
2. **无传递依赖**：新增的依赖必须标记为 `<optional>true</optional>`，除非是核心必须的。
3. **兼容性检查**：修改工具类签名时，是否会导致旧版本业务系统编译失败？
4. **AutoConfig**：新功能是否提供了 `EnableXxx` 开关？默认是否关闭？

### 3. 版本发布节奏 (Release Cycle)
+ **SNAPSHOT (快照版)**：
    - **节奏**：随时发布。
    - **场景**：异地团队开发新功能急需底层支持时，使用 `1.1.0-SNAPSHOT`。
    - _注意：生产环境严禁使用 SNAPSHOT 版本。_
+ **RELEASE (正式版)**：
    - **节奏**：**双周一版**（与业务迭代对齐）。
    - **策略**：
        * `1.0.0` -> `1.0.1` (BugFix，无痛升级)。
        * `1.0.0` -> `1.1.0` (新功能，业务按需升级)。
        * `1.0.0` -> `2.0.0` (破坏性变更，需召开架构变更评审会)。

### 4. 向后兼容策略 (Compatibility)
+ **弃用原则**：如果要删除一个方法，必须先标记 `@Deprecated` 并保留至少 **2个小版本**（约1个月），给业务团队修改缓冲期。
+ **强制升级令**：对于严重安全漏洞（如 Log4j），由PMO下发“强制升级令”，所有业务系统必须在 3 天内升级 Parent 版本，否则**禁止上线**。

### 5. 违规惩罚
+ **红线行为**：业务代码里出现私自封装的 `RedisUtil`（而不是用脚手架的）、私自引入的 `fastjson`（脚手架规定用 Jackson）。
+ **惩罚**：代码扫描（SonarQube）直接报错，CI 流水线中断，**打回重写**。该次延期计入外包团队绩效。

---

