package com.ruoyi.schedule.util;


import com.ruoyi.cache.Key;
import com.ruoyi.zm.domain.DevInstruct;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class WriteSaveUtil {
    private final RedisTemplate<String, Object> redisTemplate;
    private final InstructDataUtil instructDataUtil;
    private final Key key;
    public static final String QUEUE_WRITE_KEY = "zm:instruct_key:write:";

    public void SaveSimple(DevInstruct instruct) {
        // try {
        SaveByWrite(instruct);
        // } catch (Exception e) {
        //     log.error("SaveWithLock ->", e);
        // }
    }

    // 通过 Redis 队列存入写入指令。
    public void SaveByWrite(@NotNull DevInstruct instruct) {
        // if (instruct.getCode() != 5 && instruct.getCode() != 6) {
        //     log.error("只允许 5 或 6 的指令可以存入队列！！");
        //     return;
        // }
        // 当报文ID不为空时，证明是发送失败的指令，并且该指令是写入指令
        if (!ObjectUtils.isEmpty(instruct.getId())) {
            // 设置报文反馈类型为在等待队列中
            instruct.setFeedback(2);
            // 将写入指令报文存入队列
            redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + instruct.getSalveId(), instruct);
            // 更新写入指令报文
            instructDataUtil.UpdateWriteInstruct(instruct);
            return;
        }
        DevBaseDeviceTCPVo tcp = key.getCreateTCP(instruct.getSalveId());
        // 设置报文IP
        instruct.setIp(tcp.getIp());
        // 设置报文ID
        instruct.setId(IdGenerator.UUIDId());
        // 设置报文反馈类型为在等待队列中
        instruct.setFeedback(2);
        // 设置报文类型为发送指令
        instruct.setType(1);
        // 设置报文时间戳
        instruct.setTimestamp(Instant.now().toEpochMilli());
        // 将报文存入队列
        redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + instruct.getSalveId(), instruct);
        // 更新提取标识
        // InstructFlag.SetFlag(InstructFlag.WRITE, instruct.getSalveId(),  Boolean.TRUE);
        // 插入写入的发送指令的报文
        instructDataUtil.InsertWriteInstruct(instruct);
    }
}
