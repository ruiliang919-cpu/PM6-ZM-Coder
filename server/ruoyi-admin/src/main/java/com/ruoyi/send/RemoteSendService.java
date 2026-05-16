package com.ruoyi.send;

import com.ruoyi.cache.Key;
import com.ruoyi.pubsub.RedisPublisher;
import com.ruoyi.schedule.util.InstructAddrUtil;
import com.ruoyi.zm.domain.DevInstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 遥调发送`读取`指令到队列接口
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteSendService {
    private final InstructAddrUtil addrUtil;
    private final Map<String, List<DevInstruct>> map = new HashMap<>();
    private final Key key;

    public List<DevInstruct> Push(int slaveId) {
        List<DevInstruct> cache = map.get(RedisPublisher.CODE63 + slaveId);
        if (cache != null) {
            // publisher.Publish(RedisPublisher.CODE63, slaveId, cache);
            return cache;
        }
        List<DevInstruct> instructs = new ArrayList<>();
        instructs.addAll(Push1(slaveId));
        instructs.addAll(Push2(slaveId));
        instructs.addAll(Push3(slaveId));
        instructs.addAll(Push4(slaveId));
        instructs.addAll(Push5(slaveId));
        instructs.addAll(Push6(slaveId));
        DevInstruct gx = new DevInstruct();
        gx.setSalveId(slaveId);
        gx.setCode(63);
        gx.setAddr("Update-the-total-points");
        instructs.add(gx);
        try {
            String ip = key.getCreateTCP(slaveId).getIp();
            instructs = instructs.stream().peek(in -> in.setIp(ip)).collect(Collectors.toList());
            map.put(RedisPublisher.CODE63 + slaveId, instructs);
            // publisher.Publish(RedisPublisher.CODE63, slaveId, instructs);
            return map.get(RedisPublisher.CODE63 + slaveId);
        } catch (Exception ignored) {

        }
        return null;
    }

    // 系统设置-回路分组-分组命名1 - 16
    private List<DevInstruct> Push1(int salveId) {
        List<DevInstruct> instructs = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            DevInstruct instruct = new DevInstruct();
            instruct.setSalveId(salveId);
            instruct.setCode(63);
            instruct.setAddrNum(10);
            if (i <= 9)
                instruct.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06, "系统设置-回路分组-分组命名0" + i + "_01"));
            else
                instruct.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06, "系统设置-回路分组-分组命名" + i + "_01"));
            instructs.add(instruct);
        }
        return instructs;
    }

    // 系统设置-场景设置-场景命名1 - 10 dev_config_group
    private List<DevInstruct> Push2(int salveId) {
        List<DevInstruct> instructs = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            DevInstruct instruct = new DevInstruct();
            instruct.setSalveId(salveId);
            instruct.setCode(63);
            instruct.setAddrNum(10);
            if (i <= 9)
                instruct.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06, "系统设置-场景设置-场景命名0" + i + "_01"));
            else instruct.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06, "系统设置-场景设置-场景命名10_01"));
            instructs.add(instruct);
        }
        return instructs;
    }

    // 系统设置-控制方式-普通模式-时控？-时段信息？-亮度
    private List<DevInstruct> Push3(int salveId) {
        List<DevInstruct> instructs = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            DevInstruct instruct = new DevInstruct();
            instruct.setSalveId(salveId);
            instruct.setCode(63);
            instruct.setAddrNum(73);
            instruct.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06,
                "系统设置-控制方式-普通模式-时控" + i + "-时段信息1-亮度"));
            instructs.add(instruct);
        }
        return instructs;
    }

    // 系统设置-控制方式-场景模式-时控?-时段信息1-使能
    private List<DevInstruct> Push4(int salveId) {
        List<DevInstruct> instructs = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            DevInstruct instruct = new DevInstruct();
            instruct.setSalveId(salveId);
            instruct.setCode(63);
            instruct.setAddrNum(64);
            instruct.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06,
                "系统设置-控制方式-场景模式-时控" + i + "-时段信息1-使能"));
            instructs.add(instruct);
        }
        return instructs;
    }

    private List<DevInstruct> Push5(int salveId) {
        List<DevInstruct> instructs = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            DevInstruct instruct = new DevInstruct();
            instruct.setSalveId(salveId);
            instruct.setCode(63);
            instruct.setAddrNum(33);
            instruct.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06,
                "系统设置-场景设置-场景" + i + "-分组亮度01"));
            instructs.add(instruct);
        }
        return instructs;
    }

    private List<DevInstruct> Push6(Integer salveId) {
        DevInstruct instruct1 = new DevInstruct();
        instruct1.setSalveId(salveId);
        instruct1.setCode(63);
        instruct1.setAddrNum(16);
        instruct1.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06, "系统设置-回路分组-分区编号01"));
        DevInstruct instruct2 = new DevInstruct();
        instruct2.setSalveId(salveId);
        instruct2.setCode(63);
        instruct2.setAddrNum(21);
        instruct2.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06, "系统设置-控制方式-红外模式-传感器1-感应信息-亮度"));
        DevInstruct instruct3 = new DevInstruct();
        instruct3.setSalveId(salveId);
        instruct3.setCode(63);
        instruct3.setAddrNum(35);
        instruct3.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06, "系统设置-控制方式-照度模式-传感器1-照度值"));
        DevInstruct instruct4 = new DevInstruct();
        instruct4.setSalveId(salveId);
        instruct4.setCode(63);
        instruct4.setAddrNum(2);
        instruct4.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06, "系统控制-回路控制-回路亮度"));
        DevInstruct instruct5 = new DevInstruct();
        instruct5.setSalveId(salveId);
        instruct5.setCode(63);
        instruct5.setAddrNum(49);
        instruct5.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06, "系统设置-控制方式-交流开关-开关数量"));
        DevInstruct instruct6 = new DevInstruct();
        instruct6.setSalveId(salveId);
        instruct6.setCode(63);
        instruct6.setAddrNum(3);
        instruct6.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06, "系统设置-控制方式-红外传感模式"));
        DevInstruct instruct7 = new DevInstruct();
        instruct7.setSalveId(salveId);
        instruct7.setCode(63);
        instruct7.setAddrNum(1);
        instruct7.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06, "系统设置-控制方式-普通模式-启用时控"));
        DevInstruct instruct8 = new DevInstruct();
        instruct8.setSalveId(salveId);
        instruct8.setCode(63);
        instruct8.setAddrNum(1);
        instruct8.setAddr(addrUtil.AddrByName(InstructAddrUtil.MAP06, "系统设置-控制方式-场景模式-启用时控"));
        DevInstruct instruct9 = new DevInstruct();
        instruct9.setSalveId(salveId);
        instruct9.setCode(63);
        instruct9.setAddrNum(4);
        instruct9.setAddr("0xA007");
        List<DevInstruct> l = new ArrayList<>();
        l.add(instruct1);
        l.add(instruct2);
        l.add(instruct3);
        l.add(instruct4);
        l.add(instruct5);
        l.add(instruct6);
        l.add(instruct7);
        l.add(instruct8);
        l.add(instruct9);
        return l;
    }
}

