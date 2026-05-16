package com.ruoyi.mqtt03.addr03.handler.addr0x0028;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ruoyi.cache.Key.TELEMETER_KEY;

@Service("Addr0028Handler")
@RequiredArgsConstructor
public class Addr0x0028Handler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;

    @Override
    @Timer("0028")
    public void handle(Integer deviceNo, JsonObject payload) {
        Gson gson = new Gson();
        Addr0x0028 body = gson.fromJson(payload, Addr0x0028.class);
        List<Addr0x0028.Data> data = body.getData();
        String ip = key.getCreateTCP(deviceNo).getIp();
        Map<String, BigDecimal> m = data.stream().collect(Collectors.toMap(d -> (TELEMETER_KEY + ip + ":" + deviceNo + ":" + d.getAddr()), d -> new BigDecimal(d.getValue())));
        redisTemplate.opsForValue().multiSet(m);
    }
}
