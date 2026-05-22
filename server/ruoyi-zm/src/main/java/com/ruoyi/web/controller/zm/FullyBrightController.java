package com.ruoyi.web.controller.zm;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.modbus.util.RecordPlus;
import com.ruoyi.netty.handler.RtuWriteUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

// 全亮全灭控制层
@RestController
@RequiredArgsConstructor
@RequestMapping("/full/bright")
public class FullyBrightController {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RtuWriteUtil rtuWriteUtil;
    private final RecordPlus record;

    // 获取全亮/全灭状态
    @GetMapping("/status")
    public R<?> status() {
        String status = (String) redisTemplate.opsForValue().get("zm:global:loop:status");
        if (status != null && !status.isEmpty()) {
            return R.ok(status.equals("true"));
        }
        return R.ok(false);
    }

    @GetMapping("/set")
    public R<?> set(@RequestParam("flag") @NotNull boolean flag) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        if (flag) {
            record.runModule();
            rtuWriteUtil.allLoopControl1(1);
            redisTemplate.opsForValue().set("zm:global:loop:status", "true");
            return R.ok("已下发全亮指令");
        } else {
            record.runModule();
            rtuWriteUtil.allLoopControl1(0);
            redisTemplate.opsForValue().set("zm:global:loop:status", "false");
            return R.ok("已下发全灭指令");
        }
    }

    // 当设备处于远程模式时以上接口不能使用
    private Boolean module() {
        String module = (String) redisTemplate.opsForValue().get("zm:global:module:select");
        return "on-the-line".equals(module);
    }
}
