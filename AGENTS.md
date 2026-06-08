# AGENTS.md

This file provides guidance to Qoder (qoder.com) when working with code in this repository.

## Build & Run Commands

### Backend (Java / Spring Boot)
```bash
cd server
mvn clean package -DskipTests          # 编译打包
java -jar ruoyi-admin/target/ruoyi-admin.jar   # 运行 (端口 8081)
```

Active Spring profile: `dev` (default), `prod`, `local` — configured in `pom.xml` profiles section, loaded via `application-{profile}.yml`.

### Frontend (Vue 2 + Element UI)
```bash
cd web
npm install                # 安装依赖
npm run dev                # 开发服务器 (端口 80, 代理到 8081)
npm run build:prod         # 生产构建 → dist/
npm run lint               # ESLint 检查
```

### Windows Deployment (离线一键部署)
```bash
cd deploy/windows
python deploy.py start          # 启动全部 5 个服务
python deploy.py stop           # 停止全部服务
python deploy.py status         # 查看运行状态
python deploy.py init-db        # 初始化/重置数据库
```

The deploy script manages: Nginx (80), Java Backend (8081), MySQL (3306), Redis (6379), Mosquitto MQTT (1883).

### Database
- MySQL 8.0, database name `zm`
- Init script: `database/zm.sql`
- Connection configured in `application-{profile}.yml`

## High-Level Architecture

This is a **DC lighting monitoring system (PM6-ZM)** built on RuoYi-Vue-Plus v4.8.0. It monitors and controls physical lighting devices through a three-tier IoT architecture:

```
Physical Devices (lights/sensors) → Monitoring Screen (RS485) → MQTT/Modbus TCP → Java Backend → Vue Frontend
```

### Maven Module Map (what matters vs. what doesn't)

| Module | Role | Touch it? |
|--------|------|-----------|
| `ruoyi-admin` | Boot entrypoint + config YMLs | Only config files |
| `ruoyi-zm` | **Core: all lighting business logic** | **YES — primary workspace** |
| `ruoyi-framework` | Security, thread pools, framework glue | **NO** |
| `ruoyi-system` | Users, roles, menus (RuoYi built-in) | **NO** |
| `ruoyi-common` | Shared utils | **NO** |
| `ruoyi-generator` | Code generator | Rarely |
| `ruoyi-job` | Scheduled job infrastructure | Rarely |
| All others | OSS, SMS, demo, extend | Ignore |

All meaningful development happens inside `ruoyi-zm`.

### Inside ruoyi-zm: 16 packages by role

**Communication Layer** (how data enters/exits the system):
- `mqtt/` — MQTT message handler, publisher, device online detection. Subscribes to 5 topics (`/zm/+/heart`, `/zm/+/holding`, `/zm/+/coil`, `/zm/+/connect`, `/zm/+/request`), publishes to 29 write topics (`/zm/{ip}/write01` through `write29`)
- `mqttwrite/` — Builds MQTT write commands for loop control, scene control, AC switch, dimming, etc.
- `mqtt03/` — MQTT protocol v0.3 address-level parsing
- `modbus/` — Modbus TCP slave communication, register read/write, time sync
- `netty/` — Netty TCP server with Modbus TCP decoder

**Business Layer** (the data model and logic):
- `zm/` — 60+ domain entities, 55 mappers, 60 service interfaces+impls. Covers devices, regions, scenes, loops, alarms, energy calculation
- `web/controller/zm/` — 39 REST controllers, 5 write-operation controllers
- `web/websocket/` — Pushes device status changes to browser in real time

**Infrastructure Layer** (internal plumbing):
- `cache/` — 22 cache classes covering devices, loops, scenes, lighting, leakage, infrared, AC/DC switches, write queues
- `schedule/` — Scheduled tasks: command queues, telemetry/telecommand dispatch, data recovery
- `send/` — Outbound data dispatch (telemetry, remote send, 03 data update)
- `pubsub/` — Redis pub/sub for cross-instance messaging
- `init/` — System initialization: zones, scenes, MQTT subscriptions, schedules
- `config/`, `utils/`, `flag/`, `vo/` — Supporting utilities

### Two Communication Channels (not one)

| Channel | Trigger | Data Shape | Use Case |
|---------|---------|------------|----------|
| **HTTP REST** | User clicks button/page loads | Full object (dozens of fields) | Page init, detail view, save, export |
| **WebSocket** | MQTT data received | Delta (5 key fields) | Real-time status red/green indicators |

Both channels exist for the same device, delivering different data shapes for different purposes. Do not merge them.

### Three Data Flows to Understand

1. **Uplink** (device → browser): Monitoring screen → MQTT → `MqttMessageHandler` → Redis/MySQL → `DeviceStatusPushService` → WebSocket → Browser
2. **Downlink** (browser → device): HTTP POST → Controller → `WriteControlService` → `MqttPublisher` (or `ModbusTCPManager`) → Monitoring screen → RS485 → Device
3. **BAS external control**: BAS → MQTT `/zm/+/request` → `MainChannel.handleRequest()` → `RequestHandler` → MQTT/Modbus → Device

## Controller Writing Convention

Every controller follows the same template. Only three things change: URL prefix, injected dependencies, and method bodies.

```java
@RestController                      // Always present
@RequiredArgsConstructor              // Always present — generates constructor for final fields
@RequestMapping("/zm/xxx")           // VARY — URL prefix, match the feature
public class XxxController {

    // VARY — declare what you need, Spring injects it
    private final IXxxService service;
    private final WriteQueueCache cache;

    // VARY — one method per endpoint
    @GetMapping("/list")
    public TableDataInfo list(XxxBo bo) {
        return service.queryList(bo);
    }
}
```

Key rules:
- Controller never calls Mapper directly — always through Service
- Controller never uses RedisTemplate directly — use `WriteQueueCache` or `Key`
- Query endpoints often use `@PostMapping` (complex filter JSON in body), not `@GetMapping`
- Permission check: `@SaCheckPermission("zm:module:action")`

## Protocol Documentation Locations

| Protocol | Spec Location | Code Location |
|----------|--------------|---------------|
| MQTT | `docs/protocols/mqtt/PM6-ZM MQTT最新协议0.0.5.md` | `ruoyi-zm/.../mqtt/`, `mqttwrite/`, `mqtt03/` |
| Modbus | `docs/protocols/modbus/PM6-ZM最新协议3.8.2.xlsx` | `ruoyi-zm/.../modbus/`, `netty/` |
| BAS | `docs/protocols/bas/` (12 files) | Handled via MQTT `/zm/+/request` → `MainChannel` |

## Key Config Files

- `server/ruoyi-admin/src/main/resources/application.yml` — Main config: port, Sa-Token JWT, MyBatis-Plus, slave mode flag
- `web/vue.config.js` — Dev server port (80), proxy to `localhost:8081`
- `deploy/windows/deploy-config.ini` — Deployment parameters (DB creds, JVM opts, MQTT creds)

## Branch Strategy

Current branch: `PM6-ZM-omc` (created from `PM6-ZM-Claude`). All changes go here. `master` stays synced with upstream. Merge via PR only.
