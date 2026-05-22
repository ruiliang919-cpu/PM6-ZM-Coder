package com.ruoyi.web.websocket;

import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.domain.vo.DevFaultRecordVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备状态 WebSocket 推送服务
 * <p>
 * 推送目标：
 * - /topic/device/status/{deviceNo} — 单设备状态
 * - /topic/device/status/all        — 全部设备状态（每个设备变更时广播）
 * - /topic/device/alarm/{deviceNo}  — 设备告警
 * - /topic/device/power             — 功率数据
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceStatusPushService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 推送单设备状态变更
     *
     * @param deviceNo 设备编号
     * @param tcpVo    设备TCP缓存对象
     */
    public void pushDeviceStatus(Integer deviceNo, DevBaseDeviceTCPVo tcpVo) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("deviceNo", deviceNo);
            payload.put("onlineStatus", tcpVo.getOnlineStatus());
            payload.put("version", tcpVo.getVersion());
            payload.put("lastTime", tcpVo.getLastTime());
            payload.put("ip", tcpVo.getIp());

            // 推送到单设备订阅
            messagingTemplate.convertAndSend("/topic/device/status/" + deviceNo, payload);
            // 同时推送到全部设备订阅
            messagingTemplate.convertAndSend("/topic/device/status/all", payload);

            log.debug("WebSocket推送设备状态, deviceNo={}, onlineStatus={}", deviceNo, tcpVo.getOnlineStatus());
        } catch (Exception e) {
            log.error("WebSocket推送设备状态失败, deviceNo={}", deviceNo, e);
        }
    }

    /**
     * 推送设备告警
     *
     * @param deviceNo 设备编号
     * @param faultVo  告警信息
     */
    public void pushDeviceAlarm(Integer deviceNo, DevFaultRecordVo faultVo) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("deviceNo", deviceNo);
            payload.put("deviceId", faultVo.getDeviceId());
            payload.put("name", faultVo.getName());
            payload.put("message", faultVo.getMessage());
            payload.put("stime", faultVo.getStime());
            payload.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend("/topic/device/alarm/" + deviceNo, payload);

            log.debug("WebSocket推送设备告警, deviceNo={}, message={}", deviceNo, faultVo.getMessage());
        } catch (Exception e) {
            log.error("WebSocket推送设备告警失败, deviceNo={}", deviceNo, e);
        }
    }

    /**
     * 推送功率数据
     *
     * @param powerData 功率数据（任意结构）
     */
    public void pushPowerData(Object powerData) {
        try {
            messagingTemplate.convertAndSend("/topic/device/power", powerData);

            log.debug("WebSocket推送功率数据");
        } catch (Exception e) {
            log.error("WebSocket推送功率数据失败", e);
        }
    }

    /**
     * 推送设备离线告警（简化版）
     *
     * @param deviceNo      设备编号
     * @param alarmMessage  告警信息
     */
    public void pushDeviceAlarm(Integer deviceNo, String alarmMessage) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("deviceNo", deviceNo);
            payload.put("message", alarmMessage);
            payload.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend("/topic/device/alarm/" + deviceNo, payload);

            log.debug("WebSocket推送设备告警, deviceNo={}, message={}", deviceNo, alarmMessage);
        } catch (Exception e) {
            log.error("WebSocket推送设备告警失败, deviceNo={}", deviceNo, e);
        }
    }
}
