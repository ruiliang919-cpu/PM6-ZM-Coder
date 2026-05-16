package com.ruoyi.zm.service;

import com.ruoyi.zm.domain.StatusAcDc;

import java.util.List;

// 直流机柜-机柜信息业务层接口
public interface CabinetInfomationService {
    // 构建AC/DC的运行状态、输出电压、输出电流、通讯状态、开关机状态列表
    // slaveId：设备ID
    List<StatusAcDc> getList(int slaveId);
}
