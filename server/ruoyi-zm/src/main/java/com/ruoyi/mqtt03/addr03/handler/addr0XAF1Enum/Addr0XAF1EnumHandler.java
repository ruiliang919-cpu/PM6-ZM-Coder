package com.ruoyi.mqtt03.addr03.handler.addr0XAF1Enum;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.ruoyi.cache.Key.REMOTE_KEY;

@Service("AddrAF1EnumHandler")
@RequiredArgsConstructor
public class Addr0XAF1EnumHandler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;

    @Timer("AF1Enum")
    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
        Addr0XAF1Enum body = JSONUtil.toBean(payload.toString(), Addr0XAF1Enum.class);
        String ip = key.getCreateTCP(deviceNo).getIp();
        redisTemplate.opsForValue().set(REMOTE_KEY + ip + ":" + deviceNo + ":0XAF1Enum", body.getNum());
    }
}
