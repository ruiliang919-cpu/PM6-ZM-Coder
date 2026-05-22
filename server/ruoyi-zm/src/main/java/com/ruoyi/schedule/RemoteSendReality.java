package com.ruoyi.schedule;

import com.ruoyi.cache.Key;
import com.ruoyi.send.RemoteSendService;
import com.ruoyi.send.TelemeterSendServiceByQueue;
import com.ruoyi.send.business.Code63Business;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.config.ModbusTCPManager;
import com.ruoyi.zm.domain.DevInstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Slf4j
// @Component
@RequiredArgsConstructor
public class RemoteSendReality {
    private final RemoteSendService remote;
    private final TelemeterSendServiceByQueue telemeter;
    private final RedisTemplate<String, Object> redisTemplate;
    // private final ExecutorService e = Executors.newFixedThreadPool(20);
    // private final ExecutorService p = Executors.newFixedThreadPool(50);
    private final DListUtil dListUtil;
    private final ModbusTCPManager tcp;
    private final Key key;
    private final Code63Business code63Business;

    // @Scheduled(fixedRate = 3000)
    public void readGx() {
        // dListUtil.Nos().forEach(no -> e.submit(() ->{
        //     int slaveId = Math.toIntExact(no);
        //     DevBaseDeviceTCPVo t = key.getCreateTCP(slaveId);
        //     if (t != null && 1 == t.getOnlineStatus()) {
        //         ModbusMaster slave = tcp.getSlave(slaveId);
        //         try {
        //             boolean[] response = ModbusTcpUtil.ReadCoilsByBool(slave, slaveId, 2817, 1);
        //             // if (slaveId == 1) {
        //             //     System.out.println("更新总点::" + response[0]);
        //             //     System.out.println("更新总点标识::" + InstructFlag.Flag(InstructFlag.TOTAL_POINT, slaveId));
        //             // }
        //             if (response[0] && InstructFlag.Flag(InstructFlag.TOTAL_POINT, slaveId)) {
        //                 InstructFlag.SetFlag(InstructFlag.TOTAL_POINT, slaveId, false);
        //                 Processor(slaveId);
        //             }
        //         } catch (Exception ignored) {
        //         }
        //     }
        // }));
    }

    public void Processor(Integer deviceId) {
        redisTemplate.opsForValue().set("zm:update:" + deviceId + ":Update-the-total-points", "false");
        telemeter.Push(deviceId);
        List<DevInstruct> push = remote.Push(deviceId);
        // if (push!=null) push.forEach(d -> p.submit(() -> code63Business.Business(d)));
    }
}
