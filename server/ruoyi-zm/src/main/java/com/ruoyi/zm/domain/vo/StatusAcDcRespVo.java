package com.ruoyi.zm.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusAcDcRespVo {
    /**
     * 回路编号
     */
    private String no;
    /**
     * 运行状态
     */
    private boolean runningStatus;
    /**
     * 输出电压
     */
    private String outputVoltage;
    /**
     * 输出电流
     */
    private String outputCurrent;
    /**
     * 通讯状态
     */
    private boolean communicationStatus;
    /**
     * 开关机状态（AC/DC信息项显示）
     */
    private boolean onOffCondition;
}
