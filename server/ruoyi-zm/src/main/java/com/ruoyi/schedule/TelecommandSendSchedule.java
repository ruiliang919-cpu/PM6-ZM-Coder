package com.ruoyi.schedule;

import com.ruoyi.send.Update01DataService;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;

// 遥信定时任务
@Slf4j
@Component
@RequiredArgsConstructor
public class TelecommandSendSchedule {
    private final Update01DataService update01DataService;
    private final RedisTemplate<String, boolean[]> boolCache;

    @Timer("遥信")
    public void U(String ip, int deviceNo, boolean[] a) {
        if (NotEqCache(a, String.format("zm:queue:zm:cache:%d:%s:%d:%s", 1, ip, deviceNo, "0x0000")))
            update01DataService.UpdateBoolData(deviceNo, a);
    }

    private Boolean NotEqCache(boolean[] data, String k) {
        if (ObjectUtils.isEmpty(data) || Arrays.toString(data).isEmpty()) return false;
        boolean[] cache = boolCache.opsForValue().get(k);
        if (cache == null) {
            boolCache.opsForValue().set(k, data);
            return true;
        }
        boolean b = !Arrays.equals(cache, data);
        if (b) {
            boolCache.opsForValue().set(k, data);
            return true;
        }
        return false;
    }
}
