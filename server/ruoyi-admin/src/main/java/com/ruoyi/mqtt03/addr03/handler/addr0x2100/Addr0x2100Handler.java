package com.ruoyi.mqtt03.addr03.handler.addr0x2100;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("Addr2100Handler")
@RequiredArgsConstructor
public class Addr0x2100Handler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Timer("2100")
    public void handle(Integer deviceNo, JsonObject payload) {
        Gson gson = new Gson();
        Addr0x2100 body = gson.fromJson(payload, Addr0x2100.class);
        String key = "zm:power:ill:" + deviceNo + ":";
        int select = body.getSelect();
        List<Addr0x2100.Data> data = body.getData();
        redisTemplate.opsForValue().set(key + "select", select);
        for (Addr0x2100.Data datum : data) {
            String k = key + datum.getSensorId();
            Map<String, Integer> m = new HashMap<>();
            m.put("outAddress", Integer.parseInt(datum.getOutControlAddr()));
            m.put("outChannel", Integer.parseInt(datum.getOutControlChannel()));
            redisTemplate.opsForHash().put(k, "lux", datum.getIlluminanceLux());
            redisTemplate.opsForHash().put(k, "outControlStatus", datum.getOutControlStatus());
            redisTemplate.opsForHash().put(k, "address", m);
        }


    }
}
