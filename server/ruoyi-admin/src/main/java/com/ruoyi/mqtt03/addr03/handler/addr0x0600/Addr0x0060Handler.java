package com.ruoyi.mqtt03.addr03.handler.addr0x0600;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service("Addr0600Handler")
@RequiredArgsConstructor
public class Addr0x0060Handler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;

    @Override
    @Timer("0600")
    public void handle(Integer deviceNo, JsonObject payload) {
        Gson gson = new Gson();
        Addr0x0600 body = gson.fromJson(payload, Addr0x0600.class);
        int num = body.getNum();
        List<Addr0x0600.Data> data = body.getData();
        String ip = key.getCreateTCP(deviceNo).getIp();
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(ip, "0x2740", deviceNo), num + "");
        List<String> c1 = data.stream().map(Addr0x0600.Data::getName).collect(Collectors.toList());
        List<BigDecimal> c2 = data.stream().map(Addr0x0600.Data::getCurrent).map(BigDecimal::new).collect(Collectors.toList());
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(ip, "0x0600", deviceNo), c1);
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(ip, "0x2700", deviceNo), c2);
    }
}
