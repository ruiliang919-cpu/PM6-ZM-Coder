package com.ruoyi.mqtt03.addr03.handler.addr0xA5AE;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ruoyi.cache.Key.REMOTE_KEY;

@Service("AddrA5AEHandler")
@RequiredArgsConstructor
public class Addr0xA5AEHandler implements AddrHandler {
    private final RedisTemplate<String, short[]> shortArrayRedisTemplate;
    private final Key key;

    @Override
    @Timer("A5AE")
    public void handle(Integer deviceNo, JSONObject payload) {
        Addr0xA5AE body = JSONUtil.toBean(payload.toString(), Addr0xA5AE.class);
        List<Addr0xA5AE.Data> data = body.getData();
        short[] s = new short[16];
        for (int i = 0; i < data.size(); i++) s[i] = (short) data.get(i).getNo();
        String ip = key.getCreateTCP(deviceNo).getIp();
        shortArrayRedisTemplate.opsForValue().set(REMOTE_KEY + ip + ":" + deviceNo + ":0xA5AE", s);
    }
}
