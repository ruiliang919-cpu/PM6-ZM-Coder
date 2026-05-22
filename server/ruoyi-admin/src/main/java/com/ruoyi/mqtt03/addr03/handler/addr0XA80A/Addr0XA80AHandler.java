package com.ruoyi.mqtt03.addr03.handler.addr0XA80A;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ruoyi.cache.Key.REMOTE_KEY;

@Service("AddrA80AHandler")
@RequiredArgsConstructor
public class Addr0XA80AHandler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;
    public static final String[] addr = {"", "0XA80A", "0XA853", "0XA89C"};

    @Timer("A80A")
    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
        Addr0XA80A body = JSONUtil.toBean(payload.toString(), Addr0XA80A.class);
        int controlId = body.getControlId();
        Map<String, Object> data = new HashMap<>();
        data.put("data1", body.getData1());
        data.put("data2", body.getData2());
        String ip = key.getCreateTCP(deviceNo).getIp();
        redisTemplate.opsForValue().set(REMOTE_KEY + ip + ":" + deviceNo + ":" + addr[controlId], data);
        Map<String, Integer> data1 = new HashMap<>();
        data1.put("data3", body.getEnabled() ? body.getControlId() : 0);
        if (Objects.equals(data1.getOrDefault("data3", 0), body.getControlId())) redisTemplate.opsForValue().set(REMOTE_KEY + ip + ":" + deviceNo + ":0XA80AEnabled", data1);
    }
}
