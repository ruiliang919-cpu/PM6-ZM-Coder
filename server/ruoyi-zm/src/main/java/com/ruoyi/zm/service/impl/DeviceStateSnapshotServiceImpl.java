package com.ruoyi.zm.service.impl;

import cn.hutool.json.JSONUtil;
import com.ruoyi.zm.service.DeviceStateSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceStateSnapshotServiceImpl implements DeviceStateSnapshotService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Value("${device-state-snapshot.enabled:true}")
    private boolean enabled;

    /** Key 模式快照间隔配置: 模式 -> 秒 */
    private static final String[][] SNAPSHOT_PATTERNS = {
        {"zm:create-tcp:*", "30"},
        {"zm:queue:zm:cache:63:*", "60"},
        {"zm:queue:zm:cache:3:*", "120"},
        {"zm:queue:zm:cache:1:*", "120"},
        {"zm:power:*", "180"},
        {"zm:fault:*", "180"},
        {"zm:global:*", "180"},
        {"zm:select:*", "180"},
        {"zm:instruct_key:*", "180"},
        {"zm:handle:*", "180"},
        {"zm:workModule", "180"},
    };

    private final Map<String, Long> lastSnapshotTime = new HashMap<>();

    @Override
    @Scheduled(fixedDelay = 30_000)
    public void snapshotAll() {
        if (!enabled) return;
        long now = System.currentTimeMillis();
        for (String[] entry : SNAPSHOT_PATTERNS) {
            String pattern = entry[0];
            long interval = Long.parseLong(entry[1]) * 1000L;
            Long last = lastSnapshotTime.getOrDefault(pattern, 0L);
            if (now - last >= interval) {
                try {
                    snapshotPattern(pattern);
                    lastSnapshotTime.put(pattern, now);
                } catch (Exception e) {
                    log.error("Snapshot failed for pattern: {}", pattern, e);
                }
            }
        }
    }

    @Override
    public void snapshotPattern(String keyPattern) {
        ScanOptions options = ScanOptions.scanOptions().match(keyPattern).count(100).build();
        int saved = 0;
        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection()
                .scan(options)) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next(), StandardCharsets.UTF_8);
                try {
                    saveKeyToDb(key);
                    saved++;
                } catch (Exception e) {
                    log.warn("Failed to snapshot key: {}", key, e);
                }
            }
        }
        if (saved > 0) log.debug("Snapshot '{}': {} keys saved", keyPattern, saved);
    }

    private void saveKeyToDb(String key) {
        String type = Objects.requireNonNull(redisTemplate.type(key)).code();
        String value;
        try {
            if ("hash".equals(type)) {
                Map<Object, Object> hash = redisTemplate.opsForHash().entries(key);
                value = JSONUtil.toJsonStr(hash);
            } else if ("list".equals(type)) {
                List<Object> list = redisTemplate.opsForList().range(key, 0, -1);
                value = JSONUtil.toJsonStr(list != null ? list : Collections.emptyList());
            } else {
                Object val = redisTemplate.opsForValue().get(key);
                if (val instanceof byte[]) {
                    value = Base64.getEncoder().encodeToString((byte[]) val);
                } else if (val instanceof int[]) {
                    value = JSONUtil.toJsonStr(val);
                } else if (val instanceof short[]) {
                    value = JSONUtil.toJsonStr(val);
                } else if (val instanceof boolean[]) {
                    List<Boolean> bools = new ArrayList<>();
                    for (boolean b : (boolean[]) val) bools.add(b);
                    value = JSONUtil.toJsonStr(bools);
                } else {
                    value = val != null ? JSONUtil.toJsonStr(val) : null;
                }
            }
            if (value == null) return;
            jdbcTemplate.update(
                "INSERT INTO device_state_snapshot (state_key, state_value, state_type) VALUES (?,?,?) " +
                "ON DUPLICATE KEY UPDATE state_value = VALUES(state_value), state_type = VALUES(state_type)",
                key, value, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize key: " + key, e);
        }
    }

    @Override
    public void restoreAllIfNeeded() {
        if (!enabled) return;
        // 使用SCAN替代KEYS，避免阻塞Redis
        Long keyCount = countKeysByScan("zm:create-tcp:*");
        if (keyCount != null && keyCount > 10) {
            log.info("Redis has {} create-tcp keys, skip restore", keyCount);
            return;
        }
        log.warn("Redis appears empty ({} create-tcp keys), restoring from DB...", keyCount != null ? keyCount : 0);
        for (String[] entry : SNAPSHOT_PATTERNS) {
            try {
                restorePattern(entry[0]);
            } catch (Exception e) {
                log.error("Restore failed for pattern: {}", entry[0], e);
            }
        }
        log.info("State restore completed");
    }

    @Override
    public void restorePattern(String keyPattern) {
        String like = keyPattern.replace("*", "%");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            "SELECT state_key, state_value, state_type FROM device_state_snapshot WHERE state_key LIKE ? ORDER BY updated_at DESC LIMIT 10000",
            like);
        int restored = 0;
        for (Map<String, Object> row : rows) {
            try {
                String key = (String) row.get("state_key");
                String value = (String) row.get("state_value");
                String type = (String) row.get("state_type");
                restoreKeyToRedis(key, value, type);
                restored++;
            } catch (Exception e) {
                log.warn("Failed to restore row: {}", row.get("state_key"), e);
            }
        }
        if (restored > 0) log.info("Restored '{}': {} keys", keyPattern, restored);
    }

    /**
     * 使用SCAN命令统计匹配模式的key数量，避免KEYS命令阻塞Redis
     */
    private Long countKeysByScan(String pattern) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> {
            long count = 0;
            ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(100)
                .build();
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    cursor.next();
                    count++;
                }
            }
            return count;
        });
    }

    @SuppressWarnings("unchecked")
    private void restoreKeyToRedis(String key, String value, String type) {
        Object parsed;
        try {
            if (value.startsWith("[") || value.startsWith("{")) {
                parsed = JSONUtil.parse(value);
            } else {
                parsed = value;
            }
        } catch (Exception e) {
            parsed = value;
        }
        switch (type) {
            case "string":
                redisTemplate.opsForValue().set(key, parsed);
                break;
            case "hash":
                if (parsed instanceof Map) {
                    redisTemplate.opsForHash().putAll(key, (Map<String, Object>) parsed);
                }
                break;
            case "list":
                if (parsed instanceof Iterable) {
                    for (Object item : (Iterable<Object>) parsed) {
                        redisTemplate.opsForList().rightPush(key, item);
                    }
                }
                break;
            default:
                redisTemplate.opsForValue().set(key, parsed);
        }
    }
}
