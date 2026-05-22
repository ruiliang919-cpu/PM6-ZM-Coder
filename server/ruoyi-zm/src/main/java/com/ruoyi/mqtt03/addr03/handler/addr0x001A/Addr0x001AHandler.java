package com.ruoyi.mqtt03.addr03.handler.addr0x001A;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("Addr001AHandler")
@RequiredArgsConstructor
public class Addr0x001AHandler implements AddrHandler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Key key;

    @Timer("001A")
    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
        Addr0x001A body = JSONUtil.toBean(payload.toString(), Addr0x001A.class);
        List<Addr0x001A.Data> data = body.getData();
        String ip = key.getCreateTCP(deviceNo).getIp();
        Map<String, BigDecimal> m = data.stream().collect(Collectors.toMap(d -> (AddrHandlerFactory.getKey(ip, d.getAddr(), deviceNo)), d -> new BigDecimal(d.getValue())));
        redisTemplate.opsForValue().multiSet(m);
    }
}
