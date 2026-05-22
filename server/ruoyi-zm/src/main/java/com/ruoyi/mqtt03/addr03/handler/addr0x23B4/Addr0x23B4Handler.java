package com.ruoyi.mqtt03.addr03.handler.addr0x23B4;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service("Addr23B4Handler")
@RequiredArgsConstructor
public class Addr0x23B4Handler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;

    @Override
    @Timer("23B4")
    public void handle(Integer deviceNo, JSONObject payload) {
        Addr0x23B4 body = JSONUtil.toBean(payload.toString(), Addr0x23B4.class);
        Addr0x23B4.Data data = body.getData();
        StringBuilder s = new StringBuilder();
        Integer[] group = data.getGroup();
        for (int i = 0; i < data.getNum(); i++) {
            Integer loop = group[i];
            if (loop < 10) s.append("0").append(loop);
            else s.append(loop);
        }
        String ip = key.getCreateTCP(deviceNo).getIp();
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(ip, "0x23B4", deviceNo), s.toString());
    }
}
