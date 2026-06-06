package com.ruoyi.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleTask {
    private final InstructionQueue queue;
    private final TaskScheduler taskScheduler;
    private final TelecommandSendSchedule send01Schedule;
    private final TelemeterSendSchedule send03Schedule;
    private final DevBaseDeviceMapper deviceMapper;
    // 保存所有定时任务的Future对象
    private final Map<Long, ScheduledFuture<?>> scheduled01Tasks = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> scheduled03Tasks = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> scheduledWriteTasks = new ConcurrentHashMap<>();
    private final DListUtil dListUtil;

//    @Scheduled(fixedRate = 7200)
    public void sendZlNames() {
        dListUtil.Nos().forEach(no -> send03Schedule.PushZlNames(Math.toIntExact(no)));
    }

    public void ReStart() {
        Stop01Task();
        Start01Task(500);
        Stop03Task();
        Start03Task(2000);
        StopWriteTask();
        StartWriteQueueTask(100);
    }

    // 开启定时任务 01
    public void Start01Task(long delay) {
        List<DevBaseDevice> devices = deviceMapper.selectList(new LambdaQueryWrapper<DevBaseDevice>()
            .select(DevBaseDevice::getDeviceNo));
        if (!ObjectUtils.isEmpty(devices)) {
            devices.forEach(it -> {
                try {
                } catch (Exception e) {
                    log.warn("启动01定时任务异常, deviceNo={}", it.getDeviceNo(), e);
                }
            });
            // log.info("定时任务：start01Task 已启动");
        }
    }

    // 中断定时任务 01
    public void Stop01Task() {
        scheduled01Tasks.forEach((deviceNo, future) -> {
            if (future != null) {
                future.cancel(true);
                // log.info("已取消设备 {} 的定时01任务", deviceNo);
            }
        });
        scheduled01Tasks.clear();
    }

    // 开启定时任务 03
    public void Start03Task(long delay) {
        List<DevBaseDevice> devices = deviceMapper.selectList(new LambdaQueryWrapper<DevBaseDevice>()
            .select(DevBaseDevice::getDeviceNo));
        if (!ObjectUtils.isEmpty(devices)) {
            devices.forEach(it -> {
                try {
                    ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(() ->
                        send03Schedule.Processor(Math.toIntExact(it.getDeviceNo())), Duration.ofMillis(delay));
                    scheduled03Tasks.put(it.getDeviceNo(), future);
                } catch (Exception e) {
                    log.warn("启动03定时任务异常, deviceNo={}", it.getDeviceNo(), e);
                }
            });
            // log.info("定时任务：start03Task 已启动");
        }
    }

    // 中断定时任务 01
    public void Stop03Task() {
        scheduled03Tasks.forEach((deviceNo, future) -> {
            if (future != null) {
                future.cancel(true);
                // log.info("已取消设备 {} 的定时03任务", deviceNo);
            }
        });
        scheduled03Tasks.clear();
    }

    // 开启定时任务 取出写入队列指令
    public void StartWriteQueueTask(long delay) {
        List<DevBaseDevice> devices = deviceMapper.selectList(new LambdaQueryWrapper<DevBaseDevice>()
            .select(DevBaseDevice::getDeviceNo));
        if (!ObjectUtils.isEmpty(devices))
            devices.forEach(it -> {
                ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(() ->
                    queue.AfterPopWrite(it.getDeviceNo()), Duration.ofMillis(delay));
                scheduledWriteTasks.put(it.getDeviceNo(), future);
            });
        // log.info("定时任务：StartWriteQueueTask 已启动");
    }

    // 中断定时任务 写入
    public void StopWriteTask() {
        scheduledWriteTasks.forEach((deviceNo, future) -> {
            if (future != null) {
                future.cancel(true);
                // log.info("已取消设备 {} 的定时写入任务", deviceNo);
            }
        });
        scheduledWriteTasks.clear();
    }
}
