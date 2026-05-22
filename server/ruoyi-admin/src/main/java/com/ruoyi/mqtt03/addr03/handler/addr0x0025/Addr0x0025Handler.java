package com.ruoyi.mqtt03.addr03.handler.addr0x0025;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service("Addr0025Handler")
@RequiredArgsConstructor
public class Addr0x0025Handler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;

    @Override
    @Timer("0025")
    public void handle(Integer deviceNo, JSONObject payload) {
        Addr0x0025 body = JSONUtil.toBean(payload.toString(), Addr0x0025.class);
        Integer num = body.getNum();
        String ip = key.getCreateTCP(deviceNo).getIp();
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(ip, "0x0025", deviceNo), num);

    }
}
