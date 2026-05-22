package com.ruoyi.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModuleGuard {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Check if device is in remote-control ("on-the-line") mode.
     * When true, write commands should be blocked.
     */
    public boolean isInRemoteMode() {
        String module = (String) redisTemplate.opsForValue().get("zm:global:module:select");
        return "on-the-line".equals(module);
    }
}
