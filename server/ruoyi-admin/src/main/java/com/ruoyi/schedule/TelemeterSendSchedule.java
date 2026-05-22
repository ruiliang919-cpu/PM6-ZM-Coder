package com.ruoyi.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.AcDcCache;
import com.ruoyi.cache.Key;
import com.ruoyi.flag.InstructFlag;
import com.ruoyi.send.TelemeterSendService;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 遥测定时任务
@Slf4j
@Component
@RequiredArgsConstructor
public class TelemeterSendSchedule {
    private final TelemeterSendService send;
    private final DevBaseDeviceMapper deviceMapper;
    private final Key key;
    private final AcDcCache acDcCache;

    public void PushZlNames(int slaveId) {
        DevBaseDeviceTCPVo tcp = key.getCreateTCP(slaveId);
        if (tcp == null || 0 == tcp.getOnlineStatus()) return;
        if (InstructFlag.Flag(InstructFlag.READ03, slaveId)) send.PushZlNames(slaveId, tcp.getIp());
    }

    public void Processor(int slaveId) {
        DevBaseDeviceTCPVo tcp = key.getCreateTCP(slaveId);
        if (tcp == null || 0 == tcp.getOnlineStatus()) return;
        // 交流一路
        // 交流二路
        // 直流母线电压 直流母线电流 母线正对地电压 母线负对地电压
        // 母线正极绝缘阻值 母线负极绝缘阻值 母线交窜直电压
        // 环境温度
        // 交流输入路数 交流电流显示 交流采样模式
        // 整流模块输出电压
        // 整流模块输出电流
        // DC/DC模块输出电压
        // DC/DC模块输出电流
        // 直流回路设定亮度
        // 直流回路反馈亮度
        // 直流回路输出电压
        // 直流回路输出电流
        // 直流回路内部温度
        // 0X007B 直流回路实时功率
        // 直流调光回路数量 直流开关回路数量 交流回路数量
        // 其他开关数量 整流模块数量 DC/DC模块数量
        // 主监控版本
        // 照度传感器外控通道与外控地址
        // 馈线支路名称0~9
        // 馈线支路名称10~64
        // 漏电流
        // 漏电流支路数量
        if (InstructFlag.Flag(InstructFlag.READ03, slaveId)) {
            send.Push(slaveId, tcp.getIp());
            // D-3: 遥测指令推送后，通过 WebSocket 推送机柜实时数据
            acDcCache.pushCabinetData(slaveId);
        }
    }

    // 每天凌晨一次
    // @Scheduled(fixedDelay = 87000)
    public void Processor2() {
        // 总电量 总功率 日耗电量 周耗电量 月耗电量 季耗电量 年耗电量
        // 0X002B 电能表总电量
        // 0X00CB 电表日耗电量
        // 0X011B 电表周耗电量
        // 0X016B 电表月耗电量
        // 0X01BB 电表季耗电量
        // 0X020B 电表年耗电量
        deviceMapper.selectList(new LambdaQueryWrapper<DevBaseDevice>()
                .select(DevBaseDevice::getDeviceNo))
            .forEach(d -> send.PushPower(Math.toIntExact(d.getDeviceNo())));
    }
}
