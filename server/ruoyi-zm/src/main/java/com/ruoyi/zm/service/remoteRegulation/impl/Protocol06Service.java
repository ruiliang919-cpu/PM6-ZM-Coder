// package com.ruoyi.zm.service.remoteRegulation.impl;
//
// import com.ruoyi.zm.domain.DevProtocol06;
// import com.ruoyi.zm.mapper.DevProtocol06Mapper;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.stereotype.Service;
// import org.springframework.util.ObjectUtils;
//
// import javax.annotation.Resource;
// import java.util.List;
// import java.util.NoSuchElementException;
// import java.util.Optional;
// import java.util.concurrent.TimeUnit;
//
// // 遥调协议的数据层
// @Service
// public class Protocol06Service {
//     @Resource
//     private DevProtocol06Mapper mapper;
//     @Resource
//     private RedisTemplate<String, Object> redisTemplate;
//
//     // 获取所有的 地址、名称以及倍率，并存入到 Redis
//     private List<DevProtocol06> get06List() {
//         List<DevProtocol06> DevProtocol06s = (List<DevProtocol06>) redisTemplate.opsForValue().get("zm:DevProtocol06sList");
//         if (ObjectUtils.isEmpty(DevProtocol06s)) {
//             DevProtocol06s = mapper.selectList();
//             redisTemplate.opsForValue().set("zm:DevProtocol06sList", DevProtocol06s, 2, TimeUnit.DAYS);
//         }
//         return DevProtocol06s;
//     }
//
//     // 根据遥调协议的名称找到对应的地址和倍率
//     public DevProtocol06 getByProtocolName(String protocolName) {
//         DevProtocol06 DevProtocol06 = (DevProtocol06) redisTemplate.opsForValue().get("zm:DevProtocol06:" + protocolName);
//         if (ObjectUtils.isEmpty(DevProtocol06)) {
//             Optional<DevProtocol06> optionalProtocol = get06List().stream()
//                 .filter(value -> value.getName().equals(protocolName))
//                 .findFirst();
//             if (optionalProtocol.isPresent()) {
//                 redisTemplate.opsForValue().set("zm:DevProtocol06:" + protocolName, optionalProtocol.get(), 2, TimeUnit.DAYS);
//                 return optionalProtocol.get();
//             } else {
//                 throw new NoSuchElementException("未找到协议名称: " + protocolName);
//             }
//         }
//         return DevProtocol06;
//     }
// }
