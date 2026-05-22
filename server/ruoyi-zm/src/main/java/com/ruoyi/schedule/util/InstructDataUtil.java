package com.ruoyi.schedule.util;

import com.ruoyi.zm.domain.DevInstruct;
import com.ruoyi.zm.domain.DevWriteInstruct;
import com.ruoyi.zm.mapper.DevWriteInstructMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstructDataUtil {
    private final DevWriteInstructMapper writeInstructMapper;

    // 插入写入指令的发送报文信息
    @Async
    public void InsertWriteInstruct(DevInstruct write) {
        try {
            DevWriteInstruct writeInstruct = new DevWriteInstruct();
            BeanUtils.copyProperties(write, writeInstruct);
            if (writeInstructMapper.insert(writeInstruct) < 0)
                log.error("插入报文失败（写入的发送指令），报文信息：{}", writeInstruct);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // 更新写入报文的信息
    @Async
    public void UpdateWriteInstruct(DevInstruct write) {
        try {
            DevWriteInstruct writeInstruct = new DevWriteInstruct();
            BeanUtils.copyProperties(write, writeInstruct);
            if (writeInstructMapper.updateById(writeInstruct) < 0)
                log.error("更新报文失败（写入的发送指令），报文信息：{}", writeInstruct);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
