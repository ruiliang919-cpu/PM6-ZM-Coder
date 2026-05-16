package com.ruoyi.web.controller.zm;

import cn.dev33.satoken.annotation.SaIgnore;
import com.ruoyi.common.core.domain.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/zm/point")
@RequiredArgsConstructor
public class PointController {
    private final RedisTemplate<String, Object> redisTemplate;

    // 查询更新总点
    @GetMapping("/getStatus")
    public R<?> getStatus(Integer deviceId) {
        String flag = redisTemplate.opsForValue().get("zm:update:" + deviceId + ":Update-the-total-points") + "";
        if ("true".equals(flag)) return R.ok(true);
        return R.ok(false);
    }

    // 重置更新总点
    @GetMapping("/setStatus")
    public R<?> setStatus(Integer deviceId) {
        // redisTemplate.opsForValue().set("zm:update:" + deviceId + ":Update-the-total-points-1", "false");
        // redisTemplate.opsForValue().set("zm:update:" + deviceId + ":Update-the-total-points-2", "false");
        Set<String> keys = redisTemplate.keys("zm:update:*");
        if (keys != null) {
            keys.forEach(key -> redisTemplate.opsForValue().set(key, "false"));
        }
        return R.ok();
    }

    // 就地/远程 假设 0就地 1远程
    // 整站的场景控制、分区控制，主机和上位机遥控都有功能，主机上做一个按钮，就地时主机控制，远程时主机界面无效，受上位机控制
    // 主机需要做一个就地/远程按钮，打到该点位置0，打到远程该点位置1
    // 是整站的，不是单机柜的
    @GetMapping("/setModule")
    public R<?> setModule(Integer module) {
        if (module != null && 0 == module) {
            redisTemplate.opsForValue().set("zm:global:module:select", "on-the-spot");
        } else if (module != null && 1 == module) {
            redisTemplate.opsForValue().set("zm:global:module:select", "on-the-line");
        }
        return R.ok(module);
    }

    @GetMapping("/getModule")
    public R<?> getModule() {
        String module = (String) redisTemplate.opsForValue().get("zm:global:module:select");
        if ("on-the-spot".equals(module)) return R.ok(0);
        if ("on-the-line".equals(module)) return R.ok(1);
        redisTemplate.opsForValue().set("zm:global:module:select", "on-the-spot");
        return R.ok(0);
    }

    @SaIgnore
    @GetMapping("/getZoneStatus")
    public R<?> getZoneStatus() {
        if (redisTemplate.hasKey("zm:global:zone:status")) return R.ok(true);
        else return R.ok(false);
    }

    @GetMapping("/clearZoneStatus")
    public R<?> clearZoneStatus() {
        redisTemplate.delete("zm:global:zone:status");
        return R.ok();
    }

    @SaIgnore
    @GetMapping("/getSceneStatus")
    public R<?> getSceneStatus() {
        if (redisTemplate.hasKey("zm:global:scene:status"))
            return R.ok(true);
        else return R.ok(false);
    }

    @GetMapping("/clearSceneStatus")
    public R<?> clearSceneStatus() {
        redisTemplate.delete("zm:global:scene:status");
        return R.ok();
    }

    @SaIgnore
    @GetMapping("/getLoopStatus")
    public R<?> getLoopStatus() {
        if (redisTemplate.hasKey("zm:global:loop:status:change")) return R.ok(true);
        else return R.ok(false);
    }

    @GetMapping("/clearLoopStatus")
    public R<?> clearLoopStatus() {
        redisTemplate.delete("zm:global:loop:status:change");
        return R.ok();
    }
}
