package com.ruoyi.utils.device.time;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TimerAspect {
    private final RedisTemplate<String, Object> redisTemplate;
    public static final Set<String> address = new HashSet<>();

    @Around("@annotation(timer)")
    public Object logAndRecordExecutionTime(ProceedingJoinPoint joinPoint, Timer timer) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;
        saveData(timer.value(), duration);
        address.add(timer.value());
        return result;
    }

    private void saveData(String addr, long value) {
        String key = buildRedisKey(addr);
        redisTemplate.opsForList().rightPush(key, value);
        redisTemplate.opsForList().trim(key, -10, -1);
    }

    static String buildRedisKey(String addr) {
        return "zm:time:data:" + addr;
    }
}
