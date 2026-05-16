# PM6-ZM 直流照明监控系统

基于 RuoYi-Vue-Plus v4.8.0 二次开发的直流照明监控管理系统，支持 MQTT、Modbus TCP、HTTP REST 三种通信协议。

## 项目结构

```
PM6-ZM/
├── server/          # 后端 Java 源码 (Spring Boot 2.7 + MyBatis-Plus, Maven 多模块)
├── web/             # 前端 Vue 2 源码 (Vue CLI + Element UI + Electron)
├── docs/            # 项目文档 (协议/需求/会议/测试/手册)
├── deploy/          # 部署包 (Windows / Docker)
├── database/        # 数据库初始化脚本
├── releases/        # 历史版本发布包
├── tools/           # 开发工具与本地测试
└── archive/         # 历史归档备份
```

## 技术栈

| 层 | 技术 |
|---|------|
| 后端框架 | Spring Boot 2.7.13, MyBatis-Plus 3.5.3.1, Sa-Token |
| 通信协议 | HTTP REST (8081), MQTT (1883), Modbus TCP (1503/1504) |
| 前端框架 | Vue 2.6, Element UI 2.15, ECharts 5.4 |
| 桌面端 | Electron 13 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis + Redisson + Caffeine |

## 构建与运行

### 后端

```bash
cd server
mvn clean package        # 编译打包
java -jar ruoyi-admin/target/ruoyi-admin.jar   # 运行
```

### 前端

```bash
cd web
npm install              # 安装依赖
npm run dev              # 启动开发服务器 (端口 80)
npm run build:prod       # 生产构建
```

## 文档索引

| 类别 | 路径 |
|------|------|
| 架构分析 | `docs/architecture/` |
| Modbus 协议 | `docs/protocols/modbus/` |
| MQTT 协议 | `docs/protocols/mqtt/` |
| BAS 对接协议 | `docs/protocols/bas/` |
| 需求文档 | `docs/requirements/` |
| 会议纪要 | `docs/meeting-notes/` |
| 测试记录 | `docs/test-records/` |
| 产品手册 | `docs/manuals/` |
| 问题记录 | `docs/issues/` |


