package com.ruoyi.zm.service;

import com.ruoyi.zm.domain.DevStatusAcLoop;
import com.ruoyi.zm.domain.DevStatusDccLoop;

import java.util.List;

// 回路状态业务层接口
public interface LoopStatusService {
    // 构建直流回路状态列表
    // salveId：设备ID
    List<DevStatusDccLoop> getDccStatusList(Long salveId);

    // 构建交流回路状态列表
    List<DevStatusAcLoop> getAcStatusList(Long salveId);
}
