package com.ruoyi.mqtt03.addr03.handler.addr0XAF1E;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import com.ruoyi.zm.domain.DevDeviceRemoteRead;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.ruoyi.cache.Key.REMOTE_KEY;

@Service("AddrAF1EHandler")
@RequiredArgsConstructor
public class Addr0XAF1EHandler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;

    @Timer("AF1E")
    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
        Addr0XAF1E body = JSONUtil.toBean(payload.toString(), Addr0XAF1E.class);
        Map<String, Object> data = new HashMap<>();
        DevDeviceRemoteRead d = new DevDeviceRemoteRead();
        d.setAcSwitchNum(body.getAcSwitchNum());
        data.put("acNum", d);
        data.put("controlAc", body.getData());
        String ip = key.getCreateTCP(deviceNo).getIp();
        redisTemplate.opsForValue().set(REMOTE_KEY + ip + ":" + deviceNo + ":0XAF1E", data);
    }
}
