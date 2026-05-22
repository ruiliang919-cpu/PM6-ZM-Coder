package com.ruoyi.web.websocket;

import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.domain.vo.DevFaultRecordVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DeviceStatusPushService 单元测试
 *
 * 测试场景:
 * - 单设备状态推送
 * - 全部设备状态广播
 * - 设备告警推送（VO版本和简化版）
 * - 功率数据推送
 * - 异常处理（推送失败不抛异常）
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeviceStatusPushService - WebSocket设备状态推送测试")
class DeviceStatusPushServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private DeviceStatusPushService deviceStatusPushService;

    // ==================== 设备状态推送 ====================

    @Test
    @DisplayName("推送状态: 同时推送到单设备和全部设备Topic")
    void testPushDeviceStatus() {
        // Given
        Integer deviceNo = 1001;
        DevBaseDeviceTCPVo tcpVo = createDeviceTCPVo(1001, 1, "v1.2.3", "192.168.1.100");

        // When
        deviceStatusPushService.pushDeviceStatus(deviceNo, tcpVo);

        // Then
        verify(messagingTemplate).convertAndSend(
            eq("/topic/device/status/1001"),
            argThat((Object payload) -> {
                Map<String, Object> map = (Map<String, Object>) payload;
                return map.get("deviceNo").equals(1001) &&
                    map.get("onlineStatus").equals(1) &&
                    "v1.2.3".equals(map.get("version")) &&
                    "192.168.1.100".equals(map.get("ip")) &&
                    map.get("lastTime") != null;
            })
        );

        verify(messagingTemplate).convertAndSend(
            eq("/topic/device/status/all"),
            argThat((Object payload) -> ((Map<String, Object>) payload).get("deviceNo").equals(1001))
        );
    }

    @Test
    @DisplayName("推送状态: 离线设备状态正确推送")
    void testPushDeviceStatus_Offline() {
        // Given
        Integer deviceNo = 1002;
        DevBaseDeviceTCPVo tcpVo = createDeviceTCPVo(1002, 0, "v1.0", "192.168.1.101");

        // When
        deviceStatusPushService.pushDeviceStatus(deviceNo, tcpVo);

        // Then
        verify(messagingTemplate).convertAndSend(
            eq("/topic/device/status/1002"),
            argThat((Object payload) -> ((Map<String, Object>) payload).get("onlineStatus").equals(0))
        );
    }

    @Test
    @DisplayName("推送状态: 版本号为null时正常推送")
    void testPushDeviceStatus_NullVersion() {
        // Given
        Integer deviceNo = 1003;
        DevBaseDeviceTCPVo tcpVo = createDeviceTCPVo(1003, 1, null, "192.168.1.102");

        // When
        deviceStatusPushService.pushDeviceStatus(deviceNo, tcpVo);

        // Then
        verify(messagingTemplate).convertAndSend(
            eq("/topic/device/status/1003"),
            argThat((Object payload) -> ((Map<String, Object>) payload).get("version") == null)
        );
    }

    @Test
    @DisplayName("推送状态: 推送异常时记录错误不抛出")
    void testPushDeviceStatus_ExceptionHandling() {
        // Given
        Integer deviceNo = 1004;
        DevBaseDeviceTCPVo tcpVo = createDeviceTCPVo(1004, 1, "v1.0", "192.168.1.103");
        doThrow(new RuntimeException("WebSocket连接异常")).when(messagingTemplate).convertAndSend(anyString(), (Object) any());

        // When & Then: 不应抛异常
        deviceStatusPushService.pushDeviceStatus(deviceNo, tcpVo);
    }

    // ==================== 设备告警推送（VO版） ====================

    @Test
    @DisplayName("推送告警(VO): 构造正确payload并推送")
    void testPushDeviceAlarm_WithVo() {
        // Given
        Integer deviceNo = 2001;
        DevFaultRecordVo faultVo = createFaultRecordVo(2001, "设备2001", "过压告警", 1700000000000L);

        // When
        deviceStatusPushService.pushDeviceAlarm(deviceNo, faultVo);

        // Then
        verify(messagingTemplate).convertAndSend(
            eq("/topic/device/alarm/2001"),
            argThat((Object payload) -> {
                Map<String, Object> map = (Map<String, Object>) payload;
                return map.get("deviceNo").equals(2001) &&
                    map.get("deviceId").equals(2001) &&
                    "设备2001".equals(map.get("name")) &&
                    "过压告警".equals(map.get("message")) &&
                    map.get("stime").equals(1700000000000L) &&
                    map.get("timestamp") != null;
            })
        );
    }

    @Test
    @DisplayName("推送告警(VO): 空名称时正常推送")
    void testPushDeviceAlarm_WithVo_EmptyName() {
        // Given
        Integer deviceNo = 2002;
        DevFaultRecordVo faultVo = createFaultRecordVo(2002, null, "欠压告警", 1700000000000L);

        // When
        deviceStatusPushService.pushDeviceAlarm(deviceNo, faultVo);

        // Then
        verify(messagingTemplate).convertAndSend(
            eq("/topic/device/alarm/2002"),
            argThat((Object payload) -> ((Map<String, Object>) payload).get("name") == null)
        );
    }

    @Test
    @DisplayName("推送告警(VO): 推送异常时记录错误不抛出")
    void testPushDeviceAlarm_WithVo_Exception() {
        // Given
        Integer deviceNo = 2003;
        DevFaultRecordVo faultVo = createFaultRecordVo(2003, "设备2003", "过流告警", 1700000000000L);
        doThrow(new RuntimeException("连接断开")).when(messagingTemplate).convertAndSend(anyString(), (Object) any());

        // When & Then
        deviceStatusPushService.pushDeviceAlarm(deviceNo, faultVo);
    }

    // ==================== 设备告警推送（简化版） ====================

    @Test
    @DisplayName("推送告警(简化): 仅包含消息和时间戳")
    void testPushDeviceAlarm_WithString() {
        // Given
        Integer deviceNo = 3001;
        String alarmMessage = "设备离线";

        // When
        deviceStatusPushService.pushDeviceAlarm(deviceNo, alarmMessage);

        // Then
        verify(messagingTemplate).convertAndSend(
            eq("/topic/device/alarm/3001"),
            argThat((Object payload) -> {
                Map<String, Object> map = (Map<String, Object>) payload;
                return map.get("deviceNo").equals(3001) &&
                    "设备离线".equals(map.get("message")) &&
                    map.get("timestamp") != null;
            })
        );
    }

    @Test
    @DisplayName("推送告警(简化): 空消息时正常推送")
    void testPushDeviceAlarm_WithString_EmptyMessage() {
        // Given
        Integer deviceNo = 3002;
        String alarmMessage = "";

        // When
        deviceStatusPushService.pushDeviceAlarm(deviceNo, alarmMessage);

        // Then
        verify(messagingTemplate).convertAndSend(
            eq("/topic/device/alarm/3002"),
            argThat((Object payload) -> "".equals(((Map<String, Object>) payload).get("message")))
        );
    }

    @Test
    @DisplayName("推送告警(简化): 推送异常时记录错误不抛出")
    void testPushDeviceAlarm_WithString_Exception() {
        // Given
        Integer deviceNo = 3003;
        doThrow(new RuntimeException("发送失败")).when(messagingTemplate).convertAndSend(anyString(), (Object) any());

        // When & Then
        deviceStatusPushService.pushDeviceAlarm(deviceNo, "告警消息");
    }

    // ==================== 功率数据推送 ====================

    @Test
    @DisplayName("推送功率: 直接转发数据对象")
    void testPushPowerData() {
        // Given
        Map<String, Object> powerData = new HashMap<>();
        powerData.put("deviceNo", 4001);
        powerData.put("power", 1250.5);
        powerData.put("voltage", 220.0);

        // When
        deviceStatusPushService.pushPowerData(powerData);

        // Then
        verify(messagingTemplate).convertAndSend(eq("/topic/device/power"), eq(powerData));
    }

    @Test
    @DisplayName("推送功率: null数据正常推送")
    void testPushPowerData_Null() {
        // When
        deviceStatusPushService.pushPowerData(null);

        // Then
        verify(messagingTemplate).convertAndSend(eq("/topic/device/power"), (Object) eq(null));
    }

    @Test
    @DisplayName("推送功率: 推送异常时记录错误不抛出")
    void testPushPowerData_Exception() {
        // Given
        doThrow(new RuntimeException("WebSocket异常")).when(messagingTemplate).convertAndSend(anyString(), (Object) any());

        // When & Then
        Map<String, Object> testData = new HashMap<>();
        testData.put("test", 1);
        deviceStatusPushService.pushPowerData(testData);
    }

    // ==================== 并发与边界测试 ====================

    @Test
    @DisplayName("边界: deviceNo为0时正常推送")
    void testBoundary_DeviceNoZero() {
        // Given
        DevBaseDeviceTCPVo tcpVo = createDeviceTCPVo(0, 1, "v1.0", "192.168.1.1");

        // When
        deviceStatusPushService.pushDeviceStatus(0, tcpVo);

        // Then
        verify(messagingTemplate).convertAndSend(eq("/topic/device/status/0"), (Object) any());
    }

    @Test
    @DisplayName("边界: 大数据对象正常推送")
    void testBoundary_LargePayload() {
        // Given
        StringBuilder largeMessage = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeMessage.append("告警信息内容");
        }
        DevFaultRecordVo faultVo = createFaultRecordVo(5001, "设备5001", largeMessage.toString(), System.currentTimeMillis());

        // When
        deviceStatusPushService.pushDeviceAlarm(5001, faultVo);

        // Then
        verify(messagingTemplate).convertAndSend(eq("/topic/device/alarm/5001"), (Object) any());
    }

    // ==================== 辅助方法 ====================

    private DevBaseDeviceTCPVo createDeviceTCPVo(long id, int onlineStatus, String version, String ip) {
        DevBaseDeviceTCPVo vo = new DevBaseDeviceTCPVo();
        vo.setId(id);
        vo.setOnlineStatus(onlineStatus);
        vo.setVersion(version);
        vo.setIp(ip);
        vo.setLastTime(new Date());
        return vo;
    }

    private DevFaultRecordVo createFaultRecordVo(int deviceId, String name, String message, long stime) {
        DevFaultRecordVo vo = new DevFaultRecordVo();
        vo.setDeviceId(deviceId);
        vo.setName(name);
        vo.setMessage(message);
        vo.setStime(stime);
        return vo;
    }
}
