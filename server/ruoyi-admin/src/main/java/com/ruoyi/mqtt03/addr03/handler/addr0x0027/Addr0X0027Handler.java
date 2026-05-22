package com.ruoyi.mqtt03.addr03.handler.addr0x0027;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("Addr0027Handler")
@RequiredArgsConstructor
public class Addr0X0027Handler implements AddrHandler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Key k;

    @Override
    @Timer("0027")
    public void handle(Integer deviceNo, JSONObject payload) {
        Addr0X0027 body = JSONUtil.toBean(payload.toString(), Addr0X0027.class);
        Map<String, List<BigDecimal>> m = new HashMap<>();
        String ip = k.getCreateTCP(deviceNo).getIp();
        int num = body.getNum();
        List<Addr0X0027.Data> data = body.getData();
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(ip, "0x0027", deviceNo), BigDecimal.valueOf(num));
        List<BigDecimal> c1 = data.stream().map(Addr0X0027.Data::getVoltage).map(BigDecimal::new).collect(Collectors.toList());
        List<BigDecimal> c2 = data.stream().map(Addr0X0027.Data::getCurrent).map(BigDecimal::new).collect(Collectors.toList());
        m.put(AddrHandlerFactory.getKey(ip, "0x0420", deviceNo), c1);
        m.put(AddrHandlerFactory.getKey(ip, "0x0430", deviceNo), c2);
        redisTemplate.opsForValue().multiSet(m);
    }


}
