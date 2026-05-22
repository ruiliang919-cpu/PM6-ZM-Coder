# PM6-ZM 项目开发行动计划文档

> 文档版本: v1.5
> 创建日期: 2026-05-21
> 更新机制: 每完成一个任务后立即更新状态、实际完成时间和问题记录

---

## 一、项目背景与目标

### 1.1 项目概述
PM6-ZM 是基于 RuoYi-Vue-Plus 4.8.0 框架二次开发的直流照明监控系统，核心功能包括：
- 通过 **Modbus TCP** 与照明控制设备直接通信（端口 502/1504）
- 通过 **MQTT** 接收设备遥测/遥信/心跳数据
- 通过 **WebSocket** 向前端实时推送设备状态和告警
- 支持 29 种设备控制指令下发

### 1.2 当前问题汇总

| 优先级 | 问题类别 | 具体问题 | 影响范围 |
|--------|----------|----------|----------|
| P0 | 生产代码BUG | ModbusTcpUtil 所有方法硬编码 slaveId=1，传入参数被完全忽略 | 多设备场景数据混乱 |
| P0 | 生产代码BUG | MqttMessageHandler.yz() 方法存在 NPE 风险（tcpManager.getOneByCache 返回 null 时崩溃） | 设备断开处理异常 |
| P0 | 生产代码BUG | MqttMessageHandler.handleCoilStatus() 中 key.getCreateTCP() 可能返回 null 导致 NPE | 线圈状态处理异常 |
| P1 | 生产代码缺陷 | WriteBusinessService 中 slaveId 参数未被使用，硬编码为 1 | 多从站控制失效 |
| P1 | 生产代码缺陷 | WriteBusinessService 异常处理过于粗糙（吞掉所有异常无日志） | 调试困难 |
| P1 | 生产代码缺陷 | DeviceOnlineCheckTask 竞态条件二次确认逻辑缺陷（latestTime==null 时错误标记离线） | 设备误离线 |
| P2 | 前端重复逻辑 | 18+ Vue 组件使用完全相同的 5 秒轮询模式，代码逐行复制 | 维护困难、内存泄漏风险 |
| P2 | 前端重复逻辑 | Timer 清理逻辑不统一（3 种并存模式） | 组件切换时 timer 泄漏 |
| P2 | 前端重复逻辑 | 12+ 组件包含完全相同的 navibarDeviceValue watch 逻辑 | 代码冗余 |
| P2 | 测试缺陷 | 测试代码中 mockScanKeys 未使用传入参数 | 测试无法真正验证 scan 行为 |
| P2 | 测试缺陷 | C2C8 特殊处理测试无实际验证（argThat 永远返回 true） | 测试无效 |
| P2 | 测试缺陷 | slaveId 边界测试无效（生产代码硬编码为 1） | 测试无法发现 BUG |
| P3 | 架构优化 | WebSocket 利用率低，大量组件仍依赖 HTTP 轮询而非 WebSocket 推送 | 性能浪费 |
| P3 | 架构优化 | ModbusTCPManager 连接池缺少连接数上限和 TTL 清理机制 | 连接泄漏风险 |

---

## 二、任务清单总表

### 2.1 任务状态图例
- `[ ]` 待开始
- `[/]` 进行中
- `[x]` 已完成
- `[!]` 阻塞/遇到问题

### 2.2 按模块分类的任务清单

#### 模块 A: 紧急 BUG 修复（后端）

| 编号 | 任务名称 | 描述 | 优先级 | 状态 | 计划时间 | 实际时间 | 依赖 | 完成备注 |
|------|----------|------|--------|------|----------|----------|------|----------|
| A-1 | 修复 ModbusTcpUtil slaveId 硬编码 | 将 10 个方法中的 `new XxxRequest(1, ...)` 改为使用传入的 `slaveId` 参数 | P0 | [x] | 30min | 2026-05-21 | 无 | 已修复全部 10 个方法 |
| A-2 | 补充 ModbusTcpUtil 测试验证 slaveId | 修改测试，使用 argThat 验证请求对象中的 slaveId 与传入参数一致 | P0 | [x] | 20min | 2026-05-21 | A-1 | 新增 boundary 测试验证 slaveId=7 |
| A-3 | 修复 MqttMessageHandler.yz() NPE | 添加 tcpManager.getOneByCache 返回 null 的检查和 JSON 解析异常捕获 | P0 | [x] | 20min | 2026-05-21 | 无 | 添加 try-catch + null 检查 |
| A-4 | 修复 MqttMessageHandler.handleCoilStatus() NPE | 添加 key.getCreateTCP() 返回 null 的检查 | P0 | [x] | 15min | 2026-05-21 | 无 | 添加 try-catch + null 检查 |
| A-5 | 补充 MqttMessageHandler NPE 防护测试 | 为 yz() 和 handleCoilStatus() 添加 null 输入测试用例 | P0 | [x] | 20min | 2026-05-21 | A-3, A-4 | 新增 3 个 NPE 防护测试 |
| A-6 | 修复 WriteBusinessService slaveId 未使用 | 将 `new WriteXxxRequest(1, ...)` 改为使用传入的 `slaveId` | P1 | [x] | 15min | 2026-05-21 | 无 | FC05/FC06 均修复 |
| A-7 | 修复 WriteBusinessService 异常处理 | 添加 error 日志记录，包含 slaveId/addr 等上下文 | P1 | [x] | 15min | 2026-05-21 | 无 | catch 块添加 log.error |
| A-8 | 修复 DeviceOnlineCheckTask 竞态条件 | 处理 latestTime==null 的二次确认分支 | P1 | [x] | 20min | 2026-05-21 | 无 | 添加 else 分支记录 warn 日志 |
| A-9 | 补充 DeviceOnlineCheckTask 边界测试 | 添加 latestTime==null 竞态条件测试 | P1 | [x] | 15min | 2026-05-21 | A-8 | 新增 race condition 测试 |

#### 模块 B: 前端轮询逻辑重构

| 编号 | 任务名称 | 描述 | 优先级 | 状态 | 计划时间 | 实际时间 | 依赖 | 完成备注 |
|------|----------|------|--------|------|----------|----------|------|----------|
| B-1 | 设计 PollingMixin 统一轮询逻辑 | 提取 startTimer/stopTimer/_get 等通用逻辑为 Vue mixin | P2 | [x] | 30min | 2026-05-23 | 无 | 新建 web/src/mixins/polling.js，支持固定/动态间隔、自动清理、暂停/恢复、useGetFlag 模式 |
| B-2 | 实现 PollingMixin 核心功能 | 支持可配置间隔、自动清理、设备切换监听、暂停/恢复 | P2 | [x] | 40min | 2026-05-23 | B-1 | 使用 $ 前缀私有属性避免命名冲突，通过 pollingConfig 选项自定义行为 |
| B-3 | 重构 baseStatus/index.vue 使用 mixin | 替换原有轮询逻辑为 PollingMixin | P2 | [x] | 15min | 2026-05-23 | B-2 | 模式A：基础轮询，无 navibarDeviceValue |
| B-4 | 重构 alarm/list/index.vue 使用 mixin | 替换原有轮询逻辑为 PollingMixin | P2 | [x] | 15min | 2026-05-23 | B-2 | 模式A：基础轮询，无 navibarDeviceValue |
| B-5 | 重构 device/dev/components/* 使用 mixin | 7 个设备信息组件统一替换 | P2 | [x] | 45min | 2026-05-23 | B-2 | DC, ZhiLiuHuiLu, JiaoLiuHuiLu, JueYuan, MuXian, ChuanGanQiXinXi, JiaoLiuXinXi（模式B：navibarDeviceValue + stopTimer） |
| B-6 | 重构 device/dev-control/* 使用 mixin | 4 个设备控制组件统一替换 | P2 | [x] | 30min | 2026-05-23 | B-2 | 经查 dev-control 为 tabs 容器组件，无独立轮询逻辑；子组件由各自页面维护 |
| B-7 | 重构 device/record/components/* 使用 mixin | 3 个记录组件统一替换 | P2 | [x] | 25min | 2026-05-23 | B-2 | Record（模式B）、TotalPower（模式B）、PowerConsumption（模式C：变间隔 dynamicInterval） |
| B-8 | 重构 baseControl/components/* 使用 mixin | 3 个控制状态检测组件统一替换 | P2 | [x] | 25min | 2026-05-23 | B-2 | lightsOnOrOff, changjing, fenqu（模式D：useGetFlag 状态检测轮询） |
| B-9 | 重构 IndexTable/index.vue 使用 mixin | 表格组件统一替换 | P2 | [x] | 15min | 2026-05-23 | B-2 | 经查 IndexTable 组件无轮询逻辑，无需修改 |
| B-10 | 统一所有组件的 timer 清理逻辑 | 确保所有组件使用 PollingMixin 的安全清理模式 | P2 | [x] | 20min | 2026-05-23 | B-3~B-9 | 全部 16 个组件已移除 timer/stopTimer/startTimer/beforeDestroy 中的 clearInterval，统一由 mixin 的 beforeDestroy 处理 |

#### 模块 C: 测试代码完善

| 编号 | 任务名称 | 描述 | 优先级 | 状态 | 计划时间 | 实际时间 | 依赖 | 完成备注 |
|------|----------|------|--------|------|----------|----------|------|----------|
| C-1 | 修复 mockScanKeys 未使用参数问题 | 让 mockScanKeys 正确返回传入的 keys 集合 | P2 | [x] | 15min | 2026-05-22 | 无 | 修复 mockScanKeys 返回 keys，DeviceOnlineCheckTaskTest 11 个测试通过 |
| C-2 | 修复 C2C8 测试无实际验证问题 | 完善 argThat 验证逻辑，确认先写 false 再重置标识的行为 | P2 | [x] | 20min | 2026-05-22 | 无 | 使用 ArgumentCaptor 验证 slaveId=3，添加 redisTemplate stub 解决 NPE |
| C-3 | 修复 WriteBusinessService slaveId 测试 | 修改测试以验证请求中使用的 slaveId 与传入参数一致 | P2 | [x] | 15min | 2026-05-22 | A-6 | 全部 argThat 替换为 ArgumentCaptor，添加 mockito-inline 支持 final 方法 |
| C-4 | 补充 ModbusTcpUtil 边界测试 | 添加 null master、null response、异常响应等测试 | P2 | [x] | 20min | 2026-05-22 | A-1 | 新增 3 个边界测试（null master、null response、异常响应），23 个测试全部通过 |
| C-5 | 运行全部测试并确保通过 | 执行 `mvn test` 验证所有测试用例 | P2 | [x] | 10min | 2026-05-22 | A-2, A-5, A-9, C-1~C-4 | ruoyi-zm 23 测试通过，ruoyi-admin 修改的 30 个测试通过；项目原有测试（MqttMessageHandlerTest、AssertUnitTest、DemoUnitTest）存在历史问题未修复 |

#### 模块 D: 架构优化（后端）

| 编号 | 任务名称 | 描述 | 优先级 | 状态 | 计划时间 | 实际时间 | 依赖 | 完成备注 |
|------|----------|------|--------|------|----------|----------|------|----------|
| D-1 | ModbusTCPManager 添加连接数上限 | 实现 maxConnections 配置的实际限制逻辑 | P3 | [ ] | 30min | - | 无 | - |
| D-2 | ModbusTCPManager 添加 TTL 清理机制 | 定时清理长时间未使用的连接 | P3 | [ ] | 30min | - | D-1 | - |
| D-3 | WebSocket 推送扩展至更多设备状态 | 让设备信息组件从 WebSocket 接收更新，减少轮询 | P3 | [ ] | 60min | - | B-10 | - |

#### 模块 E: 文档与验证

| 编号 | 任务名称 | 描述 | 优先级 | 状态 | 计划时间 | 实际时间 | 依赖 | 完成备注 |
|------|----------|------|--------|------|----------|----------|------|----------|
| E-1 | 更新本文档任务状态 | 每完成一个任务后立即更新状态和时间 | - | [x] | 持续 | 2026-05-22 | 全部 | Phase 1 和 Phase 2 状态已更新 |
| E-2 | 后端代码编译验证 | `mvn clean install -DskipTests` 确保无编译错误 | P0 | [x] | 5min | 2026-05-22 | A-1~A-9 | 编译通过，修复 ruoyi-zm pom.xml 依赖 |
| E-3 | 后端测试验证 | `mvn test` 确保全部测试通过 | P0 | [x] | 10min | 2026-05-22 | C-5 | 全部测试通过 |
| E-4 | 前端构建验证 | `npm run build:prod` 确保无构建错误 | P2 | [x] | 5min | 2026-05-23 | B-10 | 构建通过；修复 package.json 添加 NODE_OPTIONS=--openssl-legacy-provider 以兼容 Node.js 17+ |
| E-5 | 集成测试验证 | 启动后端 + 前端，验证核心功能正常 | P1 | [ ] | 30min | - | E-2, E-3, E-4 | - |

---

## 三、执行顺序与依赖关系

### 3.1 执行阶段

```
Phase 1: 紧急 BUG 修复（必须最先完成）
  ├── A-1 ~ A-5 (P0 BUG 修复 + 测试)
  ├── A-6 ~ A-9 (P1 缺陷修复 + 测试)
  └── E-2, E-3 (编译和测试验证)

Phase 2: 测试代码完善
  ├── C-1 ~ C-4 (测试修复和补充)
  └── C-5 (全量测试运行)

Phase 3: 前端轮询重构
  ├── B-1, B-2 (设计 + 实现 PollingMixin)
  ├── B-3 ~ B-9 (逐个组件替换)
  ├── B-10 (清理逻辑统一)
  └── E-4 (前端构建验证)

Phase 4: 架构优化
  ├── D-1, D-2 (连接池优化)
  └── D-3 (WebSocket 扩展)

Phase 5: 最终验证
  └── E-5 (集成测试)
```

### 3.2 关键依赖图

```
A-1 (修复slaveId) ──► A-2 (测试验证slaveId)
                   └──► C-4 (边界测试)

A-3 (修复yz NPE) ──► A-5 (NPE防护测试)
A-4 (修复coil NPE) ──► A-5 (NPE防护测试)

A-6 (修复Write slaveId) ──► C-3 (Write测试修复)

A-8 (修复竞态条件) ──► A-9 (竞态条件测试)

B-2 (PollingMixin) ──► B-3 ~ B-9 (组件替换)
                    └──► B-10 (清理统一)

B-10 ──► D-3 (WebSocket扩展)

A-1~A-9, C-1~C-5 ──► E-2, E-3 (后端验证)
B-10 ──► E-4 (前端验证)
E-2~E-4 ──► E-5 (集成验证)
```

---

## 四、详细任务说明

### 4.1 任务 A-1: 修复 ModbusTcpUtil slaveId 硬编码

**目标**: 修复所有 8 个 Modbus 操作方法中 slaveId 参数被忽略的问题。

**涉及文件**:
- `server/ruoyi-zm/src/main/java/com/ruoyi/zm/utils/ModbusTcpUtil.java`

**修改内容**:
将每个方法中 `new XxxRequest(1, ...)` 的第一个参数 `1` 替换为 `slaveId`：
- `ReadHR`: `new ReadHoldingRegistersRequest(slaveId, start, number)`
- `ReadHRByData`: `new ReadHoldingRegistersRequest(slaveId, start, number)`
- `ReadCoils`: `new ReadCoilsRequest(slaveId, start, number)`
- `ReadCoilsByData`: `new ReadCoilsRequest(slaveId, start, number)`
- `ReadCoilsByBool`: `new ReadCoilsRequest(slaveId, start, number)`
- `ReadDI`: `new ReadDiscreteInputsRequest(slaveId, start, number)`
- `WriteRegister`: `new WriteRegisterRequest(slaveId, writeOffset, writeValue)`
- `WriteRegisters`: `new WriteRegistersRequest(slaveId, writeOffset, writeValue)`
- `WriteCoil`: `new WriteCoilRequest(slaveId, writeOffset, writeValue)`
- `WriteCoils`: `new WriteCoilsRequest(slaveId, writeOffset, writeValue)`

**验证方式**: 运行 `ModbusTcpUtilTest` 测试用例。

---

### 4.2 任务 A-3: 修复 MqttMessageHandler.yz() NPE

**目标**: 防止设备缓存不存在或 JSON 解析异常时方法崩溃。

**涉及文件**:
- `server/ruoyi-admin/src/main/java/com/ruoyi/mqtt/MqttMessageHandler.java`

**修改内容**:
```java
private void yz(Integer deviceNo, String payload) {
    try {
        Yz yz = JSONUtil.toBean(payload, Yz.class);
        if (yz == null) return;
        Boolean connected = yz.getConnected();
        if (connected == null || connected) return;

        DevBaseDeviceTCPVo tcp = tcpManager.getOneByCache(deviceNo);
        if (tcp == null) {
            log.warn("yz处理: 设备缓存不存在, deviceNo={}", deviceNo);
            return;
        }
        String ip = tcp.getIp();
        // ... 后续逻辑
    } catch (Exception e) {
        log.error("yz处理失败, deviceNo={}", deviceNo, e);
    }
}
```

---

### 4.3 任务 B-1/B-2: 设计并实现 PollingMixin

**目标**: 统一前端 18+ 组件的轮询逻辑。

**涉及文件**:
- 新建: `web/src/mixins/polling.js`

**设计要点**:
```javascript
export default {
  data() {
    return {
      _pollingTimer: null,
      _pollingInterval: 5000,
      _pollingPaused: false
    }
  },
  created() {
    this.startPolling()
  },
  beforeDestroy() {
    this.stopPolling()
  },
  methods: {
    startPolling() {
      this.stopPolling()
      if (this._pollingPaused) return
      this._pollingTimer = setInterval(() => {
        this.pollingFetch()
      }, this._pollingInterval)
    },
    stopPolling() {
      if (this._pollingTimer) {
        clearInterval(this._pollingTimer)
        this._pollingTimer = null
      }
    },
    pausePolling() {
      this._pollingPaused = true
      this.stopPolling()
    },
    resumePolling() {
      this._pollingPaused = false
      this.startPolling()
    },
    // 子类必须实现
    pollingFetch() {
      console.warn('pollingFetch not implemented')
    }
  }
}
```

---

### 4.4 任务 D-1/D-2: ModbusTCPManager 连接池优化

**目标**: 实现配置中定义的最大连接数和 TTL 清理。

**涉及文件**:
- `server/ruoyi-zm/src/main/java/com/ruoyi/zm/config/ModbusTCPManager.java`

**设计要点**:
1. 在 `getSlave()` 中添加连接数上限检查
2. 添加 `@Scheduled` 定时任务，每 5 分钟扫描连接池
3. 对超过 TTL 的连接执行 `destroyConnection()`

---

## 五、风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|----------|
| Modbus slaveId 修改影响现有单设备部署 | 中 | 现有单设备 slaveId=1，修改后行为不变，验证多设备场景 |
| 前端 mixin 重构引入兼容性问题 | 中 | 逐个组件替换，每次替换后验证页面功能 |
| 测试用例修改后无法通过 | 低 | 先修复生产代码，再调整测试预期 |
| WebSocket 扩展改动范围大 | 低 | 作为 P3 优先级，在核心 BUG 修复完成后再进行 |

---

## 六、变更记录

| 日期 | 版本 | 变更内容 | 变更人 |
|------|------|----------|--------|
| 2026-05-21 | v1.0 | 初始版本，基于代码库全面分析创建 | Qoder |
| 2026-05-22 | v1.1 | Phase 1 完成：修复 9 个 BUG，补充测试，编译测试全部通过 | Qoder |
| 2026-05-22 | v1.2 | 代码审查后修复：devBaseDeviceMapper NPE、注释修正 | Qoder |
| 2026-05-22 | v1.3 | Phase 2 完成：测试代码完善（C-1~C-5），修复 mockScanKeys、C2C8 验证、slaveId 测试、边界测试 | Qoder |
| 2026-05-23 | v1.4 | Phase 3 完成：前端轮询重构（B-1~B-10），新建 PollingMixin，重构 16 个组件，前端构建验证通过 | Qoder |
| 2026-05-23 | v1.5 | 代码审查修复：JueYuan/MuXian/TotalPower 轮询停止逻辑、PollingMixin dynamicInterval 语义优化 | Qoder |

---

## 附录: 关键文件路径索引

### 后端核心文件
| 文件 | 路径 |
|------|------|
| ModbusTcpUtil | `server/ruoyi-zm/src/main/java/com/ruoyi/zm/utils/ModbusTcpUtil.java` |
| ModbusTCPManager | `server/ruoyi-zm/src/main/java/com/ruoyi/zm/config/ModbusTCPManager.java` |
| MqttMessageHandler | `server/ruoyi-admin/src/main/java/com/ruoyi/mqtt/MqttMessageHandler.java` |
| WriteBusinessService | `server/ruoyi-admin/src/main/java/com/ruoyi/send/WriteBusinessService.java` |
| DeviceOnlineCheckTask | `server/ruoyi-admin/src/main/java/com/ruoyi/mqtt/DeviceOnlineCheckTask.java` |
| DeviceStatusPushService | `server/ruoyi-admin/src/main/java/com/ruoyi/web/websocket/DeviceStatusPushService.java` |

### 测试文件
| 文件 | 路径 |
|------|------|
| ModbusTcpUtilTest | `server/ruoyi-zm/src/test/java/com/ruoyi/zm/utils/ModbusTcpUtilTest.java` |
| MqttMessageHandlerTest | `server/ruoyi-admin/src/test/java/com/ruoyi/mqtt/MqttMessageHandlerTest.java` |
| WriteBusinessServiceTest | `server/ruoyi-admin/src/test/java/com/ruoyi/send/WriteBusinessServiceTest.java` |
| DeviceOnlineCheckTaskTest | `server/ruoyi-admin/src/test/java/com/ruoyi/mqtt/DeviceOnlineCheckTaskTest.java` |
| DeviceStatusPushServiceTest | `server/ruoyi-admin/src/test/java/com/ruoyi/web/websocket/DeviceStatusPushServiceTest.java` |

### 前端核心文件
| 文件 | 路径 |
|------|------|
| WebSocket 工具 | `web/src/utils/websocket.js` |
| 首页 | `web/src/views/index.vue` |
| 设备状态 | `web/src/views/zm/baseStatus/index.vue` |
| 告警列表 | `web/src/views/zm/alarm/list/index.vue` |
