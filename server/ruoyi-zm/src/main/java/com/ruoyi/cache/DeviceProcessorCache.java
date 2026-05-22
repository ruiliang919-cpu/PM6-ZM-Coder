package com.ruoyi.cache;

import com.ruoyi.zm.domain.vo.SystemControlRespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceProcessorCache {
    private final Key key;
    private final RedisTemplate<String, Object> redisTemplate;

    public CompletableFuture<SystemControlRespVo> getSystemControl(Integer deviceId) {
        return CompletableFuture.supplyAsync(() -> {
            SystemControlRespVo vo = new SystemControlRespVo();
            try {
                boolean[] arr = key.getTelecommand(deviceId, 163, 164);
                vo.setSystemSwitch(arr[0] ? 1 : 0);
                vo.setWorkMode(arr[1] ? 1 : 0);
            } catch (Exception e) {
                vo.setSystemSwitch(-1);
                vo.setWorkMode(0);
            }
            Object o = redisTemplate.opsForHash().get("zm:workModule", String.valueOf(deviceId));
            if (o != null) vo.setWorkMode(Integer.parseInt(o.toString()));
            return vo;
        });
    }
}
