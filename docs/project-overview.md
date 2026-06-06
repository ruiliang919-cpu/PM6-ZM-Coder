# PM6-ZM-Coder 项目框架图

## 整体结构

```
PM6-ZM-Coder/
│
├── server/                          ← Java 后端（Spring Boot）
│   ├── pom.xml                      ← Maven 父工程，管版本
│   │
│   ├── ruoyi-admin/                 ← ★ 启动入口 + 配置文件
│   │   └── resources/
│   │       ├── application.yml          ← 主配置
│   │       ├── application-prod.yml     ← 生产环境配置
│   │       └── application-create.yml   ← 开发环境配置
│   │
│   ├── ruoyi-framework/             ← 框架层：安全、线程池、异常处理
│   ├── ruoyi-system/                ← 系统功能：用户、角色、菜单、部门
│   ├── ruoyi-common/                ← 通用工具类
│   ├── ruoyi-extend/                ← 扩展：定时任务(xxl-job)
│   ├── ruoyi-demo/                  ← 示例代码（可忽略）
│   │
│   └── ruoyi-zm/                    ← ★★★ 核心业务模块（你要了解的）
│       └── src/main/java/com/ruoyi/
│           ├── zm/                  ← 设备、场景、分组管理
│           ├── mqtt/                ← MQTT 通讯（设备心跳、遥测）
│           ├── mqtt03/              ← MQTT 协议解析（地址级）
│           ├── modbus/              ← Modbus TCP 通讯
│           ├── netty/               ← Netty 网络层
│           ├── cache/               ← Redis 缓存
│           ├── pubsub/              ← Redis 消息订阅
│           ├── send/                ← 数据下发/命令发送
│           ├── schedule/            ← 定时任务
│           ├── config/              ← MQTT/Modbus 配置
│           ├── utils/               ← 工具类
│           ├── flag/                ← 状态标志位
│           └── web/controller/zm/   ← ★★ API 接口层
│               └── write/           ←   设备写操作（控制命令）
│
├── web/                             ← Vue 前端
│   └── src/
│       ├── views/zm/                ← ★★★ IoT 业务页面
│       │   ├── baseDevice/          ←   设备管理
│       │   ├── baseStatus/          ←   设备状态总览
│       │   ├── baseControl/         ←   场景/分区控制
│       │   ├── device/dev/          ←   设备详情（交直流回路等）
│       │   ├── device/record/       ←   设备记录
│       │   └── alarm/               ←   告警
│       ├── api/zm/                  ← ★ 后端接口调用
│       ├── utils/                   ← 工具（WebSocket、请求封装）
│       └── store/                   ← Vuex 状态管理
│
├── database/                        ← 数据库脚本
├── deploy/                          ← 部署配置
├── docs/                            ← 文档
└── tools/                           ← 工具脚本
```

---

## 物理架构（现实世界怎么连的）

### 设备清单

| 实体 | 是什么 | IP | 上面跑了什么 |
|------|--------|-----|-------------|
| 灯/传感器/供电设备 | 物理硬件 | 无 | RS485/Modbus 从机 |
| 监控屏 | 现场数据采集器 | 192.168.2.x | Modbus 主机（对下）、MQTT 客户端（对上）、Modbus TCP 服务端 |
| 主机 | 跑本项目的电脑 | 192.168.2.x 网段 | Java 程序、mosquitto broker、MySQL、Redis |
| mosquitto | MQTT 消息中间件 | 127.0.0.1:1883 | 安装在主机上，中转 MQTT 消息 |
| 浏览器 | 用户的电脑/显示器 | 任意 | 连主机的网页 |

### 线路1：监控屏上报数据到主机（自动，一直跑）

```
灯/传感器 ──RS485/Modbus RTU──→ 监控屏（采集数据）
                                       │
          监控屏 通过 MQTT 协议 发布消息到 mosquitto
          主题 /zm/99/heart /zm/99/holding /zm/99/coil 等
                                       │
                                       ↓
                              mosquitto broker（127.0.0.1:1883）
                                       │
          Java 程序 通过 MQTT 协议 订阅上述主题，收到数据
                                       │
                                       ↓
                              Java 程序（MqttMessageHandler 处理）
                                       │
                          存入 Redis / 数据库，通过 WebSocket 推到浏览器
                                       │
                                       ↓
                              浏览器页面实时更新
```

方向：**data从右往左 → Java从mosquitto收 → Java推给浏览器**

### 线路2：用户在浏览器上发控制命令

```
用户点"开灯"  ──HTTP POST──→  Java Controller（WriteControlController 等）
                                       │
                       ┌───────────────┤
                       │               │
                       ↓               ↓
              走 MQTT 下发          走 Modbus TCP 下发
              MqttPublisher         ModbusTCPManager
              发布到 mosquitto      直连监控屏IP:502
                       │               │
                       ↓               ↓
              监控屏从 mosquitto     监控屏收到 TCP 命令
              订阅拿到命令               │
                       │               │
                       └───────┬───────┘
                               ↓
                     监控屏通过 RS485/Modbus
                     发指令给灯/传感器
                               ↓
                          灯开了/灭了
```

方向：**浏览器 → HTTP → Java → MQTT或ModbusTCP → 监控屏 → RS485 → 设备**

### 线路3：BAS 外部系统发控制命令

```
BAS ──MQTT(主题/zm/+/request)──→ mosquitto(127.0.0.1:1883)
                                            │
                                            ↓
                                   Java 程序 MainChannel.handleRequest()
                                            │
                                   RequestHandler 处理
                                            │
                                            ↓
                                   MQTT 或 Modbus TCP 下发给监控屏
```

方向：**BAS → mosquitto → Java → 监控屏 → 设备**

### 为什么 MQTT 和 Modbus TCP 都在用

| | MQTT | Modbus TCP |
|--|------|------------|
| 谁主动 | 监控屏主动推 | 主机主动去问 |
| 走什么端口 | mosquitto 的 1883 | 监控屏的 502（或其他） |
| 干什么 | 心跳、状态上报、命令下发 | 精确读写某个寄存器、对时 |
| 代码位置 | `MqttMessageHandler`(收) `MqttPublisher`(发) | `ModbusTCPManager` |

### MQTT 主题清单

**主机订阅（收数据）— 5 个主题**

| 主题 | 用途 |
|------|------|
| `/zm/+/heart` | 监控屏心跳 |
| `/zm/+/holding` | 寄存器数值上报 |
| `/zm/+/coil` | 开关状态上报 |
| `/zm/+/connect` | 监控屏上下线通知 |
| `/zm/+/request` | 监控屏/BAS 请求数据 |

> `+` 是通配符，`/zm/99/heart`、`/zm/100/heart` 都匹配到同一个处理器

**主机发布（发命令）— 29 个主题**

| 主题 | 用途 | 主题 | 用途 |
|------|------|------|------|
| `/zm/{ip}/write01` | 对时设置 | `/zm/{ip}/write16` | 回路总开关 |
| `/zm/{ip}/write02` | 模块输出设置 | `/zm/{ip}/write17` | 整流模块开关机 |
| `/zm/{ip}/write03` | 添加回路 | `/zm/{ip}/write18` | 分组控制总开关 |
| `/zm/{ip}/write04` | 删除回路 | `/zm/{ip}/write19` | 场景控制 |
| `/zm/{ip}/write05` | 分组命名 | `/zm/{ip}/write20` | 照度外控值 |
| `/zm/{ip}/write06` | 场景命名 | `/zm/{ip}/write21` | 场景设置 |
| `/zm/{ip}/write07` | 分区编号 | `/zm/{ip}/write22` | 其他开关控制 |
| `/zm/{ip}/write08` | 普通时控 | `/zm/{ip}/write23` | 设置更新标志位 |
| `/zm/{ip}/write09` | 场景时控 | `/zm/{ip}/write24` | 事件记录 |
| `/zm/{ip}/write10` | 红外模式 | `/zm/{ip}/write25` | 照度模式选择 |
| `/zm/{ip}/write11` | 照度模式 | `/zm/{ip}/write26` | 红外模式选择 |
| `/zm/{ip}/write12` | 交流开关 | `/zm/{ip}/write27` | 交流开关控制 |
| `/zm/{ip}/write13` | 模式选择 | `/zm/{ip}/write28` | 保存回路 |
| `/zm/{ip}/write14` | 系统总开关 | `/zm/{ip}/write29` | 获取心跳和版本号 |
| `/zm/{ip}/write15` | 工作模式 | | |

> {ip} 是设备 IP 最后一段，比如 192.168.2.99 对应 `/zm/99/write01`

**监控屏的方向反过来：** 监控屏发 heart/holding/coil/connect/request，收 write01~write29。

---

## 你需要了解的部分（核心业务）

### ruoyi-zm/src/main/java/com/ruoyi/ 目录拆解（17个包）

```
com/ruoyi/                          ← 636个Java文件，分成4层
│
├ ★★★ 业务层（先看这个）    
│   ├── zm/                  430个  ← 数据表实体、Mapper、Service接口+实现
│   │   ├── domain/                 ← 数据库表对应的Java类
│   │   ├── domain/bo/              ← 请求参数对象
│   │   ├── domain/vo/              ← 返回给前端的对象（133个）
│   │   ├── mapper/                 ← 数据库操作（54个）
│   │   ├── service/                ← 业务接口（54个）
│   │   ├── service/impl/           ← 业务实现（54个）
│   │   ├── config/                 ← 业务配置
│   │   └── constants/              ← 常量
│   └── web/                  46个  ← API接口
│       ├── controller/zm/          ← 前端调用的接口（39个）
│       ├── controller/zm/write/    ← 设备写操作接口（5个）
│       └── websocket/              ← WebSocket推送
│
├ ★★ 通讯层（理解就好，数据进出）
│   ├── mqtt/                 7个  ← MQTT核心：收消息、发命令
│   ├── mqtt03/              65个  ← MQTT协议解析（地址级handler+请求handler）
│   ├── mqttwrite/           14个  ← MQTT写命令构建
│   ├── modbus/               4个  ← Modbus工具：对时监听器、寄存器读写
│   └── netty/                6个  ← 废弃的Netty方案，不用管
│
├ ★ 基础设施层（用到再看）
│   ├── cache/               22个  ← Redis缓存读写（含 WriteQueueCache）
│   ├── pubsub/               3个  ← Redis消息订阅
│   ├── config/               3个  ← MQTT Broker连接、Modbus连接池
│   ├── schedule/            10个  ← 定时任务
│   ├── send/                 9个  ← 数据下发/命令发送
│   ├── init/                 6个  ← 启动初始化
│   ├── flag/                 2个  ← 状态标记
│   ├── utils/                8个  ← 工具类
│   └── vo/                   2个  ← 通用对象
│
└ （没有不用管的）
```

### 按学习优先级排序

| 优先级 | 目录 | 作用 | 什么时候看 |
|--------|------|------|-----------|
| ★★★ | `zm/` | 数据+业务 | 第一个看 |
| ★★★ | `web/controller/zm/` | API接口 | 第二个看 |
| ★★ | `mqtt/` | MQTT收发 | 想知道数据怎么进来的 |
| ★★ | `modbus/` | Modbus读写 | 想知道怎么控制设备的 |
| ★ | `cache/` | Redis缓存 | 遇到缓存问题 |
| ★ | `schedule/` | 定时任务 | 遇到定时任务 |
| ☆ | 其他 | 配置/工具 | 用到再说 |

---

## HTTP 接口 vs WebSocket 推送

### 职责分工

| | HTTP Controller | WebSocket |
|--|----------------|-----------|
| 谁触发 | 用户点按钮/页面加载 | MQTT 收到数据后自动触发 |
| 给什么 | 全量数据（几十个字段） | 增量变化（几个关键字段） |
| 多久一次 | 请求一次返回一次 | 数据变化就推 |
| 干什么 | 页面初始化、查详情、发命令 | 页面实时更新，不用刷新 |

### 数据内容不同

同一个设备，两个通道给的数据不一样：

```json
// HTTP 返回（全量）
GET /zm/baseDevice/list
{"id": 1, "deviceName": "1号设备", "ip": "192.168.2.99", "port": 502,
 "onlineStatus": 1, "runMode": 0, "temperature": 25.5, "acLoopNum": 6, ...几十个字段}

// WebSocket 推送（只给变化的）
/topic/device/status/1
{"deviceNo": 1, "onlineStatus": 1, "version": "1.2.6", "lastTime": 1749805188, "ip": "99"}
// 就 5 个字段，页面只需要这几个来判断"灯变红还是变绿"
```

### 数据是怎么推到浏览器的

```
MQTT 收到监控屏心跳 → MqttMessageHandler → DeviceStatusPushService → WebSocket → 浏览器
MQTT 收到故障消息   → Update03DataService  → DeviceStatusPushService → WebSocket → 浏览器
```

### Controller 目录结构

```
web/controller/zm/          39个  ← HTTP 接口，前端调一次返一次
web/controller/zm/write/     5个  ← 设备写操作接口
web/websocket/                     ← WebSocket 推送，后端主动推到前端
```

### WebSocket 通用用途

WebSocket 就是为"服务器主动推"这个场景设计的。HTTP 只能客户端先问、服务器再答。

| 场景 | 为什么用 WebSocket |
|------|-------------------|
| 在线聊天 | 别人发消息，你立刻收到 |
| 股票行情 | 价格变动，页面自动刷新 |
| 在线协作 | 多人同时编辑文档，互相看到 |
| 游戏 | 队友操作实时同步 |
| IoT 设备监控 | 设备状态变化，不用手动刷新 |

> 只要是"数据变了，前端不用刷新就能看到"的场景，全是 WebSocket。

---

## Controller 书写套路

39 个 Controller 全是同一个模板，只有三个地方变化：URL 前缀、注入的依赖、方法里调什么。

### 模板骨架（逐行解释）

```java
// ===== 第1块：包名 =====
// 【变】在哪个文件夹就必须写这个，不然报错
// 【IDE自动】新建文件时 IDEA 自动生成
package com.ruoyi.web.controller.zm;

// ===== 第2块：import =====
// 【变】方法里用到什么类，这里就导什么
// 【IDE自动】你只管在代码里用类名，IDEA 自动加 import。缺了红线提示，多了自动删
import com.ruoyi.zm.service.IXxxService;         // 用到 Service
import org.springframework.web.bind.annotation.*; // 用到 GetMapping 等注解
import lombok.RequiredArgsConstructor;             // 用到 @RequiredArgsConstructor
// ... 省略几十行，不用管

// ===== 第3块：类声明 =====
@RestController                      // 【不变】每个 Controller 都要有，固定写法
@RequiredArgsConstructor              // 【不变】固定写法，省去手写构造函数
@RequestMapping("/zm/xxx")            // 【变，手写】URL 前缀，跟着页面功能取名
public class XxxController {          // 【变，手写】类名，跟 URL 对应

    // ===== 第4块：注入依赖 =====
    // 【变，手写】你需要调哪个 Service/Cache，就在这里声明，Spring 自动传进来
    private final IXxxService service;       // 查数据库的业务逻辑（通过 Service 层，不直接调 Mapper）
    private final WriteQueueCache cache;     // Redis 缓存操作（通过 Cache 层，不直接用 RedisTemplate）
    private final MqttPublisher publisher;   // 发 MQTT 命令给监控屏
    private final Key key;                   // 缓存工具（遥测/遥信数据读取）
    // ... 需要什么就加一行

    // ===== 第5块：方法（每个接口一个方法）=====
    // 【变，手写】前端每个API调到这里，方法名和URL自己取

    @GetMapping("/list")                     // GET 请求，查数据用
    @SaCheckPermission("zm:xxx:list")       // 权限注解（可选）
    public TableDataInfo list(XxxBo bo) {    // 入参是前端传来的查询条件
        return service.queryList(bo);        // 调 Service，把结果返回
    }

    @PostMapping("/save")                    // POST 请求，保存数据用
    public R<Void> save(@RequestBody XxxBo bo) {
        service.save(bo);
        return R.ok();                       // 保存成功，返回 ok
    }
}
```

### 五个部分总结

| 块 | 变/不变 | 手写/自动 |
|----|---------|----------|
| 1. `package` | 变 | IDE 自动生成 |
| 2. `import` | 变 | IDE 自动管理 |
| 3. 类声明：`@RestController` `@RequiredArgsConstructor` | 不变 | 手写（但就两行） |
| 3. 类声明：`@RequestMapping` `class XxxController` | 变 | 手写（URL 前缀 + 类名） |
| 4. `private final Xxx` 依赖 | 变 | 手写（需要谁写谁） |
| 5. `@GetMapping/@PostMapping` + 方法体 | 变 | 手写（每个接口自己写） |

> 实际手写的就两块半：**类名和 URL**、**依赖声明**、**方法**。其他全是 IDE 的事。

**查询类（DevBaseDeviceController）：**
```java
@RestController
@RequestMapping("/zm/baseDevice")
@RequiredArgsConstructor
public class DevBaseDeviceController extends BaseController {

    // 注入：Service + Mapper + 缓存
    private final IDevBaseDeviceService iDevBaseDeviceService;
    private final DevBaseDeviceMapper baseMapper;
    private final Key key;

    // 方法：查列表、导Excel、看详情
    @PostMapping("/list")
    public TableDataInfo<DevBaseDeviceVo> list(DevBaseDeviceBo bo, @RequestBody PageQuery pageQuery) {
        return iDevBaseDeviceService.queryPageList(bo, pageQuery);
    }
}
```

**主页类（HomeController）：**
```java
@RestController
@RequestMapping("/zm/home")
@RequiredArgsConstructor
public class HomeController {

    // 注入：Service + Cache + 工具类
    private final IDevBaseDeviceService deviceService;
    private final DeviceCache deviceCache;
    private final Key key;

    // 方法：查版本号、查电量、查机柜列表
    @GetMapping("/version")
    public R<String> version() {
        return R.ok("获取主机版本号成功", version);
    }
}
```

**写操作类（WriteControlController）：**
```java
@RestController
@RequestMapping("/zm/write/control")
@RequiredArgsConstructor
public class WriteControlController {

    // 注入：业务逻辑委托给 Service，Redis 操作委托给 WriteQueueCache
    private final WriteControlService writeControlService;
    private final WriteQueueCache writeQueueCache;
    private final ModuleGuard moduleGuard;

    // 方法：校验权限后委托给 Service 处理
    @PostMapping("/updateSimpleControl")
    public R<?> updateSimpleControl(@RequestParam Integer deviceId, @RequestBody SimpleValue simpleValue) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeControlService.updateSimpleControl(deviceId, simpleValue);
    }
}
```

### 规律总结

| 块 | 是否变化 | 说明 |
|------|---------|------|
| 1. `package` | 变 | 在哪个文件夹写哪个路径 |
| 2. `import` | 变 | IDE 自动加，不用手写 |
| 3. 类声明 `@RestController` + `@RequiredArgsConstructor` + `@RequestMapping` | 前两个不变，第三个变 | 固定套路，URL 前缀跟着功能走 |
| 4. `private final Xxx` 依赖 | 变 | 用到什么 Service/Cache 就注入什么（Controller 不直接用 Mapper/RedisTemplate） |
| 5. 方法 `@GetMapping/@PostMapping` + 方法体 | 变 | 每个接口一个方法，调 Service 或发 MQTT |

> 看 Controller 时直接跳过 import 段，看第 4 块（注入）知道它依赖谁，看第 5 块（方法）知道它能干什么。

### HTTP 请求的四种方式

| 注解 | HTTP 方法 | 干什么 | 类比 |
|------|----------|--------|------|
| `@GetMapping` | GET | 查数据 | 看书，不写字 |
| `@PostMapping` | POST | 新增数据、提交操作 | 在书上写新笔记 |
| `@PutMapping` | PUT | 修改已有数据 | 擦掉重写 |
| `@DeleteMapping` | DELETE | 删除数据 | 撕掉一页 |

这个项目基本只用 GET 和 POST：

```java
@GetMapping("/list")          // 查列表，数据拼在 URL 参数里
@PostMapping("/updateLoop1")  // 更新/控制/保存，数据放在请求体里
```

> 只读的用 GET，要改东西的用 POST。这是约定俗成，不是技术限制。

### import 分类速查

同一个 Controller 的 import，按包名前缀就能分清来源。以 `DevBaseDeviceController` 为例：

**JDK 自带（最底层，不用记）：**
```java
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
```

**Lombok（省代码的工具）：**
```java
import lombok.RequiredArgsConstructor;    // 自动生成构造函数
```

**Spring 框架：**
```java
import org.springframework.web.bind.annotation.*;          // @RestController、@GetMapping 等
import org.springframework.validation.annotation.Validated; // @Validated 参数校验
// import org.springframework.data.redis.core.RedisTemplate;  // 已封装到 WriteQueueCache，Controller 不直接用
```

**Sa-Token 框架（权限）：**
```java
import cn.dev33.satoken.annotation.SaCheckPermission;  // 权限检查
import cn.dev33.satoken.annotation.SaIgnore;            // 免登录
```

**第三方工具：**
```java
import cn.hutool.core.bean.BeanUtil;     // Hutool，对象拷贝
import com.baomidou.mybatisplus...;       // MyBatis-Plus，查询条件构建
import com.serotonin.modbus4j...;         // Modbus4j 异常
```

**若依框架（通用底座）：**
```java
import com.ruoyi.common.core.domain.R;           // 统一返回格式 R.ok()
import com.ruoyi.common.core.page.TableDataInfo;  // 分页返回格式
import com.ruoyi.common.core.domain.PageQuery;     // 分页参数
import com.ruoyi.common.utils.poi.ExcelUtil;       // Excel 导出工具
import com.ruoyi.common.annotation.RepeatSubmit;    // 防重复提交
import com.ruoyi.common.core.controller.BaseController;  // 基类
```

**本项目自己写的（`com.ruoyi.zm.` 开头，能点进去看源码）：**
```java
import com.ruoyi.zm.service.IDevBaseDeviceService;   // Service（Controller 通过 Service 访问数据）
import com.ruoyi.zm.domain.DevBaseDevice;             // 实体
import com.ruoyi.zm.domain.bo.DevBaseDeviceBo;        // 请求参数
import com.ruoyi.zm.domain.vo.DevBaseDeviceVo;        // 返回对象
import com.ruoyi.cache.WriteQueueCache;               // Redis 操作封装（Controller 不直接用 RedisTemplate）
import com.ruoyi.cache.Key;                           // 缓存工具（遥测/遥信数据读取）
```

### 按包名前缀一眼判断

| import 包名开头 | 来源 | 怎么知道有 |
|---------------|------|-----------|
| `java.` 或 `javax.` | JDK 自带 | 基础语法 |
| `lombok.` | Lombok | 抄已有代码 |
| `org.springframework.` | Spring 框架 | 背几个常用的 |
| `cn.dev33.satoken.` | Sa-Token | 抄已有代码 |
| `cn.hutool.` | Hutool | 抄已有代码 |
| `com.baomidou.` | MyBatis-Plus | 抄已有代码 |
| `com.ruoyi.common.` | 若依框架 | 抄已有代码 |
| `com.ruoyi.cache.` | 本项目缓存层 | WriteQueueCache、Key 等 |
| `com.ruoyi.zm.` | 本项目业务层 | Service、实体、VO |

> 除了 `java.` 是基础，剩下全靠在已有代码里抄。写 Java 不是靠背，是靠抄。抄多了自然记住了。

### @SaCheckPermission 权限注解

**来源：** Sa-Token 框架（`cn.dev33.satoken`），不是 Spring 自带的。若依框架集成进来的。

**作用：** 有人调这个接口时，框架先检查这个人有没有指定权限。没有就直接拒绝，后面的代码不执行。

**权限命名规则：** `项目:功能模块:操作`

```
zm : baseDevice : list
│      │           │
│      │           操作（list=查列表, query=看详情, export=导出）
│      │
│      功能模块（baseDevice=设备, baseRegion=区域, baseScene=场景...）
│
项目前缀（zm=这个项目，区别于若依自带的 system:user:list）
```

这些权限字符串定义在数据库的菜单表里，管理员在网页后台给不同角色勾选不同权限。

**URL 路径 vs 请求方式（两码事）：**

```java
@PostMapping("/list")     // "/list" 是 URL 路径，POST 是请求方式
@GetMapping("/init")      // "/init" 是 URL 路径，GET 是请求方式
```

URL 路径是自己取的名字，叫 `list`、`queryAll`、`getData` 都行。POST/GET 决定数据怎么传，路径决定走哪个方法。各管各的。

> 这个项目用 POST 查列表，是因为查询条件比较复杂（放 JSON body 里），GET 的话参数全得拼 URL 上，太长。

### 项目全部注解速查

#### Spring 框架注解（标记类给 Spring 管）

| 注解 | 位置 | 作用 |
|------|------|------|
| `@RestController` | 类上 | 标记这个类是 HTTP 接口 |
| `@Service` | 类上 | 标记这个类是业务逻辑，Spring 管理 |
| `@Component` | 类上 | 标记这个类是通用组件，Spring 管理 |

#### HTTP 请求映射（前端调哪个 URL 进哪个方法）

| 注解 | 位置 | 作用 |
|------|------|------|
| `@RequestMapping("/zm/xxx")` | 类上 | URL 前缀 |
| `@GetMapping("/list")` | 方法上 | GET 请求，查数据 |
| `@PostMapping("/save")` | 方法上 | POST 请求，新增/提交 |
| `@PutMapping("/update")` | 方法上 | PUT 请求，修改 |
| `@DeleteMapping("/{id}")` | 方法上 | DELETE 请求，删除 |

#### 请求参数获取（从 HTTP 请求中拿数据）

| 注解 | 位置 | 作用 |
|------|------|------|
| `@RequestBody` | 参数前 | 取 POST 请求体里的 JSON |
| `@RequestParam` | 参数前 | 取 URL 后面的 `?pageNum=1` |
| `@PathVariable` | 参数前 | 取 URL 路径里的 `/{id}` |

#### Lombok 注解（省写重复代码）

| 注解 | 位置 | 作用 |
|------|------|------|
| `@RequiredArgsConstructor` | 类上 | 给 `private final` 字段自动生成构造函数 |
| `@Slf4j` | 类上 | 自动生成 `log` 对象，直接 `log.info(...)` |
| `@Data` | 类上 | 自动生成 getter/setter/toString/equals |

#### 参数校验（拦非法输入）

| 注解 | 位置 | 作用 |
|------|------|------|
| `@Validated` | 类上 | 开启参数校验功能 |
| `@NotNull` | 参数前 | 这个值不能为 null |
| `@NotEmpty` | 参数前 | 这个字符串不能是空串 |
| `@RequestBody` | 参数前 | 同时校验 JSON 内部的字段 |

#### 权限控制（Sa-Token）

| 注解 | 位置 | 作用 |
|------|------|------|
| `@SaCheckPermission("zm:xxx:list")` | 方法上 | 有指定权限才能调 |
| `@SaIgnore` | 方法上 | 这个接口不需要登录 |

#### 防重复提交

| 注解 | 位置 | 作用 |
|------|------|------|
| `@RepeatSubmit` | 方法上 | 同一个请求短时间内不能重复提交 |

#### 日志

| 注解 | 位置 | 作用 |
|------|------|------|
| `@Log(title = "机柜", businessType = EXPORT)` | 方法上 | 操作记录写进数据库日志表 |

#### 定时任务 & 缓存

| 注解 | 位置 | 作用 |
|------|------|------|
| `@Scheduled(fixedDelay = 5000)` | 方法上 | 每隔 5 秒自动执行一次 |
| `@PostConstruct` | 方法上 | 项目启动时执行一次 |
| `@Cacheable` | 方法上 | 方法结果自动缓存到 Redis |
| `@CacheConfig` | 类上 | 指定缓存前缀 |

#### 事务 & 注入

| 注解 | 位置 | 作用 |
|------|------|------|
| `@Transactional` | 方法/类上 | 开启数据库事务，出错自动回滚 |
| `@Resource` | 字段上 | 另一种注入方式（和 `private final` 效果类似） |
| `@Value("${...}")` | 字段上 | 从配置文件读值 |

#### 标记说明

| 注解 | 位置 | 作用 |
|------|------|------|
| `@Override` | 方法上 | 表示覆盖父类/接口的方法 |
| `@Deprecated` | 类/方法上 | 标记过时了，别用 |
| `@Param` | 参数前 | MyBatis 的 SQL 参数标记 |

#### 你常接触的 vs 基本不碰的

| 频率 | 注解 |
|------|------|
| 天天见 | `@RestController` `@RequiredArgsConstructor` `@RequestMapping` `@GetMapping` `@PostMapping` `@RequestBody` `@Slf4j` |
| 偶尔见 | `@SaCheckPermission` `@Validated` `@NotNull` `@PathVariable` `@RequestParam` `@RepeatSubmit` `@Value` |
| 基本不碰 | `@Cacheable` `@Scheduled` `@Transactional` `@Resource` `@Deprecated` |

---

## 依赖注入（Dependency Injection）

### 我们想干什么

在 Controller 里写业务，需要调用别的类的方法。比如查设备列表，需要调 `IDevBaseDeviceService` 里的 `queryPageList()` 方法。

**我们的目的：在 Controller 里能方便地调用 Service、Mapper 等方法。**

### 直接调遇到的问题

```java
// 想调 IDevBaseDeviceService 的方法，得先拿到它的对象
IDevBaseDeviceService service = new DevBaseDeviceServiceImpl();
// ↑ ServiceImpl 里又需要 Mapper
//    Mapper 里又需要数据源
//    你得一层层手动 new，麻烦且容易出错
service.queryPageList(bo, pageQuery);
```

每用一个类就要搞清楚它里面依赖了什么、怎么一层层 new，重复又繁琐。

### 依赖注入解决的就是这个

把这些重复的 new 对象、管理依赖的活交给 Spring 框架。我们要用什么，声明一下就行：

```java
private final IDevBaseDeviceService service;   // 声明"我需要这个"

// 然后直接用它的方法
service.queryById(id);      // 不用 new，不用管它里面依赖了谁
service.queryList(bo);      // Spring 已经把组装好的对象传进来了
```

### 写法就一行

```
private final  类型  名字;

private final  IDevBaseDeviceService  iDevBaseDeviceService;  // 后面就能调 Service 的方法
private final  DevBaseDeviceMapper    baseMapper;              // 后面就能调 Mapper 的方法
private final  Key                    key;                     // 后面就能调缓存工具的方法
```

需要调哪个类的方法，就写一行 `private final 类型 名字;`，然后直接 `名字.方法()` 调用。

### 和 @RequiredArgsConstructor 的关系

```java
@RequiredArgsConstructor            // ← 给所有 private final 字段自动生成构造函数
public class XxxController {
    private final IXxxService service;    // ← private final 的字段自动进构造函数
}
// Spring 看到构造函数需要 IXxxService，就在容器里找到它，传进来
// 我们只需要 private final 声明 + 注解，剩下的 Spring 全包了
```

> 一句话：**想用什么类的方法，就 `private final` 声明一行。Spring 自动帮我们搞定它内部的所有依赖，让我们拿来就能调。**

---

```
1. 对齐需求  →  搞清楚要做什么，什么效果
2. 定接口契约 →  前后端约定：URL、请求参数、返回格式
3. 前后端并行开发 → 各写各的，对着约定的格式写
4. 联调 →  前端接上真实后端，修不匹配的地方
```

### 读懂现有代码（4步，反过来追）

```
1. 看前端页面  →  知道这个功能干什么
2. 看 API 调用  →  页面调了哪个接口
3. 看 Controller →  接口调了哪个 Service
4. 看 Service   →  Service 操作了哪张表
```

### 接口契约示例

```json
// 前后端约定：
GET /zm/baseDevice/list?pageNum=1&pageSize=10

// 后端返回格式：
{
  "code": 200,
  "rows": [{"id": 1, "deviceName": "1号设备", ...}],
  "total": 60
}
```

> 定好之后前端用假数据开发，后端配合返回真数据，最后联调。

---

## 不用动的部分（框架自带）

| 模块 | 作用 | 为什么不用管 |
|------|------|-------------|
| ruoyi-admin | 启动入口 | 只改配置文件 |
| ruoyi-framework | 安全认证、线程池 | 框架层，改了容易出全局问题 |
| ruoyi-system | 用户/角色/菜单 | 若依自带，业务无关 |
| ruoyi-common | 工具类 | 通用代码，基本不用动 |
| ruoyi-extend | xxl-job | 定时任务框架 |
| ruoyi-demo | 示例代码 | 演示用的 |

---

## 建议了解顺序

```
第1步：看懂一张表 → DevBaseDevice（设备表）→ 知道"设备"长什么样
第2步：看懂一条链路 → 设备列表页面 怎么从数据库到前端
第3步：看懂一个协议 → MQTT 是怎么收/发设备数据的
第4步：看懂控制流 → 前端点"开灯"，命令怎么到达设备的
```

---

*生成日期：2026-05-23 | 更新日期：2026-06-06*
