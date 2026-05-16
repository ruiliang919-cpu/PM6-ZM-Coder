package com.ruoyi.mqtt03.addr03.handler.addr0x0022;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.mqtt03.addr03.base.AddrHandler;
import com.ruoyi.utils.device.time.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("Addr0022Handler")
@RequiredArgsConstructor
public class Addr0X0022Handler implements AddrHandler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Key k;

    @Timer("0022")
    @Override
    public void handle(Integer deviceNo, JsonObject payload) {
        Gson gson = new Gson();
        Addr0X0022 body = gson.fromJson(payload, Addr0X0022.class);
        Map<String, List<BigDecimal>> m = new HashMap<>();
        String ip = k.getCreateTCP(deviceNo).getIp();
        int luxNum = body.getLuxNum();
        int switchNum = body.getSwitchNum();
        List<Addr0X0022.Data> data = body.getData();
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(ip, "0x0022", deviceNo), BigDecimal.valueOf(luxNum));
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(ip, "0x0023", deviceNo), BigDecimal.valueOf(switchNum));

        List<BigDecimal> c1 = data.stream().map(Addr0X0022.Data::getBrightnessSetting).map(BigDecimal::new).collect(Collectors.toList());
        List<BigDecimal> c2 = data.stream().map(Addr0X0022.Data::getBrightnessFeedback).map(BigDecimal::new).collect(Collectors.toList());
        List<BigDecimal> c3 = data.stream().map(Addr0X0022.Data::getOutputVoltage).map(BigDecimal::new).collect(Collectors.toList());
        List<BigDecimal> c4 = data.stream().map(Addr0X0022.Data::getOutputCurrent).map(BigDecimal::new).collect(Collectors.toList());
        List<BigDecimal> c5 = data.stream().map(Addr0X0022.Data::getInternalTemperature).map(BigDecimal::new).collect(Collectors.toList());
        List<BigDecimal> c6 = data.stream().map(Addr0X0022.Data::getRealTime).map(BigDecimal::new).collect(Collectors.toList());
        m.put(AddrHandlerFactory.getKey(ip, "0X04A0", deviceNo), c1);
        m.put(AddrHandlerFactory.getKey(ip, "0X04C8", deviceNo), c2);
        m.put(AddrHandlerFactory.getKey(ip, "0X04F0", deviceNo), c3);
        m.put(AddrHandlerFactory.getKey(ip, "0X0518", deviceNo), c4);
        m.put(AddrHandlerFactory.getKey(ip, "0X0540", deviceNo), c5);
        m.put(AddrHandlerFactory.getKey(ip, "0X007B", deviceNo), c6);
        redisTemplate.opsForValue().multiSet(m);
    }


}
