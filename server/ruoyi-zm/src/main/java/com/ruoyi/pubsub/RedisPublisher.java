package com.ruoyi.pubsub;

import com.ruoyi.zm.domain.DevInstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisPublisher {
    public static final String CODE03 = "Code03";
    public static final String CODE63 = "Code63";
    public static final String Recover = "Recover";
    public static final String Expired = "__keyevent@0__:expired";
    public static final String POWER = "Power";

    public void Publish(String type, int slaveId, List<DevInstruct> instructs) {
        // redisTemplate.convertAndSend(type, slaveId + "~" + JSONUtil.toJsonStr(instructs));
    }
}
