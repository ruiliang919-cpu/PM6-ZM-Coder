package com.ruoyi.zm.service.telecommand.impl;

import com.ruoyi.zm.domain.DevProtocol01;
import com.ruoyi.zm.mapper.DevProtocol01Mapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

// 遥信协议的数据层
@Service
public class Protocol01Service {
    @Resource
    private DevProtocol01Mapper mapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 获取所有的 地址、名称以及倍率，并存入到 Redis
    public List<DevProtocol01> get01List() {
        List<DevProtocol01> DevProtocol01s = (List<DevProtocol01>) redisTemplate.opsForValue().get("zm:DevProtocol01sList");
        if (ObjectUtils.isEmpty(DevProtocol01s)) {
            DevProtocol01s = mapper.selectList();
            redisTemplate.opsForValue().set("zm:DevProtocol01sList", DevProtocol01s, 2, TimeUnit.DAYS);
        }
        return DevProtocol01s;
    }

    // 根据遥信协议的名称找到对应的地址和倍率
    public DevProtocol01 getByProtocolName(String protocolName) {
        DevProtocol01 DevProtocol01 = (DevProtocol01) redisTemplate.opsForValue().get("zm:DevProtocol01:" + protocolName);
        if (ObjectUtils.isEmpty(DevProtocol01)) {
            Optional<DevProtocol01> optionalProtocol = get01List().stream()
                .filter(value -> value.getName().equals(protocolName))
                .findFirst();
            if (optionalProtocol.isPresent()) {
                redisTemplate.opsForValue().set("zm:DevProtocol01:" + protocolName, optionalProtocol.get(), 2, TimeUnit.DAYS);
                return optionalProtocol.get();
            } else {
                throw new NoSuchElementException("未找到协议名称: " + protocolName);
            }
        }
        return DevProtocol01;
    }
}
