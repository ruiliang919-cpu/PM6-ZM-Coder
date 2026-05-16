package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IlluminanceSensorParamsRespVo {
    // 传感器编号
    private Integer sensorId;
    // 启用 0-未启用/1-启用
    private Integer enabled;
    // 当前照度值
    private Integer illuminanceLux;
    // 恒照值 单位Lux
    private Integer constantIlluminanceLux;
    // 回差值 单位Lux
    private Integer hysteresisLux;
    // 调节时间 单位S
    private Integer adjustmentTimeSeconds;
    // 外控使能 0-不启用外控/1-启用外控 06功能码（外部控制大概是这个）
    private Integer externalControlEnabled;
    // 外控状态 0未启用；1启用  01功能码（暂时应该不需要使用）
    private Integer outControlStatus;
    // 外控通道
    private BigDecimal outControlChannel;
    // 外控地址
    private BigDecimal outControlAddr;
}
