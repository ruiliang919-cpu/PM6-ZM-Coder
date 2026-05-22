package com.ruoyi.mqtt03.addr03.handler.addr0XA8E5;

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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ruoyi.cache.Key.REMOTE_KEY;

@Service("AddrA8E5Handler")
@RequiredArgsConstructor
public class Addr0XA8E5Handler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;

    @Override
    @Timer("A8E5")
    public void handle(Integer deviceNo, JSONObject payload) {
        Addr0XA8E5 body = JSONUtil.toBean(payload.toString(), Addr0XA8E5.class);
        Map<String, Map<Integer, Boolean>> data = new HashMap<>();
        Map<Integer, Boolean> d1 = body.getData1().stream().collect(Collectors.toMap(Addr0XA8E5.Data::getControlId, Addr0XA8E5.Data::getEnabled, (o1, o2) -> o2));
        Map<Integer, Boolean> d2 = body.getData2().stream().collect(Collectors.toMap(Addr0XA8E5.Data::getControlId, Addr0XA8E5.Data::getEnabled, (o1, o2) -> o2));
        data.put("data1", d1);
        data.put("data2", d2);
        String ip = key.getCreateTCP(deviceNo).getIp();
        IntStream.of(1, 2, 3).forEach(i -> {
            if (d1.getOrDefault(i, false)) {
                Map<String, Integer> m = new HashMap<>();
                m.put("data3", i);
                redisTemplate.opsForValue().set(REMOTE_KEY + ip + ":" + deviceNo + ":0XA80AEnabled", m);
            }
            if (d2.getOrDefault(i, false)) {
                Map<String, Integer> m = new HashMap<>();
                m.put("data3", i);
                redisTemplate.opsForValue().set(REMOTE_KEY + ip + ":" + deviceNo + ":0XAAEEEnabled", m);
            }
        });
    }
}
