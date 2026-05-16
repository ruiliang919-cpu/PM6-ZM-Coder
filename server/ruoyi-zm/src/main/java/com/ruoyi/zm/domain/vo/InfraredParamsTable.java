package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class InfraredParamsTable {
    private Integer sensorId;
    // 启用状态
    private Integer enabled;
    // 感应信息亮度 100对应100%
    private Integer inductiveLux;
    // 感应信息开关 0-开/1-关
    private Integer inductiveSwitchStatus;
    // 未感应信息亮度 100对应100%
    private Integer uninductionLux;
    // 未感应信息开关 0-开/1-关
    private Integer uninductionSwitchStatus;
    // 延时 单位S
    private Integer delayedTime;
}
