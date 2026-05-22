package com.ruoyi.mqtt;

import com.ruoyi.zm.constants.DeviceConstants;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 设备在线状态检测定时任务
 * <p>
 * 定期扫描 Redis 中设备缓存（zm:create-tcp:*），检查 lastTime 是否超过离线阈值，
 * 超时未响应的设备标记为离线（onlineStatus=0）并更新缓存。
 * </p>
 * <p>
 * 使用 SCAN 遍历 Redis Key，避免 KEYS 命令阻塞 Redis。
 * </p>
 *
 * @author system
 */
@Component
public class DeviceOnlineCheckTask {

    private static final Logger log = LoggerFactory.getLogger(DeviceOnlineCheckTask.class);

    /** 设备离线超时阈值(毫秒) - 默认60秒无心跳视为离线 */
    private static final long OFFLINE_THRESHOLD_MS = 60 * 1000L;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 每30秒执行一次设备在线状态检查
     * <p>
     * 扫描所有 zm:create-tcp:* 缓存，对 lastTime 超过 60 秒的设备执行离线标记。
     * 仅当设备当前 onlineStatus=1（在线）时才执行状态变更，避免重复写入。
     * </p>
     */
    @Scheduled(fixedRate = 30000)
    public void checkDeviceOnlineStatus() {
        try {
            Set<String> deviceKeys = scanKeys(DeviceConstants.CACHE_TCP_PREFIX + "*");
            if (deviceKeys.isEmpty()) {
                return;
            }

            long now = System.currentTimeMillis();
            int offlineCount = 0;

            for (String key : deviceKeys) {
                try {
                    Object value = redisTemplate.opsForValue().get(key);
                    if (!(value instanceof DevBaseDeviceTCPVo)) {
                        continue;
                    }

                    DevBaseDeviceTCPVo tcp = (DevBaseDeviceTCPVo) value;

                    // 仅处理当前在线的设备
                    if (tcp.getOnlineStatus() == null
                        || tcp.getOnlineStatus() != DeviceConstants.ONLINE_STATUS_ONLINE) {
                        continue;
                    }

                    // 检查 lastTime 是否超过离线阈值
                    Date lastTime = tcp.getLastTime();
                    if (lastTime == null) {
                        // lastTime 为空，视为异常，标记离线
                        markDeviceOffline(key, tcp);
                        offlineCount++;
                        continue;
                    }

                    long elapsed = now - lastTime.getTime();
                    if (elapsed > OFFLINE_THRESHOLD_MS) {
                        // 二次确认：重新从 Redis 读取最新数据，防止竞态条件
                        Object latestValue = redisTemplate.opsForValue().get(key);
                        if (latestValue instanceof DevBaseDeviceTCPVo) {
                            DevBaseDeviceTCPVo latest = (DevBaseDeviceTCPVo) latestValue;
                            Date latestTime = latest.getLastTime();
                            if (latestTime != null) {
                                long latestElapsed = now - latestTime.getTime();
                                if (latestElapsed <= OFFLINE_THRESHOLD_MS) {
                                    // 最新状态显示设备仍在线，跳过
                                    continue;
                                }
                            } else {
                                // 二次确认时 latestTime 仍为 null，视为异常但继续标记离线
                                log.warn("设备二次确认时lastTime仍为null, deviceId={}, 将标记离线", tcp.getId());
                            }
                        }
                        // 确认离线，执行标记
                        markDeviceOffline(key, tcp);
                        offlineCount++;
                    }
                } catch (Exception e) {
                    log.error("检查设备在线状态异常, key={}", key, e);
                }
            }

            if (offlineCount > 0) {
                log.info("设备在线状态检查完成, 扫描设备数={}, 标记离线数={}", deviceKeys.size(), offlineCount);
            }
        } catch (Exception e) {
            log.error("设备在线状态检查任务执行异常", e);
        }
    }

    /**
     * 将设备标记为离线
     * <p>
     * 更新 onlineStatus=0 并刷新 lastTime，然后写回 Redis 缓存。
     * 同时清除断线设备的故障告警缓存。
     * </p>
     *
     * @param key  Redis 缓存 Key
     * @param tcp  设备缓存对象
     */
    private void markDeviceOffline(String key, DevBaseDeviceTCPVo tcp) {
        tcp.setOnlineStatus(DeviceConstants.ONLINE_STATUS_OFFLINE);
        tcp.setLastTime(new Date());
        redisTemplate.opsForValue().set(key, tcp, 1, TimeUnit.DAYS);

        // 清除断线设备的故障告警缓存
        String faultPattern = DeviceConstants.CACHE_FAULT_PREFIX + tcp.getId() + ":*";
        try {
            Set<String> faultKeys = scanKeys(faultPattern);
            if (!faultKeys.isEmpty()) {
                redisTemplate.delete(faultKeys);
            }
        } catch (Exception e) {
            log.error("清除设备故障缓存异常, deviceId={}", tcp.getId(), e);
        }

        log.warn("设备离线, deviceId={}, ip={}", tcp.getId(), tcp.getIp());
    }

    /**
     * 使用 SCAN 命令遍历 Redis Key，避免 KEYS 阻塞
     *
     * @param pattern 匹配模式，如 "zm:create-tcp:*"
     * @return 匹配的 Key 集合
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
