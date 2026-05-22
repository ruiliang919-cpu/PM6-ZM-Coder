package com.ruoyi.mqtt03.addr03.handler.addr0XAE8F;

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

import static com.ruoyi.cache.Key.REMOTE_KEY;

@Service("AddrAE8FHandler")
@RequiredArgsConstructor
public class Addr0XAE8FHandler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;

    @Timer("AE8F")
    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
        Addr0XAE8F body = JSONUtil.toBean(payload.toString(), Addr0XAE8F.class);
        Map<String, Object> data = new HashMap<>();
        data.put("data", body.getData());
        data.put("data1", body.getData1());
        data.put("data2", body.getData2());
        data.put("data3", body.getData3());
        data.put("data4", body.getData4());
        data.put("data5", body.getData5());
        data.put("select", body.getSelect());
        String ip = key.getCreateTCP(deviceNo).getIp();
        redisTemplate.opsForValue().set(REMOTE_KEY + ip + ":" + deviceNo + ":0XAE8F", data);
    }
}
