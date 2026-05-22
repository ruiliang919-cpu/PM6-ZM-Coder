package com.ruoyi.mqtt;

import com.ruoyi.zm.constants.DeviceConstants;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;

/**
 * DeviceOnlineCheckTask 单元测试
 *
 * 测试场景:
 * - 设备在线状态检测
 * - 超时离线判定
 * - 竞态条件处理
 * - 异常隔离
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeviceOnlineCheckTask - 设备在线状态检测测试")
class DeviceOnlineCheckTaskTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private DeviceOnlineCheckTask deviceOnlineCheckTask;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    // ==================== 辅助方法 ====================

    private DevBaseDeviceTCPVo createOnlineDevice(long deviceId, Date lastTime) {
        DevBaseDeviceTCPVo vo = new DevBaseDeviceTCPVo();
        vo.setId(deviceId);
        vo.setIp("192.168.1." + deviceId);
        vo.setOnlineStatus(DeviceConstants.ONLINE_STATUS_ONLINE);
        vo.setLastTime(lastTime);
        return vo;
    }

    private DevBaseDeviceTCPVo createOfflineDevice(long deviceId) {
        DevBaseDeviceTCPVo vo = new DevBaseDeviceTCPVo();
        vo.setId(deviceId);
        vo.setIp("192.168.1." + deviceId);
        vo.setOnlineStatus(DeviceConstants.ONLINE_STATUS_OFFLINE);
        vo.setLastTime(new Date(System.currentTimeMillis() - 120000));
        return vo;
    }

    @SuppressWarnings("unchecked")
    private void mockScanKeys(Set<String> keys) {
        doAnswer(invocation -> {
            RedisCallback<Set<String>> callback = invocation.getArgument(0);
            // 模拟 SCAN 命令：返回预先构建的 keys 集合，忽略传入的回调连接参数
            return keys;
        }).when(redisTemplate).execute(any(RedisCallback.class));
    }

    // ==================== 基础场景 ====================

    @Test
    @DisplayName("检测: 无设备缓存时直接返回不操作")
    void testCheck_NoDevices() {
        // Given
        mockScanKeys(Collections.emptySet());

        // When
        deviceOnlineCheckTask.checkDeviceOnlineStatus();

        // Then
        verify(valueOperations, never()).set(anyString(), any(), anyLong(), any());
    }

    @Test
    @DisplayName("检测: 心跳正常的设备保持在线状态")
    void testCheck_DeviceStillOnline() {
        // Given
        String key = DeviceConstants.CACHE_TCP_PREFIX + "1";
        Set<String> keys = new HashSet<>();
        keys.add(key);

        DevBaseDeviceTCPVo device = createOnlineDevice(1, new Date());

        mockScanKeys(keys);

        when(valueOperations.get(key)).thenReturn(device);

        // When
        deviceOnlineCheckTask.checkDeviceOnlineStatus();

        // Then: 设备仍在线，不应被标记为离线
        verify(valueOperations, never()).set(eq(key), any(DevBaseDeviceTCPVo.class), anyLong(), any());
    }

    @Test
    @DisplayName("检测: 超时未心跳的设备标记为离线")
    void testCheck_DeviceOffline() {
        // Given
        String key = DeviceConstants.CACHE_TCP_PREFIX + "2";
        Set<String> keys = new HashSet<>();
        keys.add(key);

        DevBaseDeviceTCPVo device = createOnlineDevice(2, new Date(System.currentTimeMillis() - 120000));

        mockScanKeys(keys);

        when(valueOperations.get(key)).thenReturn(device);

        // When
        deviceOnlineCheckTask.checkDeviceOnlineStatus();

        // Then: 设备应被标记为离线
        verify(valueOperations).set(argThat(k -> k.equals(key)),
            argThat((DevBaseDeviceTCPVo vo) -> vo.getOnlineStatus() == DeviceConstants.ONLINE_STATUS_OFFLINE),
            anyLong(), any());
    }

    @Test
    @DisplayName("检测: 已离线的设备不重复处理")
    void testCheck_AlreadyOffline() {
        // Given
        String key = DeviceConstants.CACHE_TCP_PREFIX + "3";
        Set<String> keys = new HashSet<>();
        keys.add(key);

        DevBaseDeviceTCPVo device = createOfflineDevice(3);

        mockScanKeys(keys);

        when(valueOperations.get(key)).thenReturn(device);

        // When
        deviceOnlineCheckTask.checkDeviceOnlineStatus();

        // Then: 不应再写入
        verify(valueOperations, never()).set(eq(key), any(DevBaseDeviceTCPVo.class), anyLong(), any());
    }

    // ==================== 边界与异常场景 ====================

    @Test
    @DisplayName("检测: lastTime为空时标记离线")
    void testCheck_NullLastTime() {
        // Given
        String key = DeviceConstants.CACHE_TCP_PREFIX + "4";
        Set<String> keys = new HashSet<>();
        keys.add(key);

        DevBaseDeviceTCPVo device = createOnlineDevice(4, null);

        mockScanKeys(keys);

        when(valueOperations.get(key)).thenReturn(device);

        // When
        deviceOnlineCheckTask.checkDeviceOnlineStatus();

        // Then
        verify(valueOperations).set(argThat(k -> k.equals(key)),
            argThat((DevBaseDeviceTCPVo vo) -> vo.getOnlineStatus() == DeviceConstants.ONLINE_STATUS_OFFLINE),
            anyLong(), any());
    }

    @Test
    @DisplayName("检测: 非DevBaseDeviceTCPVo类型值被跳过")
    void testCheck_InvalidValueType() {
        // Given
        String key = DeviceConstants.CACHE_TCP_PREFIX + "5";
        Set<String> keys = new HashSet<>();
        keys.add(key);

        mockScanKeys(keys);

        when(valueOperations.get(key)).thenReturn("invalid data");

        // When
        deviceOnlineCheckTask.checkDeviceOnlineStatus();

        // Then: 不应写入
        verify(valueOperations, never()).set(eq(key), any(DevBaseDeviceTCPVo.class), anyLong(), any());
    }

    @Test
    @DisplayName("检测: 单个设备异常不影响其他设备处理")
    void testCheck_ExceptionIsolation() {
        // Given
        String key1 = DeviceConstants.CACHE_TCP_PREFIX + "6";
        String key2 = DeviceConstants.CACHE_TCP_PREFIX + "7";
        Set<String> keys = new HashSet<>();
        keys.add(key1);
        keys.add(key2);

        DevBaseDeviceTCPVo device1 = createOnlineDevice(6, new Date(System.currentTimeMillis() - 120000));
        DevBaseDeviceTCPVo device2 = createOnlineDevice(7, new Date());

        mockScanKeys(keys);

        when(valueOperations.get(key1)).thenThrow(new RuntimeException("Redis异常"));
        when(valueOperations.get(key2)).thenReturn(device2);

        // When
        deviceOnlineCheckTask.checkDeviceOnlineStatus();

        // Then: device2 不应被影响
        verify(valueOperations, never()).set(eq(key2), any(DevBaseDeviceTCPVo.class), anyLong(), any());
    }

    @Test
    @DisplayName("检测: 在线状态为null时视为离线")
    void testCheck_NullOnlineStatus() {
        // Given
        String key = DeviceConstants.CACHE_TCP_PREFIX + "8";
        Set<String> keys = new HashSet<>();
        keys.add(key);

        DevBaseDeviceTCPVo device = new DevBaseDeviceTCPVo();
        device.setId(8L);
        device.setOnlineStatus(null);
        device.setLastTime(new Date());

        mockScanKeys(keys);

        when(valueOperations.get(key)).thenReturn(device);

        // When
        deviceOnlineCheckTask.checkDeviceOnlineStatus();

        // Then: null onlineStatus 不应触发写入（因为不是 ONLINE）
        verify(valueOperations, never()).set(eq(key), any(DevBaseDeviceTCPVo.class), anyLong(), any());
    }

    // ==================== 竞态条件测试 ====================

    @Test
    @DisplayName("检测: 竞态条件二次确认 - 最新状态在线则跳过")
    void testCheck_RaceCondition_StillOnline() {
        // Given
        String key = DeviceConstants.CACHE_TCP_PREFIX + "9";
        Set<String> keys = new HashSet<>();
        keys.add(key);

        DevBaseDeviceTCPVo oldDevice = createOnlineDevice(9, new Date(System.currentTimeMillis() - 120000));
        DevBaseDeviceTCPVo latestDevice = createOnlineDevice(9, new Date()); // 最新状态在线

        mockScanKeys(keys);

        when(valueOperations.get(key))
            .thenReturn(oldDevice)
            .thenReturn(latestDevice);

        // When
        deviceOnlineCheckTask.checkDeviceOnlineStatus();

        // Then: 二次确认后不应标记离线
        verify(valueOperations, never()).set(eq(key), any(DevBaseDeviceTCPVo.class), anyLong(), any());
    }

    @Test
    @DisplayName("检测: 竞态条件二次确认 - 最新状态仍超时则标记离线")
    void testCheck_RaceCondition_ConfirmedOffline() {
        // Given
        String key = DeviceConstants.CACHE_TCP_PREFIX + "10";
        Set<String> keys = new HashSet<>();
        keys.add(key);

        DevBaseDeviceTCPVo oldDevice = createOnlineDevice(10, new Date(System.currentTimeMillis() - 120000));
        DevBaseDeviceTCPVo latestDevice = createOnlineDevice(10, new Date(System.currentTimeMillis() - 120000));

        mockScanKeys(keys);

        when(valueOperations.get(key))
            .thenReturn(oldDevice)
            .thenReturn(latestDevice);

        // When
        deviceOnlineCheckTask.checkDeviceOnlineStatus();

        // Then: 确认离线
        verify(valueOperations).set(argThat(k -> k.equals(key)),
            argThat((DevBaseDeviceTCPVo vo) -> vo.getOnlineStatus() == DeviceConstants.ONLINE_STATUS_OFFLINE),
            anyLong(), any());
    }

    @Test
    @DisplayName("检测: 竞态条件二次确认 - latestTime为null时应标记离线")
    void testCheck_RaceCondition_NullLatestTime() {
        // Given
        String key = DeviceConstants.CACHE_TCP_PREFIX + "11";
        Set<String> keys = new HashSet<>();
        keys.add(key);

        DevBaseDeviceTCPVo oldDevice = createOnlineDevice(11, new Date(System.currentTimeMillis() - 120000));
        DevBaseDeviceTCPVo latestDevice = createOnlineDevice(11, null); // 二次确认时 lastTime 为 null

        mockScanKeys(keys);

        when(valueOperations.get(key))
            .thenReturn(oldDevice)
            .thenReturn(latestDevice);

        // When
        deviceOnlineCheckTask.checkDeviceOnlineStatus();

        // Then: 应标记离线（latestTime 为 null 视为异常，继续离线标记）
        verify(valueOperations).set(argThat(k -> k.equals(key)),
            argThat((DevBaseDeviceTCPVo vo) -> vo.getOnlineStatus() == DeviceConstants.ONLINE_STATUS_OFFLINE),
            anyLong(), any());
    }
}
