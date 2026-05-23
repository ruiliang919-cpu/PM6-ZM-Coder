# 全项目代码审查报告

**日期：** 2026-05-23
**分支：** PM6-ZM-Claude
**审查范围：** 全项目（Java 后端 + Vue 前端）
**审查维度：** 安全 / 架构 / 后端代码质量 / 前端代码质量
**总计发现：** 约 138 项 (Critical 21 / High 47 / Medium 51 / Low 19)

---

## 一、安全审查 (5 Critical / 8 High / 9 Medium / 4 Low)

### Critical

| # | 问题 | 位置 |
|---|------|------|
| 1 | Sa-Token 拦截器整段被注释掉，所有 API 请求无认证检查 | `server/ruoyi-framework/.../SaTokenConfig.java:33-55` |
| 2 | 所有 `@SaCheckPermission` 注解被注释，IoT 写操作无权限控制（任何登录用户都能发设备指令） | 全部 `ruoyi-zm/.../controller/zm/**/*Controller.java` |
| 3 | `Runtime.exec()` 命令注入 — Modbus 寄存器值直接拼接到 shell 命令执行，攻击者可执行任意系统命令 | `server/ruoyi-zm/.../modbus/util/BasicProcessImageListener.java:49-54` |
| 4 | JWT 签名密钥硬编码在配置文件中 | `server/ruoyi-admin/.../application.yml:126` |
| 5 | 生产环境数据库密码硬编码在配置文件 | `server/ruoyi-admin/.../application-prod.yml:25,32` |

### High

| # | 问题 | 位置 |
|---|------|------|
| 6 | MQTT 明文 TCP 传输无 TLS，密码硬编码 | `application-prod.yml:2-4`, `MqttConfig.java:35-37` |
| 7 | Swagger/Actuator 端点暴露且无认证 | `application.yml:142-147` |
| 8 | 153 个 npm 漏洞 (13 critical, 49 high) | `web/package.json` |
| 9 | CORS 配置 `allowedOriginPatterns("*")` + `allowCredentials(true)` — 任何网站可发起认证请求 | `ResourcesConfig.java:34-36` |
| 10 | 暴力破解保护形同虚设 (999次尝试/1分钟锁定) | `application.yml:60-63` |
| 11 | XSS 过滤器未覆盖 `/zm/**` 路径 | `application.yml:222-225` |
| 12 | Redis 生产环境无 TLS | `application-prod.yml:48-52` |
| 13 | Token 空闲超时未设置 (24小时内令牌均可复用) | `application.yml:112-114` |

### Medium

| # | 问题 | 位置 |
|---|------|------|
| 14 | WebSocket CORS 允许任意来源 | `WebSocketConfig.java:37,43` |
| 15 | MySQL 开发环境 SSL 禁用 | `application-create.yml:58,66` |
| 16 | Spring Boot 2.7.13 已停止维护 (2023-11 EOL)，无安全补丁 | `server/pom.xml:17` |
| 17 | 数据库种子脚本中设备密码全为 admin/admin | `database/zm.sql:105-124` |
| 18 | IoT 写操作端点无限流保护 | 全部 write Controller |
| 19 | JWT 历史弱密钥在 git 历史中可见 | 所有分支 git history |
| 20 | 邮件 SMTP 凭据占位 | `application-create.yml:150-154` |
| 21 | HikariCP 连接泄漏检测阈值过高 (60s) | `application-prod.yml:42` |
| 22 | Modbus HTTP 端点被排除在认证之外 | `application.yml` security.excludes |

### Low

| # | 问题 | 位置 |
|---|------|------|
| 23 | 代码生成器页面使用 v-html | `web/src/views/tool/gen/index.vue:179` |
| 24 | 未配置 Content-Security-Policy 头 | 全局 |
| 25 | Sa-Token 版本略旧 (1.35.0) | `server/pom.xml:27` |
| 26 | SnakeYAML 手动锁定版本表明存在已知 CVE | `server/pom.xml:268-272` |

---

## 二、架构审查 (38 项发现)

### Critical

| # | 问题 | 位置 |
|---|------|------|
| 1 | `ruoyi-framework` 反向依赖 `ruoyi-zm`（框架层不应依赖业务模块），根因是 `MpSqlInjector` 类放错了位置 | `ruoyi-framework/pom.xml:71`, `MybatisPlusConfig.java:104-107` |
| 2 | 43 个 ZM Controller 中有 21 个直接注入 Mapper Bean，绕过 Service 层 | 各 zm Controller |
| 3 | 43 个 ZM Controller 中有 19 个直接操作 `RedisTemplate`，无缓存抽象 | 各 zm Controller |
| 4 | `WriteController` 上帝类 — 1582 行 | `WriteController.java` |
| 5 | `Update03DataService` 上帝类 — 1583 行 | `send/Update03DataService.java` |

### High

| # | 问题 | 位置 |
|---|------|------|
| 6 | `@Async` 注解由 `ScheduledExecutorService` 执行（应用用任务型线程池跑定时任务型工作） | `AsyncConfig.java:40-42` |
| 7 | `@ExceptionHandler(NotLoginException.class)` 被注释，认证失败信息泄露给客户端 | `GlobalExceptionHandler.java:60-65` |
| 8 | RuntimeException/Exception 处理器直接返回 `e.getMessage()` 给客户端，泄露内部错误 | `GlobalExceptionHandler.java:136-151` |
| 9 | `DevBaseDeviceServiceImpl` 用静态字段替代 Redis 缓存，无 TTL、无线程安全 | `DevBaseDeviceServiceImpl.java:184-196` |
| 10 | Modbus 存在两套并行实现 — modbus4j (活跃) + Netty CarTcpNettyServer (已注释但代码未删) | `CarTcpNettyServer.java`, `RtuHandler.java`, `RtuWriteUtil.java` |
| 11 | Redis PubSub 订阅者直接调用 Controller 方法（`WriteController.workModule()`），层次混乱 | `RedisSubscriber.java:22-51` |
| 12 | com.ruoyi.cache 包有 21 个类，形成"影子 Service 层" | `cache/` 目录 |
| 13 | Maven profile 与 Spring profile 冲突 (`pom.xml` 设 dev，`application.yml` 硬编码 prod) | `pom.xml:52-80`, `application.yml:74-75` |
| 14 | 配置文件中有真实默认凭据（非占位符） | `application-create.yml:59-60`, `application-prod.yml:24-25` |
| 15 | 缓存键命名无规范 — 前缀不一致、嵌套层级随意 | 全局 Redis 操作 |

---

## 三、后端代码质量 (8 Critical / 9 High / 10 Medium / 6 Low)

### Critical

| # | 问题 | 位置 |
|---|------|------|
| 1 | NPE: `selectOne()` 返回 null 时直接调用 `.getDeviceName()` | `ModbusTCPManager.java:202-205` |
| 2 | NPE: `selectById()` 返回 null 时直接调用 `.getDeviceName()` | `RecordFaultController.java:224` |
| 3 | NPE: `Keys.getCreateTCP()` 对 null device 直接访问 `.getDeviceNo()` 等属性 | `Keys.java:38-44` |
| 4 | `Key.getCreateTCP()` 静默吞异常 + 返回全 null 的空对象，导致下游 NPE | `Key.java:90-108` |
| 5 | Redis `KEYS` 命令每 5 秒执行，会阻塞 Redis（生产环境可能引起延迟尖刺） | `RecordFaultController.java:62` |
| 6 | Redis `KEYS` 命令在快照恢复路径中使用 | `DeviceStateSnapshotServiceImpl.java:126` |
| 7 | `DListUtil` 多线程共享可变状态无同步保护 | `DListUtil.java:23-26` |
| 8 | `DeviceCache.getDcDimNum()` 计算完数据后返回 `new HashMap<>()` 空 Map | `DeviceCache.java:127-152` |

### High

| # | 问题 | 位置 |
|---|------|------|
| 9 | `WriteController.updateLoop1()` 用 `synchronized` 锁整个 Controller，串行化所有请求 | `WriteController.java:145` |
| 10 | `MqttPublisher.publish()` 错误日志为空字符串 `log.error("", e)` | `MqttPublisher.java:47-49` |
| 11 | `Recover.java` 350 行方法中 22 个 try-catch 全部吞异常 (catch 体为空) | `Recover.java:34-346` |
| 12 | `RecordFaultController.allFaults()` 每隔 5 秒全表扫描设备表 | `RecordFaultController.java:52-57` |
| 13 | `LeakageCache` 对纯 Redis 读取加了 `@Transactional` | `LeakageCache.java:27` |
| 14 | `ModbusTCPManager.getOneByCache()` 缓存/DB 都 miss 时返回全 null 对象 | `ModbusTCPManager.java:223-235` |
| 15 | `DListUtil` 缓存失效存在 check-then-act 竞态条件 | `DListUtil.java:28-37` |
| 16 | `ModbusTCPManager` 连接池健康检查定时任务被注释 | `ModbusTCPManager.java:144-149` |
| 17 | Disruptor 依赖引入但未使用 | `ruoyi-zm/pom.xml:82-86` |

### Medium

| # | 问题 | 位置 |
|---|------|------|
| 18 | `Modbus4jReadUtil` 使用 `e.printStackTrace()` 代替日志框架 | `Modbus4jReadUtil.java:29,51,74,97` |
| 19 | `CarTcpNettyChannelInboundHandlerAdapter` 使用 `cause.printStackTrace()` | `CarTcpNettyChannelInboundHandlerAdapter.java:59` |
| 20 | `PointController` 使用 `redisTemplate.keys()` | `PointController.java:34` |
| 21 | `ServletUtils.renderString()` 使用 `e.printStackTrace()` | `ServletUtils.java:141` |
| 22 | `DeviceCache.getCabinetList()` 6 层嵌套 try-catch，静默默认值替代 | `DeviceCache.java:62-124` |
| 23 | `MqttMessageHandler` 残留 `main()` 测试方法 | `MqttMessageHandler.java:282-289` |
| 24 | `MqttPublisher` 残留 `main()` + 注释掉的 `@Scheduled` | `MqttPublisher.java:64-103` |
| 25 | `WebSocketConfig` 认证拒绝时不记录日志 | `WebSocketConfig.java:58-71` |
| 26 | `MqRecover.java` 整个类被注释掉但仍保留 | `mqtt/MqRecover.java` |
| 27 | `DeviceFlag` getter 无同步，setter 有同步 | `DeviceFlag.java:13-25` |

### Low

| # | 问题 | 位置 |
|---|------|------|
| 28 | 魔数 845 在多处硬编码出现 | `MqttMessageHandler.java:197` 等 |
| 29 | 测试文件用 `System.out.println` 代替断言 | 多个 Test 文件 |
| 30 | `DeviceCache` 的 `@Lazy` 注解无意义 | `DeviceCache.java:25` |

---

## 四、前端代码质量 (3 Critical / 15 High / 16 Medium / 7 Low)

### Critical

| # | 问题 | 位置 |
|---|------|------|
| 1 | `cantCloseWindow()` 在浏览器模式下未定义，抛出 ReferenceError | `web/src/store/modules/user.js:64` |
| 2 | `window.ipcRenderer` 在浏览器模式下未检查就访问，TypeError 崩溃 | `web/src/App.vue:80,86` |
| 3 | **PollingMixin 不存在** — commit 声称已添加但实际没有，20+ 组件各自复制粘贴轮询逻辑 | 全局 |

### High

| # | 问题 | 位置 |
|---|------|------|
| 4 | 多个组件 `startTimer()` 不先 `clearInterval` 旧定时器，导致定时器泄漏 | 4+ 组件 |
| 5 | API 调用缺少 `.catch()` / `.finally()`，加载 spinner 卡死 | 多个组件 |
| 6 | **261 个** `console.log/warn/error` 泄露设备数据到浏览器控制台 | 101 个文件 |
| 7 | `deepClone` 逻辑错误 — 条件恒为 true，对 `0`/`''`/`false` 也抛异常 | `web/src/utils/index.js:340` |
| 8 | **30+ 组件**完全相同的 navibar device watcher + table style 方法 | 全局 |
| 9 | **PowerConsumptionCopy / PowerConsumptionCopyCopy** — 复制粘贴组件未重命名 | 3 个目录 |
| 10 | v-html 渲染代码生成器内容（XSS 风险） | `web/src/views/tool/gen/index.vue:179` |
| 11 | 空 `.catch(() => {})` 吞掉删除 API 的错误 | 4+ 组件 |
| 12 | DC 组件 API 响应结构与其他组件不一致（双重 .data 嵌套） | `DC/index.vue:144-148` |
| 13 | `beforeDestroy` 清理方式不一致（有的调 `stopTimer()`，有的直接 `clearInterval`） | 多个组件 |
| 14 | `@keydown.enter` 加在 `<el-button>` 上（按钮不接收键盘事件，Enter 提交无效） | 多个对话框 |
| 15 | CSS 两个相同的 1080px 断点，第二个覆盖第一个 | `device/dev/index.vue:140-150` |
| 16 | `recoverPassword` 弹窗结果 `console.log(res)` 泄露密码 | `baseDevice/index.vue:530,561` |

### Medium

| # | 问题 | 位置 |
|---|------|------|
| 17 | WebSocket 订阅在快速页面切换时可能泄漏 | `websocket.js:252-266` |
| 18 | 大量注释掉的代码块（模板、JS、CSS） | 全局 |
| 19 | 通用变量名（`demoList`, `_get()`, `flag`, `type`, `open01`）无业务含义 | 全局 |
| 20 | Router replace 未捕获 NavigationDuplicated 错误 | `device/dev/index.vue:113-118` |
| 21 | `errorCode.js` 缺少 500/601 等错误码映射 | `web/src/utils/errorCode.js` |
| 22 | `download` 函数 loading 单例在并发下载时可能过早关闭 | `request.js:131-161` |
| 23 | `var`/`let`/`const` 混用 | `websocket.js` |
| 24 | `fenqu` 组件导入了从未使用的组件 | `fenqu/index.vue:135-137` |
| 25 | `ZhiLiuHuiLu` 注释掉的重复定时器代码 | `ZhiLiuHuiLu/index.vue:174-179` |
| 26 | Store actions 不必要的 Promise 包装 | `user.js:38-46` |
| 27 | `request.js` GET 参数改写逻辑对所有方法生效 | `request.js:36-41` |

---

## 优先修复路线图

### 第一批 — 立即修复 (安全底线)
1. 恢复 `SaTokenConfig.java` 拦截器注册（取消注释）
2. 取消所有 `@SaCheckPermission` 的注释
3. 修复 `Runtime.exec()` 命令注入 → 用 ProcessBuilder + 参数校验
4. 轮换所有硬编码密钥/密码，改为纯环境变量
5. 修复 CORS 配置（限制来源白名单）

### 第二批 — 本周内 (稳定性)
6. 修复 4 个 NPE 点（加 null 检查）
7. 替换所有 `redisTemplate.keys()` 为 SCAN
8. 恢复 `@ExceptionHandler(NotLoginException.class)`
9. 修复前端 `ipcRenderer` / `cantCloseWindow` 浏览器模式崩溃
10. 创建 `PollingMixin` 并统一 20+ 组件的轮询逻辑

### 第三批 — 架构清理 (本月内)
11. 移动 `MpSqlInjector` 到 `ruoyi-common`，解除框架反向依赖
12. 拆分 `WriteController` (1582行) 和 `Update03DataService` (1583行)
13. 删除 Netty Modbus 死代码
14. 引入缓存抽象层，禁止 Controller 直接操作 RedisTemplate
15. 清理 261 个 console.log，引入日志开关

---

## 正面发现

- MQTT/Modbus 协议集成架构设计合理（Spring Integration + 连接池模式）
- WebSocket 推送实现干净（STOMP 协议处理、心跳、断线重连）
- 接口/实现分离规范（52 对 service/impl）
- 前端组件拆分合理，权限路由守卫正确
- `MqttMessageHandlerTest` 是测试典范（495 行，20+ 用例）
- SCAN 模式在部分代码中已存在，只需统一推广
- 防重复提交机制已存在（IoT 控制面板的必要防护）
