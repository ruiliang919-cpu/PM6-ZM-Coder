package com.ruoyi.cache;

import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqttwrite.ill.IllOutValue;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.domain.DevBaseDevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 获取照度传感器设备信息工具缓存
@Slf4j
@Component
@RequiredArgsConstructor
public class IllUtilCache {
    private final DListUtil dListUtil;
    private final static Map<Integer, Integer> m = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;
    private final MqttPublisher mqttPublisher;

    private final Map<Long, ExecutorService> deviceExecutors = new ConcurrentHashMap<>();

    public void submitDeviceTask(Long deviceNo, Runnable task) {
        deviceExecutors.computeIfAbsent(deviceNo, k -> Executors.newSingleThreadExecutor()).submit(task);
    }

    // 外控地址对应IP地址的最后一段，例如某机柜IP 192.168.1.190，那么对应的外控地址就是190
    public Integer getDeviceId(Integer address) {
        Integer cachedId = m.get(address);
        if (cachedId != null) return cachedId;
        List<DevBaseDevice> devices = dListUtil.List();
        Map<Integer, Integer> newMappings = new HashMap<>();
        for (DevBaseDevice device : devices) {
            try {
                String[] ipParts = device.getIp().split("\\.");
                if (ipParts.length == 4) {
                    int ipLastSegment = Integer.parseInt(ipParts[3]);
                    newMappings.put(ipLastSegment, Math.toIntExact(device.getDeviceNo()));
                }
            } catch (NumberFormatException e) {
                //                log.warn("Invalid IP address format for device: {}", device.getIp());
            }
        }
        m.clear();
        m.putAll(newMappings);
        return m.getOrDefault(address, -1);
    }

    @Value("${checkOutValue:false}")
    private boolean checkOutValueMQFlag;

    @Scheduled(fixedDelay = 50)
    public void checkOutValueMQ() {
        if (checkOutValueMQFlag) {
            List<Long> nos = dListUtil.Nos();
            for (Long no : nos) {
                submitDeviceTask(no, () -> {
                    String key = "zm:power:ill:" + no + ":";
                    for (int i = 1; i <= 5; i++) {
                        try {
                            Boolean outControlStatus = (Boolean) redisTemplate.opsForHash().get(key + i, "outControlStatus");
                            if (Boolean.TRUE.equals(outControlStatus)) {
                                Map<String, Integer> m = (Map<String, Integer>) redisTemplate.opsForHash().get(key + i, "address");
                                Integer outAddress = getDeviceId(m.get("outAddress"));
                                Integer outChannel = m.get("outChannel");
                                Integer lux = (Integer) redisTemplate.opsForHash().get("zm:power:ill:" + outAddress + ":" + outChannel, "lux");
                                if (lux != null) {
                                    mqttPublisher.publish(Math.toIntExact(no), PublishKey.照度外控值, IllOutValue.build(i, lux));
                                    Thread.sleep(2000);
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
        }
    }
}
