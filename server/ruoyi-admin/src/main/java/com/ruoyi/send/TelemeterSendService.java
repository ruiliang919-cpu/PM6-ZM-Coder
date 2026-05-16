package com.ruoyi.send;

import com.ruoyi.cache.Key;
import com.ruoyi.pubsub.RedisPublisher;
import com.ruoyi.schedule.util.InstructAddrUtil;
import com.ruoyi.zm.domain.DevInstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

// 遥测发送指令到队列接口
@Slf4j
@Service
@RequiredArgsConstructor
public class TelemeterSendService {
    private final InstructAddrUtil addrUtil;
    private final RedisPublisher publisher;
    private final Key key;
    private final Map<String, List<DevInstruct>> map = new HashMap<>();
    private final Map<String, List<DevInstruct>> powerMap = new HashMap<>();

    public void Push(int slaveId, String ip) {
        List<DevInstruct> cache = map.get(RedisPublisher.CODE03 + slaveId);
        if (cache != null) {
            publisher.Publish(RedisPublisher.CODE03, slaveId, cache);
            return;
        }
        List<DevInstruct> instructs = AllInstructs(slaveId);
        if (!instructs.isEmpty()) {
            instructs = instructs.stream().peek(in -> in.setIp(ip)).collect(Collectors.toList());
            map.put(RedisPublisher.CODE03 + slaveId, instructs);
            publisher.Publish(RedisPublisher.CODE03, slaveId, instructs);
        }
    }

    public void PushZlNames(int slaveId, String ip) {
        List<DevInstruct> cache = map.get("ZlNames" + slaveId);
        if (cache != null) {
            publisher.Publish(RedisPublisher.CODE03, slaveId, cache);
            return;
        }
        List<DevInstruct> instructs = sendZlNames(slaveId);
        if (!instructs.isEmpty()) {
            instructs = instructs.stream().peek(in -> in.setIp(ip)).collect(Collectors.toList());
            map.put("ZlNames" + slaveId, instructs);
            publisher.Publish(RedisPublisher.CODE03, slaveId, instructs);
        }
    }

    public void PushPower(int slaveId) {
        List<DevInstruct> cache = powerMap.get(RedisPublisher.POWER + slaveId);
        if (cache != null) {
            publisher.Publish(RedisPublisher.POWER, slaveId, cache);
            return;
        }
        List<DevInstruct> instructs = Power(slaveId);
        if (!instructs.isEmpty()) {
            try {
                String ip = key.getCreateTCP(slaveId).getIp();
                instructs = instructs.stream().peek(in -> in.setIp(ip)).filter(d -> !(d.getAddr().equals("0x0000") && d.getAddrNum() == 80)).collect(Collectors.toList());
                powerMap.put(RedisPublisher.POWER + slaveId, instructs);
                publisher.Publish(RedisPublisher.POWER, slaveId, instructs);
            } catch (Exception e) {
                // log.error("PushPower", e);
            }
        }
    }

    private List<DevInstruct> AllInstructs(int salveId) {
        List<DevInstruct> result = new ArrayList<>();
        result.addAll(buildByContains(salveId, 1,
            "交流一路", "交流二路", "母线", "环境温度", "数量", "交流输入路数",
            "交流电流显示", "交流采样模式"));
        result.addAll(buildByContains(salveId, 10, "主监控版本_01", "照度传感器外控通道01"));
        result.addAll(buildByContains(salveId, 8, "DC/DC模块输出电压01", "DC/DC模块输出电流01"));
        result.addAll(buildByContains(salveId, 5, "整流模块输出电压17", "整流模块输出电流17"));
        result.addAll(buildByContains(salveId, 16,
            "整流模块输出电压01", "整流模块输出电流01", "系统设置 -回路分组-分组选择_01"));
        result.addAll(buildByContains(salveId, 40,
            "直流回路设定亮度01", "直流回路反馈亮度01", "直流回路输出电压01",
            "直流回路输出电流01", "直流回路内部温度01"));
        result.addAll(buildByLoop(salveId, 40, 16,
            "系统设置 -回路分组-分组回路编号%s_01"));
        result.addAll(Arrays.asList(
            createSpecial(salveId, "0x000E", 1),
            createSpecial(salveId, "0x000F", 1),
            createSpecial(salveId, "0x0010", 1),
            createSpecial(salveId, "0x0011", 1),
            createSpecial(salveId, "0x0012", 1),
            createSpecial(salveId, "0x0013", 1),
            createSpecial(salveId, "0x0014", 1),
            createSpecial(salveId, "0x0015", 1),
            createSpecial(salveId, "0x0016", 1),
            createSpecial(salveId, "0x0017", 1),
            createSpecial(salveId, "0x0018", 1),
            createSpecial(salveId, "0x0019", 1),
            createSpecial(salveId, "0x001A", 1),
            createSpecial(salveId, "0x001B", 1),
            createSpecial(salveId, "0x001C", 1),
            createSpecial(salveId, "0x001D", 1),
            createSpecial(salveId, "0x001E", 1),
            createSpecial(salveId, "0x001F", 1),
            createSpecial(salveId, "0x0020", 1),
            createSpecial(salveId, "0x0021", 1),
            createSpecial(salveId, "0x0022", 1),
            createSpecial(salveId, "0x0023", 1),
            createSpecial(salveId, "0x0024", 1),
            createSpecial(salveId, "0x0025", 1),
            createSpecial(salveId, "0x0026", 1),
            createSpecial(salveId, "0x0027", 1),
            createSpecial(salveId, "0x0028", 1),
            createSpecial(salveId, "0x0029", 1),
            createSpecial(salveId, "0x002A", 1),
            createSpecial(salveId, "0x2700", 64),
            createSpecial(salveId, "0x2740", 1)
        ));
        return result;
    }

    public List<DevInstruct> sendZlNames(int salveId) {
        return new ArrayList<>(buildByLoop(salveId, 10, 64, "馈线支路名称%s_01"));
    }

    private List<DevInstruct> buildByContains(int salveId, int addrNum, String... keywords) {
        return addrUtil.AddrByContains(InstructAddrUtil.MAP03, keywords)
            .stream()
            .map(a -> createInstruct(salveId, a, addrNum))
            .collect(Collectors.toList());
    }

    private List<DevInstruct> buildByLoop(int salveId, int addrNum, int count, String namePattern) {
        List<DevInstruct> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String formatted = String.format(namePattern, i <= 9 ? "0" + i : i);
            String addr = addrUtil.AddrByName(InstructAddrUtil.MAP03, formatted);
            list.add(createInstruct(salveId, addr, addrNum));
        }
        return list;
    }

    private DevInstruct createSpecial(int salveId, String addr, int addrNum) {
        DevInstruct instruct = new DevInstruct();
        instruct.setSalveId(salveId);
        instruct.setCode(3);
        instruct.setAddr(addr);
        instruct.setAddrNum(addrNum);
        return instruct;
    }

    private DevInstruct createInstruct(int salveId, String addr, int addrNum) {
        DevInstruct instruct = new DevInstruct();
        instruct.setSalveId(salveId);
        instruct.setCode(3);
        instruct.setAddr(addr);
        instruct.setAddrNum(addrNum);
        return instruct;
    }

    private List<DevInstruct> Power(int salveId) {
        List<DevInstruct> c = addrUtil.AddrByContains(InstructAddrUtil.MAP03,
            "1#电能表总电量", "1#电表日耗电量", "1#电表周耗电量", "1#电表月耗电量", "1#电表季耗电量", "1#电表年耗电量"
        ).stream().map(a -> {
            DevInstruct instruct = new DevInstruct();
            instruct.setSalveId(salveId);
            instruct.setCode(3);
            instruct.setAddr(a);
            instruct.setAddrNum(80);
            return instruct;
        }).collect(Collectors.toList());
        DevInstruct i = new DevInstruct();
        i.setSalveId(salveId);
        i.setCode(3);
        i.setAddr("0x0000");
        i.setAddrNum(14);
        c.add(i);
        return c;
    }
}
