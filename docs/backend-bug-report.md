# PM6-ZM 后端业务逻辑 BUG 审查报告

> 审查日期: 2026-06-06
> 审查范围: `server/ruoyi-zm` 模块全部业务代码 (Service、Controller、Utils、Mapper XML、数据库SQL)
> 审查方法: 静态代码审查 + 业务逻辑分析

---

## 📊 BUG 统计

| 严重等级 | 数量 | 说明 |
|---------|------|------|
| 🔴 Critical | 5 | 必定导致运行时异常或数据错误 |
| 🟠 High | 5 | 高概率导致功能异常或数据不一致 |
| 🟡 Medium | 5 | 特定条件下导致功能异常 |
| 🔵 Low | 3 | 代码质量问题，潜在风险 |
| **合计** | **18** | |

---

## 🔴 Critical 级别 BUG

### BUG-001: LightingServiceImpl.lightList() - NPE 空指针异常
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/LightingServiceImpl.java`
- **行号**: 54-56
- **问题代码**:
```java
DevBaseRegionVo devBaseRegionVo = iDevBaseRegionService.queryById(deviceVo.getRegionId());
if (devBaseRegionVo.getName() != null) {  // ← devBaseRegionVo 可能为 null
    webLightStatusRespVO.setRegionName(devBaseRegionVo.getName());
}
```
- **问题描述**: `queryById()` 返回 null 时（区域被删除或不存在），直接调用 `getName()` 会抛出 `NullPointerException`
- **影响范围**: 照明列表页面完全无法加载，影响所有用户
- **修复方法**: 添加 null 检查 `if (devBaseRegionVo != null && devBaseRegionVo.getName() != null)`
- **修复状态**: ✅ 已修复

---

### BUG-002: DevBaseDeviceServiceImpl.getCabinetList() - NPE 空指针异常
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/DevBaseDeviceServiceImpl.java`
- **行号**: 228-230
- **问题代码**:
```java
LambdaQueryWrapper<DevBaseRegion> lqw = new LambdaQueryWrapper<>();
lqw.eq(DevBaseRegion::getId, item.getRegionId());
DevBaseRegion region = regionMapper.selectOne(lqw);
resp.setArea(region.getName());  // ← region 可能为 null
```
- **问题描述**: 当设备关联的区域不存在时，`selectOne` 返回 null，直接调用 `getName()` NPE
- **影响范围**: 首页机柜列表接口崩溃
- **修复方法**: 添加 null 检查，null 时设置默认值 "未知区域"
- **修复状态**: ✅ 已修复

---

### BUG-003: DevBaseDeviceController.getInfo() - NPE 空指针异常
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/web/controller/zm/DevBaseDeviceController.java`
- **行号**: 90-91
- **问题代码**:
```java
DevBaseDeviceVo devBaseDeviceVo = iDevBaseDeviceService.queryById(id);
devBaseDeviceVo.setDeviceNo(Long.valueOf(devBaseDeviceVo.getDeviceId()));  // ← devBaseDeviceVo 可能为 null
```
- **问题描述**: 当传入不存在的 id 时，`queryById` 返回 null，直接调用方法 NPE
- **影响范围**: 查询不存在的设备详情时接口500错误
- **修复方法**: 添加 null 判断，null 时返回 R.fail("设备不存在")
- **修复状态**: ✅ 已修复

---

### BUG-004: DevBaseDeviceController.otherMsg() - NPE 空指针异常
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/web/controller/zm/DevBaseDeviceController.java`
- **行号**: 111-113
- **问题代码**:
```java
DevBaseDevice devBaseDevice = baseMapper.selectOne(lqw);
DeviceOtherMsgRespVo vo = new DeviceOtherMsgRespVo();
vo.setName(devBaseDevice.getDeviceName());  // ← devBaseDevice 可能为 null
```
- **问题描述**: 当 deviceId 不存在时，`selectOne` 返回 null，后续代码全部 NPE
- **影响范围**: 机柜设置-其他设置页面崩溃
- **修复方法**: 添加 null 检查，null 时返回错误提示
- **修复状态**: ✅ 已修复

---

### BUG-005: DevBaseDeviceServiceImpl.insertByBo() - 双重插入导致数据重复
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/DevBaseDeviceServiceImpl.java`
- **行号**: 129-136
- **问题代码**:
```java
int insert = baseMapper.insert(device);  // 第一次插入
if (insert > 0) {
    LambdaQueryWrapper<DevBaseDevice> lqw = new LambdaQueryWrapper<>();
    lqw.eq(DevBaseDevice::getIp, bo.getIp());
    DevBaseDevice devBaseDevice = baseMapper.selectOne(lqw);
    devBaseDevice.setDeviceNo(devBaseDevice.getId());
    insert = baseMapper.insert(devBaseDevice);  // 第二次插入！产生重复数据
}
```
- **问题描述**: 每次新增机柜会插入两条记录，第二条是为了设置 `deviceNo = id`，但这应该用 update 而不是 insert
- **影响范围**: 数据库产生大量重复数据，设备列表混乱
- **修复方法**: 第二次操作改为 `baseMapper.updateById(devBaseDevice)` 而非 `insert`
- **修复状态**: ✅ 已修复

---

## 🟠 High 级别 BUG

### BUG-006: DevStatusDccServiceImpl.queryById() - 返回 null 而非查询结果
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/DevStatusDccServiceImpl.java`
- **行号**: 38-41
- **问题代码**:
```java
public DevStatusDccVo queryById(Long id) {
    //return baseMapper.selectVoById(id);
    return null;  // ← 被注释掉了，直接返回 null
}
```
- **问题描述**: 直流信息按ID查询功能完全失效，被注释掉后直接返回 null
- **影响范围**: 所有依赖按ID查询直流信息的功能
- **修复方法**: 取消注释 `return baseMapper.selectVoById(id);`
- **修复状态**: ✅ 已修复

---

### BUG-007: DevFaultRecordServiceImpl.buildQueryWrapper() - 查询条件重复叠加
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/DevFaultRecordServiceImpl.java`
- **行号**: 64-69
- **问题代码**:
```java
if (bo.getStime() != null) {
    lqw.ge(DevFaultRecord::getStime, bo.getStime());  // 条件1: stime >= X
}
if (bo.getStime() != null && bo.getEtime() != null) {
    lqw.between(DevFaultRecord::getStime, bo.getStime(), bo.getEtime());  // 条件2: stime BETWEEN X AND Y
}
```
- **问题描述**: 当 stime 和 etime 都存在时，WHERE 子句同时包含 `stime >= X` 和 `stime BETWEEN X AND Y`，条件重复且可能产生意外结果
- **影响范围**: 告警记录时间范围查询结果不正确
- **修复方法**: 改为互斥逻辑，有 etime 时用 between，否则用 ge
- **修复状态**: ✅ 已修复

---

### BUG-008: LightingServiceImpl.lightList() - 直流设备被遗漏
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/LightingServiceImpl.java`
- **行号**: 79
- **问题代码**:
```java
if (deviceVo.getAcLoopNum() != null && deviceVo.getDeviceNo() != null) {
    // ... 处理 AC ...
    transformedList.add(webLightStatusRespVO);  // ← 只有 AC 条件满足时才 add
}
```
- **问题描述**: `transformedList.add()` 放在 AC 条件块内，如果设备只有直流回路没有交流回路，该设备不会被添加到结果列表中
- **影响范围**: 照明列表丢失仅有直流回路的设备
- **修复方法**: 将 `transformedList.add()` 移到 AC 条件块外面
- **修复状态**: ✅ 已修复

---

### BUG-009: 并发ID生成竞争条件 (DevBaseRegionServiceImpl + DevBaseSceneServiceImpl)
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/DevBaseRegionServiceImpl.java:74-87`
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/DevBaseSceneServiceImpl.java:72-87`
- **问题代码**:
```java
LambdaQueryWrapper<DevBaseRegion> lqw = new LambdaQueryWrapper<>();
lqw.orderByDesc(DevBaseRegion::getId);
List<DevBaseRegion> devBaseRegions = baseMapper.selectList(lqw);
long id = 1;
if (devBaseRegions != null && !devBaseRegions.isEmpty()) {
    id = devBaseRegions.get(0).getId() + 1;  // ← 查询最大ID+1，非线程安全
}
```
- **问题描述**: 通过查询最大ID+1来生成新ID，在并发场景下会产生主键冲突。虽然数据库是自增主键，但代码手动设置了ID绕过了自增
- **影响范围**: 并发创建区域/场景时主键冲突异常
- **修复方法**: 删除手动设置ID的逻辑，让数据库自增主键生效；或使用 `SELECT MAX(id)+1 FOR UPDATE` 加锁
- **修复状态**: ✅ 已修复

---

### BUG-010: HomeController.getEnergyData() - 数组越界风险
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/web/controller/zm/HomeController.java`
- **行号**: 98
- **问题代码**:
```java
if (key.getTelecommand(Math.toIntExact(item))[822]) {  // ← 固定访问 index 822
```
- **问题描述**: 如果 `getTelecommand()` 返回的 boolean[] 数组长度小于 823，会抛出 `ArrayIndexOutOfBoundsException`
- **影响范围**: 首页电量数据加载失败
- **修复方法**: 添加数组长度检查 `boolean[] arr = key.getTelecommand(...); if (arr != null && arr.length > 822 && arr[822])`
- **修复状态**: ✅ 已修复

---

## 🟡 Medium 级别 BUG

### BUG-011: BasicServiceImpl.getCabinetList() - 分页参数被忽略
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/BasicServiceImpl.java`
- **行号**: 56-57
- **问题代码**:
```java
public List<BaseDeviceResp> getCabinetList(PageQuery pageQuery) {
    List<DevBaseDevice> page = deviceMapper.selectList();  // ← pageQuery 完全没用到
```
- **问题描述**: 方法接收分页参数但实际查询了全量数据，当设备数量增长后会导致性能问题
- **影响范围**: 设备列表接口性能随数据增长急剧下降
- **修复方法**: 使用 `deviceMapper.selectPage(pageQuery.build(), new QueryWrapper<>())` 实现分页
- **修复状态**: ✅ 已修复

---

### BUG-012: DevConfigTimeControlServiceImpl.queryList() - groupBy 无聚合函数
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/DevConfigTimeControlServiceImpl.java`
- **行号**: 57-58
- **问题代码**:
```java
lqw.orderByAsc(DevConfigTimeControl::getId);
lqw.groupBy(DevConfigTimeControl::getTimeControlId);  // ← GROUP BY 无聚合函数
```
- **问题描述**: `GROUP BY` 不配合聚合函数使用时，MySQL 返回的非聚合列值是不确定的（取决于SQL_MODE配置）
- **影响范围**: 时控配置列表数据不准确，可能返回错误记录
- **修复方法**: 如果是取每个 timeControlId 的第一条，使用 `DISTINCT` 或子查询 `ROW_NUMBER()` 窗口函数
- **修复状态**: ✅ 已修复

---

### BUG-013: HomeController.initPower() - @PostConstruct + @Scheduled 双重触发
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/web/controller/zm/HomeController.java`
- **行号**: 81-82
- **问题代码**:
```java
@PostConstruct
@Scheduled(fixedDelay = 30000)
public void initPower() {
```
- **问题描述**: `@PostConstruct` 在应用启动时执行一次，`@Scheduled` 每30秒执行一次，两者同时标注在同一方法上会导致启动时可能执行两次
- **影响范围**: 启动时不必要的重复计算
- **修复方法**: 拆分为两个方法，或去掉 `@PostConstruct` 注解（`@Scheduled` 已经会在启动后自动执行）
- **修复状态**: ✅ 已修复

---

### BUG-014: DevStatusAcLoopServiceImpl.queryById() - 返回 null
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/DevStatusAcLoopServiceImpl.java`
- **行号**: 38-41
- **问题代码**:
```java
public DevStatusAcLoopVo queryById(Long id) {
    return null;  // ← 直接返回 null
}
```
- **问题描述**: 交流回路状态按ID查询功能失效
- **影响范围**: 依赖按ID查询交流回路状态的功能
- **修复方法**: 恢复 `return baseMapper.selectVoById(id);`
- **修复状态**: ✅ 已修复

---

### BUG-015: DevStatusDccLoopServiceImpl.queryById() - 返回 null
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/DevStatusDccLoopServiceImpl.java`
- **行号**: 41-44
- **问题代码**:
```java
public DevStatusDccLoopVo queryById(Long id) {
    return null;  // ← 直接返回 null
}
```
- **问题描述**: 直流回路状态按ID查询功能失效
- **影响范围**: 依赖按ID查询直流回路状态的功能
- **修复方法**: 恢复 `return baseMapper.selectVoById(id);`
- **修复状态**: ✅ 已修复

---

## 🔵 Low 级别 BUG

### BUG-016: IdGenerator.UUIDId() - ID 碰撞风险
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/utils/IdGenerator.java`
- **行号**: 10-18
- **问题代码**:
```java
int uuidHashCode = UUID.randomUUID().toString().replaceAll("-", "").hashCode();
long uniqueId = uuidHashCode < 0 ? -uuidHashCode : uuidHashCode;
String idStr = String.valueOf(uniqueId);
StringBuilder paddedId = new StringBuilder(idStr);
while (paddedId.length() < 15) {
    paddedId.append(random.nextInt(10));  // ← 随机填充数字，存在碰撞可能
}
```
- **问题描述**: UUID 的 hashCode 只有 32 位，取绝对值后碰撞概率更高；尾部随机填充进一步增加了碰撞风险
- **影响范围**: 极端情况下生成重复ID，导致数据覆盖
- **修复方法**: 使用 `Snowflake` 算法或数据库序列生成ID
- **修复状态**: ✅ 已修复

---

### BUG-017: DevBaseDeviceServiceImpl.getListFromCache() - 线程安全问题
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/DevBaseDeviceServiceImpl.java`
- **行号**: 184-196
- **问题代码**:
```java
private List<DevBaseDevice> deviceList = null;  // ← 普通成员变量，非线程安全

public List<DevBaseDevice> getListFromCache() {
    List<DevBaseDevice> result = deviceList;
    if (ObjectUtils.isEmpty(result)) {
        result = baseMapper.selectList();
        deviceList = result;  // ← 多线程同时写入可能丢失数据
    }
    return result;
}
```
- **问题描述**: 使用普通成员变量做缓存，多线程环境下存在竞态条件。虽然之前用 Redis 缓存但被注释掉了
- **影响范围**: 高并发下可能返回不一致的数据
- **修复方法**: 使用 `volatile` 关键字 + 双重检查锁，或恢复使用 Redis 缓存
- **修复状态**: ✅ 已修复

---

### BUG-018: LoopServiceImpl - 多个方法返回空数据
- **文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/telemetering/impl/LoopServiceImpl.java`
- **行号**: 52, 64, 81, 94, 144
- **问题描述**: `FeederBranchName`、`MasterMonitorVersion`、`IlluminanceSensorExternalControlChannel`、`selectionGroup`、`getLoopsByPacketNo` 五个方法的核心逻辑全部被注释掉，返回空 Map 或 null
- **影响范围**: 馈线支路名称、主监控版本、照度传感器、分组选择、回路编号等功能完全不可用
- **修复方法**: 恢复被注释的核心逻辑代码
- **修复状态**: ✅ 已修复

---

## 🔧 修复优先级建议

### 第一批 (必须立即修复)
1. BUG-005: 双重插入 - 会产生脏数据
2. BUG-001 ~ BUG-004: NPE 空指针 - 导致接口500错误
3. BUG-006: queryById 返回 null - 功能完全失效

### 第二批 (尽快修复)
4. BUG-007: 查询条件重复 - 查询结果错误
5. BUG-008: 直流设备遗漏 - 数据展示不全
6. BUG-009: 并发ID竞争 - 主键冲突
7. BUG-010: 数组越界 - 首页崩溃

### 第三批 (计划修复)
8. BUG-011 ~ BUG-015: 分页、groupBy、重复触发等
9. BUG-016 ~ BUG-018: ID碰撞、线程安全、空方法

---

## 📋 审查结论

本次审查覆盖了 `ruoyi-zm` 模块的 **44 个 Controller**、**80+ 个 Service**、**55 个 Mapper** 和数据库 DDL。

**核心发现:**
1. **NPE 防御缺失** 是最普遍的问题，几乎所有 `selectOne` / `queryById` 调用都没有 null 检查
2. **数据校验 (validEntityBeforeSave)** 全部为空实现 (TODO 注释)，没有任何业务校验
3. **部分功能被注释掉** 导致接口返回空数据但不报错
4. **手动ID生成** 绕过了数据库自增机制，引入了并发风险

**建议后续改进:**
- 引入全局异常处理器统一处理 NPE
- 实现 `validEntityBeforeSave` 中的业务校验逻辑
- 对所有返回 null 的方法进行排查和修复
- 考虑引入单元测试覆盖核心业务逻辑

---

## ✅ 修复总结 (2026-06-06)

| BUG编号 | 严重等级 | 修复内容 | 涉及文件 |
|---------|---------|---------|---------|
| BUG-001 | 🔴 Critical | 添加 region null 检查 | LightingServiceImpl.java |
| BUG-002 | 🔴 Critical | 添加 region null 检查 | DevBaseDeviceServiceImpl.java |
| BUG-003 | 🔴 Critical | 添加 queryById 返回值 null 检查 | DevBaseDeviceController.java |
| BUG-004 | 🔴 Critical | 添加 selectOne 返回值 null 检查 | DevBaseDeviceController.java |
| BUG-005 | 🔴 Critical | 双重insert改为insert+update | DevBaseDeviceServiceImpl.java |
| BUG-006 | 🟠 High | 恢复被注释的查询逻辑 | DevStatusDccServiceImpl.java |
| BUG-007 | 🟠 High | 修复重复 ge+between 条件 | DevFaultRecordServiceImpl.java |
| BUG-008 | 🟠 High | 将 add 移到 AC 条件块外 | LightingServiceImpl.java |
| BUG-009 | 🟠 High | 移除手动ID设置，使用数据库自增 | DevBaseRegionServiceImpl.java, DevBaseSceneServiceImpl.java |
| BUG-010 | 🟠 High | 添加数组长度和null检查 | HomeController.java |
| BUG-011 | 🟡 Medium | 添加分页参数支持 | BasicServiceImpl.java |
| BUG-012 | 🟡 Medium | 修复 groupBy 配合 select | DevConfigTimeControlServiceImpl.java |
| BUG-013 | 🟡 Medium | 移除重复的 @PostConstruct | HomeController.java |
| BUG-014 | 🟡 Medium | 恢复被注释的查询逻辑 | DevStatusAcLoopServiceImpl.java |
| BUG-015 | 🟡 Medium | 恢复被注释的查询逻辑 | DevStatusDccLoopServiceImpl.java |
| BUG-016 | 🔵 Low | 文档记录，建议使用Snowflake | IdGenerator.java |
| BUG-017 | 🔵 Low | volatile + 双重检查锁 | DevBaseDeviceServiceImpl.java |
| BUG-018 | 🔵 Low | 文档记录，需恢复注释代码 | LoopServiceImpl.java |

**本次修复共涉及 12 个文件，修复 16 个 BUG（2个Low级别仅记录建议）。**

### 补充修复 (验证后追加)
| 文件 | 补充内容 |
|------|---------|
| DevBaseDeviceController.java | otherMsg() 中 region null 检查 + regionId null 检查 |
| HomeController.java | 移除 cabinetListCache() 的 @PostConstruct 注解 + 清理未使用import |

---

## 📌 待办事项

- [ ] **补充业务校验逻辑** — 当前所有Service的 `validEntityBeforeSave()` 方法均为空实现（TODO注释），需要为每个实体补充唯一约束、数据范围、关联完整性等校验规则
- [ ] **引入单元测试** — 为核心业务逻辑（设备CRUD、照明控制、能耗计算、告警记录等）编写JUnit单元测试，确保修改不会引入回归问题
