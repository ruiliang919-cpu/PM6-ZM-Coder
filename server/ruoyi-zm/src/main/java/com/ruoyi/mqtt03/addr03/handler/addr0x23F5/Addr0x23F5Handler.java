package com.ruoyi.mqtt03.addr03.handler.addr0x23F5;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.cache.LoopByGroupCache;
import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("Addr23F5Handler")
@RequiredArgsConstructor
public class Addr0x23F5Handler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;

    @Override
    @Timer("23F5")
    public void handle(Integer deviceNo, JSONObject payload) {
        Addr0x23F5 body = JSONUtil.toBean(payload.toString(), Addr0x23F5.class);
        List<Addr0x23F5.Data> data = body.getData();
        List<String> c1 = data.stream().map(Addr0x23F5.Data::getName).collect(Collectors.toList());
        List<Long> c2 = data.stream().map(Addr0x23F5.Data::getLoopNum).map(Long::valueOf).collect(Collectors.toList());
        String ip = key.getCreateTCP(deviceNo).getIp();
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(ip, "0xA1F0", deviceNo), c1);
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(ip, "0x23F5num", deviceNo), c2);
        String[] loopAddress = LoopByGroupCache.LOOP_NUMS_ADDR_ARR;
        Map<String, String> m = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            Addr0x23F5.Data d = data.get(i);
            m.put(AddrHandlerFactory.getKey(ip, loopAddress[i], deviceNo), loopStr(d.getValue()));
        }
        redisTemplate.opsForValue().multiSet(m);
    }

    private String loopStr(Integer[] g) {
        StringBuilder s = new StringBuilder();

        for (Integer v : g) {
            if (v != 0) {
                if (v < 10) s.append("0").append(v);
                else s.append(v);
            }
        }

        return (s.length() == 0) ? "" : s.toString();
    }
}
