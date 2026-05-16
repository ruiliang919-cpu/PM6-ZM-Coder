package com.ruoyi.zm.service.telemetering.impl;

import com.ruoyi.zm.domain.DevProtocol03;
import com.ruoyi.zm.mapper.DevProtocol03Mapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

// 遥测协议的数据层
@Service
public class Protocol03Service {
    @Resource
    private DevProtocol03Mapper mapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 获取所有的 地址、名称以及倍率，并存入到 Redis
    public List<DevProtocol03> get03List() {
        List<DevProtocol03> devProtocol03s = (List<DevProtocol03>) redisTemplate.opsForValue().get("zm:devProtocol03sList");
        if (ObjectUtils.isEmpty(devProtocol03s)) {
            devProtocol03s = mapper.selectList();
            redisTemplate.opsForValue().set("zm:devProtocol03sList", devProtocol03s, 2, TimeUnit.DAYS);
        }
        // System.out.println("devProtocol03s" + devProtocol03s);
        return devProtocol03s;
    }

    // 根据遥测协议的名称找到对应的地址和倍率
    public DevProtocol03 getByProtocolName(String protocolName) {
        DevProtocol03 devProtocol03 = (DevProtocol03) redisTemplate.opsForValue().get("zm:devProtocol03:" + protocolName);
        if (ObjectUtils.isEmpty(devProtocol03)) {
            Optional<DevProtocol03> optionalProtocol = get03List().stream()
                .filter(value -> value.getName().equals(protocolName))
                .findFirst();
            if (optionalProtocol.isPresent()) {
                redisTemplate.opsForValue().set("zm:devProtocol03:" + protocolName, optionalProtocol.get(), 2, TimeUnit.DAYS);
                return optionalProtocol.get();
            } else {
                throw new NoSuchElementException("未找到协议名称: " + protocolName);
            }
        }
        return devProtocol03;
    }

    // 根据遥测协议的地址找到对应的名称和倍率
    public DevProtocol03 getByProtocolAddr(String addr) {
        DevProtocol03 devProtocol03 = (DevProtocol03) redisTemplate.opsForValue().get("zm:devProtocol03:addr:" + addr);
        if (ObjectUtils.isEmpty(devProtocol03)) {
            Optional<DevProtocol03> optionalProtocol = get03List().stream()
                .filter(value -> value.getAddr().equals(addr))
                .findFirst();
            if (optionalProtocol.isPresent()) {
                redisTemplate.opsForValue().set("zm:devProtocol03:addr:" + addr, optionalProtocol.get(), 2, TimeUnit.DAYS);
                return optionalProtocol.get();
            } else {
                return new DevProtocol03();
            }
        }
        return devProtocol03;
    }
}
