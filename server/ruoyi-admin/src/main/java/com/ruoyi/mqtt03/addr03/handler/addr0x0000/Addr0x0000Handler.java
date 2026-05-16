package com.ruoyi.mqtt03.addr03.handler.addr0x0000;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import com.ruoyi.zm.domain.DevBaseLoss;
import com.ruoyi.zm.domain.vo.PowerTimeRespVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("Addr0000Handler")
@RequiredArgsConstructor
public class Addr0x0000Handler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;

    @Timer("0000")
    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        Gson gson = new Gson();
        Addr0x0000 body = gson.fromJson(payload, Addr0x0000.class);
        DevBaseLoss loss = (DevBaseLoss) redisTemplate.opsForValue().get("zm:power:loss:" + deviceNo);
        PowerTimeRespVo power = (PowerTimeRespVo) redisTemplate.opsForValue().get("zm:power:power:" + deviceNo);
        DevBaseLoss cacheLoss = new DevBaseLoss();
        PowerTimeRespVo cachePower = new PowerTimeRespVo();
        cachePower.setPower(new BigDecimal(body.getData()[0]));
        cacheLoss.setLoss(new BigDecimal(body.getData()[1]));
        if (loss == null || !loss.equals(cacheLoss)) redisTemplate.opsForValue().set("zm:power:loss:" + deviceNo, cacheLoss);
        if (power == null || !power.equals(cachePower)) redisTemplate.opsForValue().set("zm:power:power:" + deviceNo, cachePower);
    }
}
