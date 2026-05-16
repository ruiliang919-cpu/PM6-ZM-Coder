package com.ruoyi.mqtt03.addr03.handler.addr0XAAEE;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import com.ruoyi.zm.domain.DevConfigTimeControlScene;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import static com.ruoyi.cache.Key.REMOTE_KEY;

@Service("AddrAAEEHandler")
@RequiredArgsConstructor
public class Addr0XAAEEHandler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;
    private static final String[] addr = {"", "0XAAEE", "0XAB2E", "0XAB6E"};

    @Timer("AAEE")
    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        Gson gson = new Gson();
        Addr0XAAEE body = gson.fromJson(payload, Addr0XAAEE.class);
        int controlId = body.getControlId();
        LinkedList<DevConfigTimeControlScene> data = body.getData();
        String ip = key.getCreateTCP(deviceNo).getIp();
        redisTemplate.opsForValue().set(REMOTE_KEY + ip + ":" + deviceNo + ":" + addr[controlId], data);
        Map<String, Integer> data1 = new HashMap<>();
        data1.put("data3", body.getEnabled() ? body.getControlId() : 0);
        if (Objects.equals(data1.getOrDefault("data3", 0), body.getControlId())) redisTemplate.opsForValue().set(REMOTE_KEY + ip + ":" + deviceNo + ":0XAAEEEnabled", data1);
    }
}
