# PM6-ZM 全链路测试 BUG 报告

**测试日期**: 2026-06-06
**测试环境**: Windows 11, 本地部署 (MySQL 3306, Redis 6379, MQTT 1883, Java 8081, Nginx 80)
**测试范围**: 前端页面 + 后端API + 数据库 全链路联动测试

---

## BUG 列表

### BUG-001 [CRITICAL] 场景新增接口返回500 — id字段无默认值
- **描述**: `POST /zm/baseScene/add` 返回 `{"code":500,"msg":"系统错误，请稍后重试"}`
- **影响**: 无法新增场景
- **复现场景**: `curl -X POST /zm/baseScene/add -d '{"name":"test"}'`
- **根因**: `dev_base_scene` 表的 `id` 列未设置 AUTO_INCREMENT，且 `DevBaseSceneServiceImpl.insertByBo()` 未手动设置 id，导致 MySQL 报错 `Field 'id' doesn't have a default value`
- **修复方案**: 在 `insertByBo()` 中手动生成 id（查询最大 id + 1），并在插入前设置 id 和 sceneId
- **修复文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/DevBaseSceneServiceImpl.java`
- **状态**: ✅ 已修复

### BUG-002 [MEDIUM] 区域新增接口缺少id参数时返回空错误信息
- **描述**: `POST /zm/baseDistrict/add` 不传 `id` 字段时返回 `{"code":500,"msg":null,"data":null}`，错误信息为 null
- **影响**: 前端用户无法了解操作失败原因
- **复现场景**: `curl -X POST /zm/baseDistrict/add -d '{"name":"test","orderNo":99}'`
- **根因**: `DevBaseDistrictBo.id` 为 `Long` 类型（可为 null），controller 中 `bo.getId() < 0` 直接拆箱导致 NPE
- **修复方案**: 在 controller 的 `add()` 方法中添加 `bo.getId() == null` 空值检查
- **修复文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/web/controller/zm/DevBaseDistrictController.java`
- **状态**: ✅ 已修复

### BUG-003 [LOW] 前端 navbar.js 调用不存在的后端接口
- **描述**: `web/src/api/zm/navbar.js` 中 `testGetData()` 调用 `GET /zm/time/test/getData`，但后端不存在此接口
- **影响**: 手动采集设备电量功能无法使用，返回 404
- **复现场景**: `curl GET /zm/time/test/getData` 返回 404 Not Found
- **根因**: 后端未实现此接口，该功能暂未开放
- **修复方案**: 在 Navbar.vue 的 `testGetData()` 方法中添加 `.catch()` 错误处理，提示"手动采集功能暂未开放"
- **修复文件**: `web/src/layout/components/Navbar.vue`
- **状态**: ✅ 已修复（前端容错处理）

### BUG-004 [LOW] 照明控制 zoneControlList 空请求体返回500
- **描述**: `POST /zm/lighting/zoneControlList` 传空 JSON `{}` 返回 `{"code":500,"msg":null}`
- **影响**: 前端若未正确传递分页参数会导致500错误
- **复现场景**: `curl -X POST /zm/lighting/zoneControlList -d '{}'`
- **根因**: `ConfigDistrictServiceImpl.getPageTable()` 直接调用 `page.getPageNum()` 拆箱，当 pageNum 为 null 时 NPE
- **修复方案**: 在 `getPageTable()` 中添加 null 检查，pageNum 默认 1，pageSize 默认 10
- **修复文件**: `server/ruoyi-zm/src/main/java/com/ruoyi/zm/service/impl/ConfigDistrictServiceImpl.java`
- **状态**: ✅ 已修复

### BUG-005 [INFO] 场景删除接口返回操作失败
- **描述**: `DELETE /zm/baseScene/{id}` 删除场景时返回 `{"code":500,"msg":"操作失败"}`
- **影响**: 无法删除场景
- **复现场景**: `curl -X DELETE /zm/baseScene/10`
- **根因**: 原 JAR 版本过旧，包含已知 BUG；重建 JAR 后删除功能正常
- **修复方案**: 重新构建 JAR 部署（随 BUG-001 一起修复）
- **状态**: ✅ 已修复

---

## 测试结果汇总

### ✅ 正常通过的模块

| 模块 | 接口 | 状态 |
|------|------|------|
| 登录认证 | POST /login, GET /getInfo, POST /logout | ✅ 正常 |
| 验证码 | GET /captchaImage | ✅ 正常 (captchaEnabled=false) |
| 机柜列表 | POST /zm/basic/getCabinetList | ✅ 返回61条数据 |
| 机柜详情 | GET /zm/baseDevice/{id} | ✅ 正常 |
| 机柜初始化 | GET /zm/baseDevice/init | ✅ 正常 |
| 密码验证 | GET /recover/password | ✅ 正常 |
| 区域列表 | POST /zm/baseRegion/list | ✅ 返回3条数据 |
| 区域无分页 | GET /zm/baseRegion/noPageList | ✅ 正常 |
| 区域新增 | POST /zm/baseRegion/add | ✅ 正常 |
| 区域修改 | PUT /zm/baseRegion/edit | ✅ 正常 |
| 区域删除 | DELETE /zm/baseRegion/{id} | ✅ 正常 |
| 分区列表 | POST /zm/baseDistrict/list | ✅ 返回32条数据 |
| 分区详情 | GET /zm/baseDistrict/{id} | ✅ 正常 |
| 分区新增(带id) | POST /zm/baseDistrict/add | ✅ 正常 |
| 分区修改 | PUT /zm/baseDistrict/edit | ✅ 正常 |
| 分区删除 | DELETE /zm/baseDistrict/{id} | ✅ 正常 |
| 场景列表 | GET /zm/basic/getSceneList | ✅ 返回9条数据 |
| 场景选择列表 | GET /zm/baseScene/getSceneSelectList | ✅ 正常 |
| 场景修改 | PUT /zm/baseScene/edit | ✅ 正常 |
| 照明状态 | POST /zm/lighting/lightList | ✅ 返回61条数据 |
| 分区状态 | GET /zm/point/getZoneStatus | ✅ 正常 |
| 场景状态 | GET /zm/point/getSceneStatus | ✅ 正常 |
| 回路状态 | GET /zm/point/getLoopStatus | ✅ 正常 |
| 全亮全灭状态 | GET /full/bright/status | ✅ 正常 |
| 告警列表 | POST /zm/record/getFaultSeven | ✅ 正常 (空数据) |
| 最新告警 | GET /zm/record/getNews | ✅ 正常 |
| 历史事件 | POST /zm/record/getRecordSeven | ✅ 正常 (空数据) |
| 历史记录 | POST /zm/record/getFaultAll | ✅ 正常 (空数据) |
| 交流回路 | POST /zm/cabinetInfo/acList | ✅ 正常 |
| 直流回路 | POST /zm/cabinetInfo/dccList | ✅ 正常 |
| 母线信息 | GET /zm/cabinetInfo/busInfo | ✅ 正常 |
| DC/AC信息 | POST /zm/cabinetInfo/dcAc | ✅ 正常 |
| 绝缘信息 | GET /zm/cabinetInfo/getBusInsulation | ✅ 正常 |
| 交流信息 | GET /zm/cabinetInfo/getAlternating | ✅ 正常 |
| 传感器数据 | POST /leakage/getTable | ✅ 正常 |
| 首页版本 | GET /zm/home/version | ✅ 返回 V0.3.1-mqtt |
| 首页机柜 | POST /zm/home/cabinetList | ✅ 返回61条数据 |
| 总耗电量 | GET /zm/home/powersByAll | ✅ 正常 |
| 日耗电量 | GET /zm/home/powersByDay | ✅ 正常 |
| 时控普通列表 | GET /zm/timeControl/simpleList | ✅ 正常 |
| 时控选择列表 | GET /zm/timeControl/simpleListSelect | ✅ 正常 |
| 场景时控选择 | GET /zm/timeControl/sceneListSelect | ✅ 正常 |
| 机柜选择列表 | GET /zm/timeControl/getCabinetList | ✅ 正常 |
| 分组列表 | POST /zm/timeControl/simpleGroupList | ✅ 正常 |
| 普通时控启用 | GET /zm/timeControl/getSimpleEnabled | ✅ 正常 |
| 场景时控启用 | GET /zm/timeControl/getSceneEnabled | ✅ 正常 |
| 场景列表(无分页) | GET /zm/timeControl/getSceneList | ✅ 正常 |
| 模块场景列表 | GET /zm/timeControl/getModuleSceneList | ✅ 正常 |
| 模块设置 | GET /zm/point/setModule | ✅ 正常 |
| 清除状态 | GET /zm/point/clearZoneStatus | ✅ 正常 |

### ⚠️ 设备离线相关 (预期行为)

| 接口 | 返回 | 说明 |
|------|------|------|
| GET /zm/write/updateZoneLightSwitch | 601 设备处于远程控制模式 | 设备离线，预期行为 |
| GET /zm/write/intoScenes | 601 设备处于远程控制模式 | 设备离线，预期行为 |
| GET /full/bright/set | 601 设备处于远程控制模式 | 设备离线，预期行为 |

---

## 修复优先级

1. **BUG-001** [CRITICAL] - 重新构建JAR并部署 (必须立即修复)
2. **BUG-004** [MEDIUM] - 添加分页参数默认值
3. **BUG-002** [MEDIUM] - 添加参数校验和错误信息
4. **BUG-005** [LOW] - 调查场景删除失败原因
5. **BUG-003** [LOW] - 确认接口是否存在或已废弃
