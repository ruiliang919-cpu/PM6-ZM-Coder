package com.ruoyi.mqtt;

import cn.hutool.json.JSONUtil;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.MainChannel;
import com.ruoyi.mqtt.vo.Heart;
import com.ruoyi.mqtt.vo.MqBody;
import com.ruoyi.mqtt.vo.TelecommandBody;
import com.ruoyi.schedule.TelecommandSendSchedule;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.web.websocket.DeviceStatusPushService;
import com.ruoyi.zm.config.ModbusTCPManager;
import com.ruoyi.zm.constants.DeviceConstants;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevFaultRecordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MqttMessageHandler 单元测试
 *
 * 测试场景:
 * - MQTT消息路由分发
 * - 心跳处理（首次上线、保持在线、离线恢复）
 * - 设备断开处理
 * - 线圈状态处理
 * - 异常处理
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MqttMessageHandler - MQTT消息处理器测试")
class MqttMessageHandlerTest {

    @Mock
    private MainChannel mainChannel;

    @Mock
    private DListUtil dListUtil;

    @Mock
    private ModbusTCPManager tcpManager;

    @Mock
    private DevFaultRecordMapper faultRecordMapper;

    @Mock
    private DevBaseDeviceMapper devBaseDeviceMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private TelecommandSendSchedule telecommandSendSchedule;

    @Mock
    private RedisTemplate<String, boolean[]> boolArrayRedisTemplate;

    @Mock
    private Key key;

    @Mock
    private DeviceStatusPushService deviceStatusPushService;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ValueOperations<String, boolean[]> boolValueOperations;

    @InjectMocks
    private MqttMessageHandler mqttMessageHandler;

    private Map<String, Long> ipMap;

    @BeforeEach
    void setUp() {
        ipMap = new HashMap<>();
        ipMap.put("192168001001", 1L);
        when(dListUtil.ipMapX()).thenReturn(ipMap);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(boolArrayRedisTemplate.opsForValue()).thenReturn(boolValueOperations);
    }

    // ==================== 消息路由测试 ====================

    @Test
    @DisplayName("路由: holding类型消息调用MainChannel.handleHoldingRegister")
    void testHandleEvent_Holding() {
        // Given
        String topic = "/zm/192168001001/holding";
        String payload = "{\"data\":[1,2,3]}";
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verify(mainChannel).handleHoldingRegister(eq(1), eq(payload));
    }

    @Test
    @DisplayName("路由: coil类型消息调用handleCoilStatus")
    void testHandleEvent_Coil() {
        // Given
        String topic = "/zm/192168001001/coil";
        String payload = "{\"data\":[true,false]}";
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verify(telecommandSendSchedule).U(anyString(), eq(1), any());
    }

    @Test
    @DisplayName("路由: heart类型消息调用handleHeartbeat")
    void testHandleEvent_Heart() {
        // Given
        String topic = "/zm/192168001001/heart";
        Heart heart = new Heart();
        Heart.Data data = new Heart.Data();
        data.setOnline(true);
        data.setVersion("v1.0");
        heart.setData(data);
        String payload = JSONUtil.toJsonStr(heart);

        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        DevBaseDeviceTCPVo tcpVo = new DevBaseDeviceTCPVo();
        tcpVo.setOnlineStatus(DeviceConstants.ONLINE_STATUS_OFFLINE);
        when(tcpManager.getOneByCache(1)).thenReturn(tcpVo);

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verify(valueOperations).set(anyString(), any(DevBaseDeviceTCPVo.class), anyLong(), any());
    }

    @Test
    @DisplayName("路由: request类型消息调用MainChannel.handleRequest")
    void testHandleEvent_Request() {
        // Given
        String topic = "/zm/192168001001/request";
        String payload = "{\"cmd\":\"read\"}";
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verify(mainChannel).handleRequest(eq(1), eq(payload));
    }

    @Test
    @DisplayName("路由: connect类型消息调用yz处理")
    void testHandleEvent_Connect() {
        // Given
        String topic = "/zm/192168001001/connect";
        String payload = "{\"connected\":false}";
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        DevBaseDeviceTCPVo tcpVo = new DevBaseDeviceTCPVo();
        tcpVo.setOnlineStatus(DeviceConstants.ONLINE_STATUS_ONLINE);
        tcpVo.setIp("192.168.1.1");
        tcpVo.setId(1L);
        when(tcpManager.getOneByCache(1)).thenReturn(tcpVo);
        when(boolValueOperations.get(anyString())).thenReturn(null);
        when(devBaseDeviceMapper.selectOne(any())).thenReturn(null);

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verify(valueOperations, atLeastOnce()).set(anyString(), any(), anyLong(), any());
    }

    @Test
    @DisplayName("路由: 未知类型消息不处理")
    void testHandleEvent_Unknown() {
        // Given
        String topic = "/zm/192168001001/unknown";
        Message<String> message = new GenericMessage<>("test", Collections.singletonMap("mqtt_receivedTopic", topic));

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verifyNoInteractions(mainChannel);
        verifyNoInteractions(telecommandSendSchedule);
    }

    @Test
    @DisplayName("路由: 设备号映射失败时记录警告并返回")
    void testHandleMqttMessage_DeviceNotFound() {
        // Given
        ipMap.clear(); // 清空映射
        String topic = "/zm/192168001099/holding";
        Message<String> message = new GenericMessage<>("test", Collections.singletonMap("mqtt_receivedTopic", topic));

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verifyNoInteractions(mainChannel);
    }

    @Test
    @DisplayName("路由: topic为空时不处理")
    void testHandleMqttMessage_NullTopic() {
        // Given
        Message<String> message = new GenericMessage<>("test", Collections.emptyMap());

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verifyNoInteractions(mainChannel);
    }

    // ==================== 心跳处理测试 ====================

    @Test
    @DisplayName("心跳: 设备首次上线完整更新缓存")
    void testHandleHeartbeat_FirstOnline() {
        // Given
        String topic = "/zm/192168001001/heart";
        Heart heart = createHeart(true, "v1.0");
        String payload = JSONUtil.toJsonStr(heart);
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        DevBaseDeviceTCPVo tcpVo = new DevBaseDeviceTCPVo();
        tcpVo.setOnlineStatus(DeviceConstants.ONLINE_STATUS_OFFLINE);
        when(tcpManager.getOneByCache(1)).thenReturn(tcpVo);

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verify(valueOperations).set(
            argThat((String k) -> k.contains("zm:create-tcp:1")),
            argThat((DevBaseDeviceTCPVo vo) ->
                vo.getOnlineStatus() == DeviceConstants.ONLINE_STATUS_ONLINE &&
                "v1.0".equals(vo.getVersion())),
            anyLong(), any());
    }

    @Test
    @DisplayName("心跳: 设备从离线恢复上线")
    void testHandleHeartbeat_RecoverFromOffline() {
        // Given
        String topic = "/zm/192168001001/heart";
        Heart heart = createHeart(true, "v2.0");
        String payload = JSONUtil.toJsonStr(heart);
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        DevBaseDeviceTCPVo tcpVo = new DevBaseDeviceTCPVo();
        tcpVo.setOnlineStatus(DeviceConstants.ONLINE_STATUS_OFFLINE);
        tcpVo.setVersion(null);
        when(tcpManager.getOneByCache(1)).thenReturn(tcpVo);

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verify(valueOperations).set(anyString(),
            argThat((DevBaseDeviceTCPVo vo) ->
                vo.getOnlineStatus() == DeviceConstants.ONLINE_STATUS_ONLINE),
            anyLong(), any());
    }

    @Test
    @DisplayName("心跳: 设备已在线仅刷新lastTime")
    void testHandleHeartbeat_StillOnline() {
        // Given
        String topic = "/zm/192168001001/heart";
        Heart heart = createHeart(true, "v1.0");
        String payload = JSONUtil.toJsonStr(heart);
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        DevBaseDeviceTCPVo tcpVo = new DevBaseDeviceTCPVo();
        tcpVo.setOnlineStatus(DeviceConstants.ONLINE_STATUS_ONLINE);
        tcpVo.setVersion("v1.0");
        Date oldTime = new Date(System.currentTimeMillis() - 10000);
        tcpVo.setLastTime(oldTime);
        when(tcpManager.getOneByCache(1)).thenReturn(tcpVo);

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verify(valueOperations).set((String) anyString(),
            argThat((DevBaseDeviceTCPVo vo) -> vo.getLastTime() != null && vo.getLastTime().after(oldTime)),
            anyLong(), any());
    }

    @Test
    @DisplayName("心跳: 心跳数据解析为空时记录警告不抛异常")
    void testHandleHeartbeat_NullData() {
        // Given
        String topic = "/zm/192168001001/heart";
        Heart heart = new Heart();
        heart.setData(null);
        String payload = JSONUtil.toJsonStr(heart);
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then: 不应操作缓存
        verify(valueOperations, never()).set(anyString(), any(), anyLong(), any());
    }

    @Test
    @DisplayName("心跳: 设备缓存不存在时记录警告不抛异常")
    void testHandleHeartbeat_DeviceCacheMissing() {
        // Given
        String topic = "/zm/192168001001/heart";
        Heart heart = createHeart(true, "v1.0");
        String payload = JSONUtil.toJsonStr(heart);
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        when(tcpManager.getOneByCache(1)).thenReturn(null);

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verify(valueOperations, never()).set(anyString(), any(), anyLong(), any());
    }

    @Test
    @DisplayName("心跳: WebSocket推送失败不影响心跳处理")
    void testHandleHeartbeat_WebSocketPushFailure() {
        // Given
        String topic = "/zm/192168001001/heart";
        Heart heart = createHeart(true, "v1.0");
        String payload = JSONUtil.toJsonStr(heart);
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        DevBaseDeviceTCPVo tcpVo = new DevBaseDeviceTCPVo();
        tcpVo.setOnlineStatus(DeviceConstants.ONLINE_STATUS_OFFLINE);
        when(tcpManager.getOneByCache(1)).thenReturn(tcpVo);
        doThrow(new RuntimeException("WebSocket连接断开")).when(deviceStatusPushService).pushDeviceStatus(anyInt(), (DevBaseDeviceTCPVo) any());

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then: 心跳处理应完成，不因WebSocket异常中断
        verify(valueOperations).set(anyString(), any(DevBaseDeviceTCPVo.class), anyLong(), any());
    }

    @Test
    @DisplayName("心跳: 版本号从空更新为有值")
    void testHandleHeartbeat_VersionUpdate() {
        // Given
        String topic = "/zm/192168001001/heart";
        Heart heart = createHeart(true, "v3.0");
        String payload = JSONUtil.toJsonStr(heart);
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        DevBaseDeviceTCPVo tcpVo = new DevBaseDeviceTCPVo();
        tcpVo.setOnlineStatus(DeviceConstants.ONLINE_STATUS_ONLINE);
        tcpVo.setVersion(null); // 版本为空
        tcpVo.setLastTime(new Date());
        when(tcpManager.getOneByCache(1)).thenReturn(tcpVo);

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verify(valueOperations).set(anyString(),
            argThat((DevBaseDeviceTCPVo vo) -> "v3.0".equals(vo.getVersion())),
            anyLong(), any());
    }

    // ==================== 线圈状态测试 ====================

    @Test
    @DisplayName("线圈: 正常解析并调用调度")
    void testHandleCoilStatus_Success() {
        // Given
        String topic = "/zm/192168001001/coil";
        TelecommandBody body = new TelecommandBody();
        body.setData(new boolean[]{true, false, true});
        String payload = JSONUtil.toJsonStr(body);
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        DevBaseDeviceTCPVo tcpVo = new DevBaseDeviceTCPVo();
        tcpVo.setIp("192.168.1.1");
        when(key.getCreateTCP(1)).thenReturn(tcpVo);

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then
        verify(telecommandSendSchedule).U(eq("192.168.1.1"), eq(1), any());
    }

    @Test
    @DisplayName("线圈: 解析异常时记录错误不抛异常")
    void testHandleCoilStatus_ParseError() {
        // Given
        String topic = "/zm/192168001001/coil";
        String payload = "invalid json";
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then: 不应抛异常，也不应调用调度
        verifyNoInteractions(telecommandSendSchedule);
    }

    @Test
    @DisplayName("线圈: 设备缓存不存在时记录警告不抛异常")
    void testHandleCoilStatus_DeviceCacheMissing() {
        // Given
        String topic = "/zm/192168001001/coil";
        TelecommandBody body = new TelecommandBody();
        body.setData(new boolean[]{true, false, true});
        String payload = JSONUtil.toJsonStr(body);
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        when(key.getCreateTCP(1)).thenReturn(null);

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then: 不应抛异常，也不应调用调度
        verifyNoInteractions(telecommandSendSchedule);
    }

    @Test
    @DisplayName("connect: 设备缓存不存在时记录警告不抛异常")
    void testHandleConnect_DeviceCacheMissing() {
        // Given
        String topic = "/zm/192168001001/connect";
        String payload = "{\"connected\":false}";
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        when(tcpManager.getOneByCache(1)).thenReturn(null);

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then: 不应抛异常
        verify(valueOperations, never()).set(anyString(), any(), anyLong(), any());
    }

    @Test
    @DisplayName("connect: JSON解析异常时记录错误不抛异常")
    void testHandleConnect_ParseError() {
        // Given
        String topic = "/zm/192168001001/connect";
        String payload = "invalid json";
        Message<String> message = new GenericMessage<>(payload, Collections.singletonMap("mqtt_receivedTopic", topic));

        // When
        mqttMessageHandler.handleMqttMessage(message);

        // Then: 不应抛异常
        verify(valueOperations, never()).set(anyString(), any(), anyLong(), any());
    }

    // ==================== 辅助方法 ====================

    private Heart createHeart(boolean online, String version) {
        Heart heart = new Heart();
        Heart.Data data = new Heart.Data();
        data.setOnline(online);
        data.setVersion(version);
        data.setTime(System.currentTimeMillis() / 1000);
        heart.setData(data);
        return heart;
    }
}
