# API 与 Views 目录结构不对应问题

## 问题描述

前端项目的 `api/zm/` 和 `views/zm/` 两个目录的文件结构不一致，没有做到一一对应。

## 期望的结构（规范做法）

每个页面（views）应该有对应的 API 目录，且目录结构一致：

```
views/zm/                          api/zm/
├── baseRegion/                    ├── baseRegion/
│   └── index.vue                  │   ├── list.js
│                                  │   ├── add.js
│                                  │   └── ...
├── baseScene/                     ├── baseScene/
│   └── index.vue                  │   ├── list.js
│                                  │   └── ...
```

## 实际的结构（当前情况）

```
views/zm/                          api/zm/
├── baseRegion/   (文件夹)          ├── baseRegion.js   (单文件)
├── baseScene/    (文件夹)          ├── baseScene.js    (单文件)
├── baseControl/  (文件夹)          ├── baseControl.js  (单文件)
├── baseDevice/   (文件夹)          ├── baseDevice.js   (单文件)
├── baseDistrict/ (文件夹)          ├── baseDistrict.js (单文件)
├── baseStatus/   (文件夹)          ├── baseStatus.js   (单文件)
├── baseInstructs/(文件夹)          │ （无对应文件 ❌）
├── alarm/                          ├── alarm/
│   └── list/                       │   └── list.js
│       └── index.vue               │
├── device/                         ├── device/
│   ├── dev/                        │   ├── dev.js
│   ├── dev-control/                │   ├── dev-control.js
│   ├── record/                     │   ├── record.js
│   └── setting/                    │   ├── setting.js
│                                   │   └── instructs.js  ← 位置不对
└── history/                        └── history/
    ├── event/                          ├── event.js
    └── record/                         ├── record.js
                                        └── powerCalculation.js ← views 无对应页面
```

## 具体问题

| 问题类型 | 具体描述 |
|---------|---------|
| 结构不一致 | views 是文件夹，api 是单个 .js 文件 |
| views 有但 api 没有 | `views/baseInstructs/` 没有对应的 api 文件 |
| api 有但 views 没有 | `api/history/powerCalculation.js` 没有直接对应的页面 |
| 文件名不对应 | `api/alarm/list.js` 对应 `views/alarm/list/index.vue`，层级不一致 |
| 位置不对应 | `api/device/instructs.js` 放在 device 下，但 views 里是 `baseInstructs/` |

## 影响

- 不影响功能运行
- 但找接口时不够直观，新人接手时容易困惑
- 不符合前端项目规范

## 建议修复方案

1. 统一目录结构，让 api/ 和 views/ 一一对应
2. 缺失的 api 文件补上（如 baseInstructs）
3. 位置不对的文件移正确（如 instructs.js）
4. 多余的 api 文件确认是否有对应页面（如 powerCalculation.js）
