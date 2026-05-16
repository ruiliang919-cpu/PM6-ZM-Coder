package com.ruoyi.mqtt03.addr03.handler.addr0x0026;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("Addr0026Handler")
@RequiredArgsConstructor
public class Addr0X0026Handler implements AddrHandler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Key k;

    @Override
    @Timer("0026")
    public void handle(Integer deviceNo, JsonObject payload) {
        Gson gson = new Gson();
        Addr0X0026 body = gson.fromJson(payload, Addr0X0026.class);
        Map<String, List<BigDecimal>> m = new HashMap<>();
        String ip = k.getCreateTCP(deviceNo).getIp();
        int num = body.getNum();
        List<Addr0X0026.Data> data = body.getData();
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(ip, "0x0026", deviceNo), BigDecimal.valueOf(num));
        List<BigDecimal> c1 = data.stream().map(Addr0X0026.Data::getVoltage).map(BigDecimal::new).collect(Collectors.toList());
        List<BigDecimal> c2 = data.stream().map(Addr0X0026.Data::getCurrent).map(BigDecimal::new).collect(Collectors.toList());
        m.put(AddrHandlerFactory.getKey(ip, "0x0400", deviceNo), c1);
        m.put(AddrHandlerFactory.getKey(ip, "0x0410", deviceNo), c2);
        redisTemplate.opsForValue().multiSet(m);
    }


}
