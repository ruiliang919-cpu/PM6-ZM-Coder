package com.ruoyi.send;

import com.ruoyi.cache.Key;
import com.ruoyi.pubsub.RedisPublisher;
import com.ruoyi.schedule.util.InstructAddrUtil;
import com.ruoyi.zm.domain.DevInstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemeterSendServiceByQueue {
    private final InstructAddrUtil addrUtil;
    private final RedisPublisher publisher;

    private static final int CODE = 3;
    private static final int TYPE = 7;
    private static final int GROUP_SELECT_ADDR_NUM = 16;
    private static final int GROUP_LOOP_ADDR_NUM = 40;
    private static final String MAP03 = InstructAddrUtil.MAP03;
    private static final String GROUP_SELECT_PREFIX = "系统设置 -回路分组-分组选择_01";
    private static final String GROUP_LOOP_PREFIX = "系统设置 -回路分组-分组回路编号";
    private static final String GROUP_LOOP_SUFFIX = "_01";
    private static final String KEY = "CODE03QUEUE:";
    private final Map<String, List<DevInstruct>> map = new HashMap<>();
    private final Key key;

    // 推送遥测指令到消息队列
    public void Push(int slaveId) {
        List<DevInstruct> cache = map.get(KEY + slaveId);
        if (cache != null) {
            publisher.Publish(RedisPublisher.CODE03, slaveId, cache);
            return;
        }
        List<DevInstruct> instructions = new ArrayList<>();
        // 添加分组选择指令
        instructions.add(createBaseInstruct(slaveId, GROUP_SELECT_PREFIX, GROUP_SELECT_ADDR_NUM));

        // 添加分组回路编号指令（1-9）
        for (int i = 1; i < 10; i++) addGroupLoopInstruct(instructions, slaveId, String.format("%02d", i));

        // 添加分组回路编号指令（10-16）
        for (int i = 10; i <= 16; i++) addGroupLoopInstruct(instructions, slaveId, String.valueOf(i));

        // 批量发布指令
        if (!instructions.isEmpty()) {
            try {
                String ip = key.getCreateTCP(slaveId).getIp();
                instructions = instructions.stream().peek(in -> in.setIp(ip)).collect(Collectors.toList());
                map.put(KEY + slaveId, instructions);
                publisher.Publish(RedisPublisher.CODE03, slaveId, instructions);
            } catch (Exception e) {
                log.error("TelemeterSendServiceByQueue → Push", e);
            }
        }
    }

    // 创建基础指令对象
    private DevInstruct createBaseInstruct(int slaveId, String addrName, int addrNum) {
        DevInstruct instruct = new DevInstruct();
        instruct.setSalveId(slaveId);
        instruct.setCode(CODE);
        instruct.setType(TYPE);
        String s = addrUtil.AddrByName(MAP03, addrName);
        if (s.equals("<None>")) System.out.println("<None>::" + addrName);
        instruct.setAddr(s);
        instruct.setAddrNum(addrNum);
        return instruct;
    }

    // 添加分组回路指令
    private void addGroupLoopInstruct(List<DevInstruct> instructions, int slaveId, String loopNumber) {
        String addrName = GROUP_LOOP_PREFIX + loopNumber + GROUP_LOOP_SUFFIX;
        instructions.add(createBaseInstruct(slaveId, addrName, GROUP_LOOP_ADDR_NUM));
    }
}
