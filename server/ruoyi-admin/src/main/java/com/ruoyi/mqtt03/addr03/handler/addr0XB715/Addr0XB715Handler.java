package com.ruoyi.mqtt03.addr03.handler.addr0XB715;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.ruoyi.cache.Key.REMOTE_KEY;

@Service("AddrB715Handler")
@RequiredArgsConstructor
public class Addr0XB715Handler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;

    @Override
    @Timer("B715")
    public void handle(Integer deviceNo, JsonObject payload) {
        Gson gson = new Gson();
        Addr0XB715 body = gson.fromJson(payload, Addr0XB715.class);
        Addr0XB715.Data data = body.getData();
        String ip = key.getCreateTCP(deviceNo).getIp();
        redisTemplate.opsForValue().set(REMOTE_KEY + ip + ":" + deviceNo + ":0XB715", data);
    }
}
