package com.ruoyi.send.business;

import com.ruoyi.send.Update03DataService;
import com.ruoyi.zm.config.ModbusTCPManager;
import com.ruoyi.zm.domain.DevInstruct;
import com.ruoyi.zm.domain.DevProtocol03;
import com.ruoyi.zm.service.telemetering.impl.Protocol03Service;
import com.ruoyi.zm.utils.ScaleUtil;
import com.serotonin.modbus4j.ModbusMaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ruoyi.zm.utils.ScaleUtil.toAscII;

@Slf4j
@Component
@RequiredArgsConstructor
public class Code03Business {
    private final Protocol03Service protocol03Service;
    private final Update03DataService update03DataService;
    private final ModbusTCPManager masterTcp;
    private final RedisTemplate<String, Object> redisTemplate;

    // 功能码03业务逻辑
    public void Business(DevInstruct instruct) {
        ModbusMaster slave = masterTcp.getSlave(instruct.getSalveId());
        if(slave == null) return;
        // 处理发送地址
        int addr = ScaleUtil.from16To10(instruct.getAddr());
        try {
            // 发送请求
//            short[] shorts = ModbusTcpUtil.ReadHR(slave, instruct.getSalveId(), addr, instruct.getAddrNum());
//            response03(instruct, shorts);
        } catch (Exception e) {
            log.error("03 Business 出错了：", e);
        }
    }

    // 功能码03的数据处理方法
    // addr：保持寄存器地址
    // data：设备返回的数据
    private Object dispose03Data(String addr, short[] data) {
        DevProtocol03 protocol03 = protocol03Service.getByProtocolAddr(addr);
        Object result;
        // 处理倍率
        if (!ObjectUtils.isEmpty(protocol03.getMagnification())) {
            // 处理高位转低位
            List<BigDecimal> list = new ArrayList<>();
            if (
                protocol03.getName().contains("总电量") || protocol03.getName().contains("总功率") || protocol03.getName().contains("日耗电量") ||
                    protocol03.getName().contains("周耗电量") || protocol03.getName().contains("月耗电量") || protocol03.getName().contains("季耗电量") ||
                    protocol03.getName().contains("年耗电量") || protocol03.getName().contains("电能表总电量") || protocol03.getName().contains("电能表总功率") ||
                    protocol03.getName().contains("电表日耗电量") || protocol03.getName().contains("电表周耗电量") || protocol03.getName().contains("电表月耗电量") ||
                    protocol03.getName().contains("电表季耗电量") || protocol03.getName().contains("电表年耗电量") || protocol03.getName().contains("直流回路实时功率")
            ) {
                for (int i = 0; i < data.length; i += 2)
                    list.add(BigDecimal.valueOf((((long) (data[i] & 0xFFFF) << 16) | (data[i + 1] & 0xFFFF))).divide(new BigDecimal(protocol03.getMagnification())));
                result = list;
            } else {
                if (data.length > 2) {
                    for (short datum : data) list.add(BigDecimal.valueOf(datum).divide(new BigDecimal(protocol03.getMagnification())));
                    result = list;
                } else result = BigDecimal.valueOf(data[0]).divide(new BigDecimal(protocol03.getMagnification()));
            }
        }
        else if (addr.equals("0x2740")) result = data[0] + "";
        else result = toAscII(data);
        return result;
    }

    private void response03(DevInstruct instruct, short[] shorts) {
        try {
            // [1] 读取寄存器地址对应的协议信息，若有倍率则处理，若为 ASCII 字符串则处理返回的结果至 0000...之前
            Object response = dispose03Data(instruct.getAddr(), shorts);
            // [2] 判断返回的结果与缓存的对象是否相等
            boolean result = eq03Cache(instruct, response);
            if (result == false) {
                try {
                    // 若不等则更新缓存
                    update03Cache(instruct, response);
                } catch (Exception e) {
                    log.error("处理 地址：{} 功能码：{} 数据出错：", instruct.getAddr(), instruct.getCode(), e);
                }
            }
            if (instruct.getAddr().equals("0x0000") ||
                instruct.getAddr().equals("0X002B") ||
                instruct.getAddr().equals("0X00CB") ||
                instruct.getAddr().equals("0X011B") ||
                instruct.getAddr().equals("0X016B") ||
                instruct.getAddr().equals("0X01BB") ||
                instruct.getAddr().equals("0X020B")) {
                update03DataService.updateData(instruct, response);
            }
        } catch (Exception e) {
            log.error("response03 出错：", e);
        }
    }

    // 更新缓存的方法 功能码03
    private void update03Cache(DevInstruct instruct, Object data) {
        String key = String.format("zm:queue:zm:cache:%d:%s:%d:%s", instruct.getCode(), instruct.getIp(), instruct.getSalveId(), instruct.getAddr());
        redisTemplate.opsForValue().set(key, data);
    }

    // 功能码03 缓存与响应对象对比方法
    private Boolean eq03Cache(DevInstruct instruct, Object data) {
        String key = String.format("zm:queue:zm:cache:%d:%s:%d:%s", instruct.getCode(), instruct.getIp(), instruct.getSalveId(), instruct.getAddr());
        Object cache = redisTemplate.opsForValue().get(key);
        if (data instanceof short[] && cache != null) {
            return Arrays.equals((short[]) data, (short[]) cache);
        }
        if (data instanceof int[] && cache != null) return Arrays.equals((int[]) data, (int[]) cache);
        // 缓存中不存在数据，直接插入缓存，并返回 false，以便数据库更新数据
        if (ObjectUtils.isEmpty(cache)) {
            redisTemplate.opsForValue().set(key, data);
            return false;
        }
        return cache.equals(data);
    }
}
