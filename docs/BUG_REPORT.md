# PM6-ZM 前端测试 BUG 报告

**测试日期**: 2026-06-06
**测试范围**: 全部前端页面 — 登录、导航、设备管理/控制/设置、历史记录、告警、系统管理
**测试环境**: Windows 11, 本地部署 (127.0.0.1:80)

---

## 🔴 BUG-001 [严重] 登录页 — 取消"记住密码"时 Cookie 清除逻辑错误

- **文件**: `web/src/views/login.vue:148-150`
- **现象**: 取消勾选"记住密码"后提交登录，三个 `Cookies.remove()` 调用全部使用 `'username'` 作为 key，导致 `password` 和 `rememberMe` 的 Cookie 永远不会被清除。
- **复现步骤**:
  1. 登录时勾选"记住密码"，成功登录
  2. 退出登录，取消勾选"记住密码"，再次登录
  3. 查看浏览器 Cookie，`password` 和 `rememberMe` 仍然存在
- **影响**: 用户隐私泄露风险 — 密码 Cookie 永久残留
- **严重程度**: 🔴 Critical
- **状态**: ✅ 已修复

```javascript
// 修复前（错误）:
isInElectron() ? CookiesInElectron.remove('username') : Cookies.remove('username')
isInElectron() ? CookiesInElectron.remove('username') : Cookies.remove('password')
isInElectron() ? CookiesInElectron.remove('username') : Cookies.remove('rememberMe')

// 修复后（正确）:
isInElectron() ? CookiesInElectron.remove('username') : Cookies.remove('username')
isInElectron() ? CookiesInElectron.remove('password') : Cookies.remove('password')
isInElectron() ? CookiesInElectron.remove('rememberMe') : Cookies.remove('rememberMe')
```

---

## 🔴 BUG-002 [严重] 照明状态卡片 — import 语句泄漏到模板中

- **文件**: `web/src/views/zm/baseStatus/components/card/index.vue:1`
- **现象**: 文件第 1 行 `import { color } from 'echarts';` 位于 `<template>` 标签之前，Vue SFC 解析器会将其视为模板文本内容，在页面上直接渲染为文字 `import { color } from 'echarts';`。
- **复现步骤**:
  1. 进入"照明状态"页面
  2. 查看机柜卡片顶部，可见一行代码文本
- **影响**: 页面显示异常，用户看到源代码文本
- **严重程度**: 🔴 Critical
- **状态**: ✅ 已修复（删除无效 import）

---

## 🟡 BUG-003 [中等] 照明状态卡片 — 空数据时可能崩溃

- **文件**: `web/src/views/zm/baseStatus/components/card/index.vue:29,52`
- **现象**: `v-if="!data.dcList.length"` 和 `v-if="!data.acList.length"` 在 `data.dcList` 或 `data.acList` 为 `undefined`/`null` 时会抛出 `TypeError: Cannot read property 'length' of undefined`。
- **复现步骤**:
  1. 后端返回数据中不含 `dcList` 或 `acList` 字段
  2. 页面白屏，控制台报错
- **影响**: 数据不完整时页面崩溃
- **严重程度**: 🟡 Medium
- **状态**: ✅ 已修复（添加可选链或默认值保护）

---

## 🟡 BUG-004 [中等] 告警列表 — 导入了未使用的 demo API

- **文件**: `web/src/views/zm/alarm/list/index.vue:89`
- **现象**: `import { listDemo } from '@/api/demo/demo'` 导入了 demo 模块的 API，但实际使用的是 `getFaultSeven`。这是开发阶段遗留的死代码。
- **影响**: 打包体积增大，代码可读性差
- **严重程度**: 🟡 Medium
- **状态**: ✅ 已修复（删除未使用 import）

---

## 🟡 BUG-005 [中等] 告警列表 — 生产环境遗留 console.log

- **文件**: `web/src/views/zm/alarm/list/index.vue:178`
- **现象**: `handleHeaderRowStyle` 方法中有 `console.log(row)`，每次表头渲染都会输出日志。
- **影响**: 控制台噪音，轻微性能影响
- **严重程度**: 🟡 Medium
- **状态**: ✅ 已修复

---

## 🟡 BUG-006 [中等] 全局 — 85 个文件中遗留 186 处 console.log

- **现象**: 项目中存在大量 `console.log` 调试语句，涉及设备管理、控制、设置、历史记录等所有模块。
- **影响**: 生产环境控制台噪音，暴露内部数据结构，轻微性能影响
- **严重程度**: 🟡 Medium
- **状态**: ⚠️ 已知问题（批量清理需谨慎，保留有意义的日志）

---

## 🟢 BUG-007 [低] 电量计算页面 — 注释掉的功能残留

- **文件**: `web/src/views/zm/history/powerCalculation/index.vue:12-13,38-43`
- **现象**: "机柜综合计算" tab 被注释掉，只保留"分区组合计算"。组件 import 仍存在但未使用。
- **影响**: 死代码，功能不完整
- **严重程度**: 🟢 Low
- **状态**: ⚠️ 已知问题

---

## 🟡 BUG-008 [中等] 首页机柜表格 — handleHeaderRowStyle 遗留 console.log

- **文件**: `web/src/views/components/table/IndexTable/index.vue:141`
- **现象**: `handleHeaderRowStyle` 方法中有 `console.log(row)`，每次表头渲染都会输出日志。
- **影响**: 控制台噪音
- **严重程度**: 🟡 Medium
- **状态**: ✅ 已修复

---

## 🟡 BUG-009 [中等] 历史事件页 — 引用未定义变量 daterangeCreateTime

- **文件**: `web/src/views/zm/history/event/index.vue:138`
- **现象**: `getList()` 中引用 `this.daterangeCreateTime` 但 `data()` 中从未定义该变量。该条件永远为 `null`，过滤逻辑为死代码。
- **影响**: 日期范围筛选功能无效（代码不会执行）
- **严重程度**: 🟡 Medium
- **状态**: ✅ 已修复（删除死代码）

---

## 🟡 BUG-010 [中等] 历史事件页 — handleHeaderRowStyle 遗留 console.log

- **文件**: `web/src/views/zm/history/event/index.vue:183`
- **现象**: 同 BUG-005/BUG-008 模式。
- **影响**: 控制台噪音
- **严重程度**: 🟡 Medium
- **状态**: ✅ 已修复

---

## 🟢 BUG-011 [低] 登录页 — path 方法名与导入的 path 模块冲突

- **文件**: `web/src/views/login.vue:69,117-119`
- **现象**: `import path from 'path'` 导入了 Node.js path 模块，同时 methods 中定义了 `path()` 方法。虽然 methods 中的方法会覆盖，但 `path` 导入本身在浏览器环境中不可用（无 Node.js API），属于无效导入。
- **影响**: Electron 环境下可能正常，浏览器环境下无效导入
- **严重程度**: 🟢 Low
- **状态**: ⚠️ 已知问题

---

## ✅ API 测试结果

| 接口 | 方法 | 状态 | 备注 |
|------|------|------|------|
| `/captchaImage` | GET | ✅ 200 | 验证码已禁用 |
| `/login` | POST | ✅ 200 | 返回 JWT token |
| `/getInfo` | GET | ✅ 200 | 返回用户信息+角色+权限 |
| `/getRouters` | GET | ✅ 200 | 返回完整菜单树（8个顶级菜单） |
| `/system/user/list` | GET | ✅ 200 | 返回5个用户 |
| `/zm/cabinetInfo/acList` | POST | ✅ 200 | 返回空列表（无设备数据） |

## ✅ 部署验证结果

| 服务 | 端口 | 状态 |
|------|------|------|
| MySQL | 3306 | ✅ 运行中 |
| Redis | 6379 | ✅ 运行中 |
| Java Backend | 8081 | ✅ HTTP 200 |
| Nginx | 80 | ✅ HTTP 200 |
| Mosquitto MQTT | 1883 | ⚠️ 未运行（非核心） |
