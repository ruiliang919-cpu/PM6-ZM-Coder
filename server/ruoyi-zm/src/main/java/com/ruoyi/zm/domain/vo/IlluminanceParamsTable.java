package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IlluminanceParamsTable {
    private Integer sensorId;
    // 当前照度值
    private Integer illuminanceLux;
    // 恒照值 单位Lux
    private Integer constantIlluminanceLux;
    // 回差值 单位Lux
    private Integer hysteresisLux;
    // 调节时间 单位S
    private Integer adjustmentTimeSeconds;
    // 外控使能 0-不启用外控/1-启用外控 06功能码
    private Integer externalControlEnabled;
    private Integer enabled;
    // 外控状态 0未启用；1启用
    private Integer outControlStatus;
    // 外控通道
    private Integer outControlChannel;
    // 外控地址
    private Integer outControlAddr;
}
