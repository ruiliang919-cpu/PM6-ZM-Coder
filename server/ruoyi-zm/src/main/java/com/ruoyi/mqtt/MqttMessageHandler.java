package com.ruoyi.mqtt;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt.vo.Heart;
import com.ruoyi.mqtt.vo.MqBody;
import com.ruoyi.mqtt.vo.TelecommandBody;
import com.ruoyi.mqtt03.MainChannel;
import com.ruoyi.schedule.TelecommandSendSchedule;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.web.websocket.DeviceStatusPushService;
import com.ruoyi.zm.config.ModbusTCPManager;
import com.ruoyi.zm.constants.DeviceConstants;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevFaultRecord;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.domain.vo.DevFaultRecordVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevFaultRecordMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttMessageHandler {
    private final MainChannel mainChannel;
    private final DListUtil dListUtil;
    private final ModbusTCPManager tcpManager;
    private final DevFaultRecordMapper faultRecordMapper;
    private final DevBaseDeviceMapper devBaseDeviceMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TelecommandSendSchedule telecommandSendSchedule;
    private final RedisTemplate<String, boolean[]> boolArrayRedisTemplate;
    private final Key key;
    private final DeviceStatusPushService deviceStatusPushService;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMqttMessage(Message<?> message) {
        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
        // log.info("{}-{}", topic, message.getPayload().toString());
        if (topic == null || topic.isEmpty()) return;
        String[] parts = topic.split("/");

        Long deviceNo = dListUtil.ipMapX().getOrDefault(parts[2], -1L);
        if (deviceNo == -1) {
            log.warn("主题：{} 判断设备号 -1", topic);
            return;
        }

        MqBody event = new MqBody();
        event.setDeviceNo(Math.toIntExact(deviceNo));
        event.setBody(message.getPayload().toString());
        event.setEnd(parts[parts.length - 1]);
        handleEvent(event);
    }

    private void handleEvent(MqBody event) {
        switch (event.getEnd()) {
            case "holding":
                mainChannel.handleHoldingRegister(event.getDeviceNo(), event.getBody());
                break;
            case "coil":
                handleCoilStatus(event.getDeviceNo(), event.getBody());
                break;
            case "heart":
                handleHeartbeat(event.getDeviceNo(), event.getBody());
                break;
            case "connect":
                yz(event.getDeviceNo(), event.getBody());
                break;
            case "request":
                mainChannel.handleRequest(event.getDeviceNo(), event.getBody());
                break;
        }
    }

    /**
     * 处理设备心跳消息
     * <p>
     * 策略：先更新内存对象状态，成功后写入Redis缓存，异常则不更新缓存，保证状态一致性。
     * 每次心跳都刷新 lastTime，确保离线检测定时任务可正常判断设备活性。
     * </p>
     *
     * @param deviceNo 设备编号
     * @param payload  心跳消息体
     */
    private void handleHeartbeat(Integer deviceNo, String payload) {
        try {
            // 1. 解析心跳数据
            Heart heart = JSONUtil.toBean(payload, Heart.class);
            if (heart == null || heart.getData() == null) {
                log.warn("心跳处理: 心跳数据解析为空, deviceNo={}", deviceNo);
                return;
            }
            Heart.Data data = heart.getData();
            Boolean online = data.getOnline();
            String version = data.getVersion();

            // 2. 从缓存获取设备信息
            DevBaseDeviceTCPVo tcp = tcpManager.getOneByCache(deviceNo);
            if (tcp == null) {
                log.warn("心跳处理: 设备缓存不存在, deviceNo={}", deviceNo);
                return;
            }

            // 3. 判断设备状态变化
            boolean wasOffline = tcp.getOnlineStatus() == null
                || tcp.getOnlineStatus() == DeviceConstants.ONLINE_STATUS_OFFLINE;
            boolean shouldGoOnline = Boolean.TRUE.equals(online);
            boolean needFullUpdate = wasOffline && shouldGoOnline;
            boolean needVersionUpdate = (tcp.getVersion() == null || tcp.getVersion().isEmpty())
                && version != null && !version.isEmpty();
            boolean keyExists = Boolean.TRUE.equals(
                redisTemplate.hasKey(DeviceConstants.CACHE_TCP_PREFIX + deviceNo));

            // 4. 根据状态变化更新缓存
            if (!keyExists || needFullUpdate) {
                // 设备首次写入缓存 或 从离线恢复上线：完整更新
                tcp.setVersion(version);
                tcp.setOnlineStatus(DeviceConstants.ONLINE_STATUS_ONLINE);
                tcp.setLastTime(new Date());
                redisTemplate.opsForValue().set(
                    DeviceConstants.CACHE_TCP_PREFIX + deviceNo, tcp, 2, TimeUnit.DAYS);
                redisTemplate.delete("zm:order:" + deviceNo + ":1:0x0000");
                redisTemplate.delete(DeviceConstants.CACHE_FAULT_PREFIX + deviceNo + ":0xAAAA");
                log.info("设备上线, deviceNo={}, version={}", deviceNo, version);
            } else if (needVersionUpdate) {
                // 设备已在线但版本号缺失：仅更新版本和心跳时间
                tcp.setVersion(version);
                tcp.setLastTime(new Date());
                redisTemplate.opsForValue().set(
                    DeviceConstants.CACHE_TCP_PREFIX + deviceNo, tcp, 2, TimeUnit.DAYS);
                log.debug("设备版本更新, deviceNo={}, version={}", deviceNo, version);
            } else {
                // 设备已在线且版本已知：仅刷新最后心跳时间（关键：保证离线检测可工作）
                tcp.setLastTime(new Date());
                redisTemplate.opsForValue().set(
                    DeviceConstants.CACHE_TCP_PREFIX + deviceNo, tcp, 2, TimeUnit.DAYS);
            }

            log.debug("设备心跳更新成功, deviceNo={}", deviceNo);

            // WebSocket 推送设备状态变更
            try {
                DevBaseDeviceTCPVo updatedTcp = tcpManager.getOneByCache(deviceNo);
                if (updatedTcp != null) {
                    deviceStatusPushService.pushDeviceStatus(deviceNo, updatedTcp);
                }
            } catch (Exception pushEx) {
                log.warn("WebSocket推送心跳状态失败, deviceNo={}", deviceNo, pushEx);
            }
        } catch (Exception e) {
            log.error("心跳处理失败, deviceNo={}", deviceNo, e);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static
    class Yz {
        private Boolean connected;
    }

    private void yz(Integer deviceNo, String payload) {
        try {
            Yz yzBean = JSONUtil.toBean(payload, Yz.class);
            if (yzBean == null) {
                log.warn("yz处理: JSON解析结果为空, deviceNo={}", deviceNo);
                return;
            }
            Boolean connected = yzBean.getConnected();
            if (connected == null || connected) return;
            DevBaseDeviceTCPVo tcp = tcpManager.getOneByCache(deviceNo);
            if (tcp == null) {
                log.warn("yz处理: 设备缓存不存在, deviceNo={}", deviceNo);
                return;
            }
            String ip = tcp.getIp();
        boolean[] initData = new boolean[845];
        boolean[] sourceData = boolArrayRedisTemplate.opsForValue().get("zm:queue:zm:cache:1:" + ip + ":" + deviceNo + ":0x0000");
        if (sourceData != null) {
            sourceData[sourceData.length - 1] = !sourceData[sourceData.length - 1];
            initData = sourceData;
        }
        boolArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:1:" + ip + ":" + deviceNo + ":0x0000", initData);
        redisTemplate.delete("zm:order:" + deviceNo + ":1:0x0000");
        if (tcp.getOnlineStatus() == 1) {
            tcp.setOnlineStatus(0); // 断开
            redisTemplate.opsForValue().set("zm:create-tcp:" + deviceNo, tcp, 1, TimeUnit.DAYS);
            // 清除断线设备的告警信息
            Set<String> keys = scanKeys("zm:fault:" + deviceNo + ":*");
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            boolArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:1:" + ip + ":" + deviceNo + ":0x0000", initData);
            DevFaultRecordVo vo = new DevFaultRecordVo();
            vo.setDeviceId(Math.toIntExact(deviceNo));
            DevBaseDevice deviceInfo = devBaseDeviceMapper.selectOne(new LambdaQueryWrapper<DevBaseDevice>()
                .select(DevBaseDevice::getDeviceName)
                .eq(DevBaseDevice::getDeviceNo, deviceNo));
            vo.setName(deviceInfo != null ? deviceInfo.getDeviceName() : "未知设备");
            vo.setMessage("设备离线");
            vo.setStime(System.currentTimeMillis());
            redisTemplate.opsForValue().set("zm:fault:" + deviceNo + ":0xAAAA", vo);
            DevFaultRecord recordFault = new DevFaultRecord();
            recordFault.setDeviceId(Math.toIntExact(deviceNo));
            recordFault.setMessage(vo.getMessage());
            recordFault.setStime(vo.getStime() / 1000);
            recordFault.setShowType(1);
            recordFault.setType(1);
            faultRecordMapper.insert(recordFault);

            // WebSocket 推送设备离线告警
            try {
                deviceStatusPushService.pushDeviceStatus(deviceNo, tcp);
                deviceStatusPushService.pushDeviceAlarm(deviceNo, "设备离线");
            } catch (Exception pushEx) {
                log.warn("WebSocket推送离线告警失败, deviceNo={}", deviceNo, pushEx);
            }
        }
        } catch (Exception e) {
            log.error("yz处理失败, deviceNo={}", deviceNo, e);
        }
    }

    private void handleCoilStatus(Integer deviceNo, String payload) {
        try {
            TelecommandBody bean = JSONUtil.toBean(payload, TelecommandBody.class);
            if (bean == null) {
                log.warn("线圈处理: JSON解析结果为空, deviceNo={}", deviceNo);
                return;
            }
            DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceNo);
            if (tcpVo == null) {
                log.warn("线圈处理: 设备缓存不存在, deviceNo={}", deviceNo);
                return;
            }
            String ip = tcpVo.getIp();
            telecommandSendSchedule.U(ip, deviceNo, bean.getData());
        } catch (Exception e) {
            log.error("线圈处理失败, deviceNo={}", deviceNo, e);
        }
    }

    /**
     * 使用 SCAN 命令替代 KEYS，避免阻塞 Redis
     */
    private Set<String> scanKeys(String pattern) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();
            ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(100)
                .build();
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            }
            return keys;
        });
    }
}
