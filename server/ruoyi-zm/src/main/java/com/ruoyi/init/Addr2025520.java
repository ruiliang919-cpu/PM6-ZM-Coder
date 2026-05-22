package com.ruoyi.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.zm.domain.DevProtocol01;
import com.ruoyi.zm.domain.DevProtocol03;
import com.ruoyi.zm.mapper.DevProtocol01Mapper;
import com.ruoyi.zm.mapper.DevProtocol03Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Addr2025520 {
    @Value("${init.addr2025520:false}")
    public boolean initFlag;
    private final DevProtocol03Mapper protocol03Mapper;
    private final DevProtocol01Mapper protocol01Mapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        if (initFlag) {
            redisTemplate.delete("zm:devProtocol03sList");
            List<String> n3s = Arrays.asList("整流模块输出电压17", "整流模块输出电压18", "整流模块输出电压19", "整流模块输出电压20", "整流模块输出电压21", "整流模块输出电流17", "整流模块输出电流18", "整流模块输出电流19", "整流模块输出电流20", "整流模块输出电流21");
            List<DevProtocol03> p3s = protocol03Mapper.selectList(new LambdaQueryWrapper<DevProtocol03>().select(DevProtocol03::getId).in(DevProtocol03::getAddr, "0x0428", "0x0429", "0x042A", "0x042B", "0x042C", "0x0438", "0x0439", "0x043A", "0x043B", "0x043C"));
            for (int i = 0; i < p3s.size(); i++) p3s.get(i).setName(n3s.get(i));
            protocol03Mapper.updateBatchById(p3s);
            List<String> n1s = Arrays.asList("整流模块通讯故障17", "整流模块通讯故障18", "整流模块通讯故障19", "整流模块通讯故障20", "整流模块通讯故障21", "整流模块故障17", "整流模块故障18", "整流模块故障19", "整流模块故障20", "整流模块故障21", "整流模块开关机状态17", "整流模块开关机状态18", "整流模块开关机状态19", "整流模块开关机状态20", "整流模块开关机状态21");
            List<DevProtocol01> p1s = protocol01Mapper.selectList(new LambdaQueryWrapper<DevProtocol01>().select(DevProtocol01::getId).in(DevProtocol01::getAddr, "0x0027", "0x0028", "0x0029", "0x002A", "0x002B", "0x0118", "0x0119", "0x011A", "0x011B", "0x011C", "0x0166", "0x0167", "0x0168", "0x0169", "0x016A"));
            for (int i = 0; i < p1s.size(); i++) p1s.get(i).setName(n1s.get(i));
            protocol01Mapper.updateBatchById(p1s);
            Long c = protocol01Mapper.selectCount(new LambdaQueryWrapper<DevProtocol01>().eq(DevProtocol01::getAddr, "0x0027"));
            if (c >= 1) return;
            List<DevProtocol01> dp1s = new ArrayList<>();
            new HashMap<String, String>() {{
                put("0x0027", "整流模块通讯故障17");
                put("0x0028", "整流模块通讯故障18");
                put("0x0029", "整流模块通讯故障19");
                put("0x002A", "整流模块通讯故障20");
                put("0x002B", "整流模块通讯故障21");
            }}.forEach((k, v) -> dp1s.add(new DevProtocol01(){{
                setAddr(k);
                setName(v);
            }}));
            protocol01Mapper.insertBatch(dp1s);
        }
    }
}
