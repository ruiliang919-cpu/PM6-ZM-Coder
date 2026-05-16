package com.ruoyi.init;

import com.ruoyi.schedule.ScheduleTask;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.web.controller.zm.WriteController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class TaskInit {
    @Resource
    public ScheduleTask task;
    @Resource
    private WriteController w;
    @Resource
    private DListUtil dListUtil;

    public void startTask() {
        task.ReStart();
    }

    // 处理时间
    @Scheduled(fixedDelay = 3600000)
    public void checkTime() {
        dListUtil.Nos().forEach(no -> {
            w.timeSaveNow(Math.toIntExact(no));
        });
    }
}
