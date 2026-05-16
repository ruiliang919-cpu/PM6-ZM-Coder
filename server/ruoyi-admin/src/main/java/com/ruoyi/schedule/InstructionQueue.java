package com.ruoyi.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 指令队列
 * 写入指令单独存放在一张表中，避免连接丢失（设备离线）的风险，连接成功后重新发送。
 * 遥调和遥控的写入指令需要存入到报文中。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InstructionQueue {
    public static final String QUEUE_WRITE_KEY = "zm:instruct_key:write:";
    // private final RedisTemplate<String, Object> redisTemplate;
    // private final ModbusTCPManager masterTcp;
    // private final WriteBusinessService writeBusinessService;
    // private final Key key;
    // private final ExecutorService p = Executors.newFixedThreadPool(30);

    public void AfterPopWrite(Long no) {
        // p.submit(() -> AfterPopWriteMain(no));
    }

    public void AfterPopWriteMain(Long no) {
        // int slaveId = Math.toIntExact(no);
        // DevBaseDeviceTCPVo tcp = key.getCreateTCP(slaveId);
        // if (tcp != null && 1 == tcp.getOnlineStatus()) {
        //     // Redis 3.x 不支持此特性！！
        //     List<Object> instructs = redisTemplate.opsForList().leftPop(QUEUE_WRITE_KEY + slaveId, 20);
        //     if (!ObjectUtils.isEmpty(instructs)) {
        //         List<DevInstruct> collect = instructs.stream().map(i -> {
        //             if (!ObjectUtils.isEmpty(i) && i instanceof DevInstruct) return (DevInstruct) i;
        //             return null;
        //         }).filter(Objects::nonNull).collect(Collectors.toList());
        //         writeBusinessService.Business(collect, masterTcp.getSlave(slaveId), slaveId);
        //     }
        // }
    }
}
