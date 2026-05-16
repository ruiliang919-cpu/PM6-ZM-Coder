package com.ruoyi.zm.domain;


import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("dev_status_acdc")
public class StatusAcDc extends BaseEntity implements Serializable {
    /**
     * 系统编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 设备ID
     */
    private Long deviceId;
    /**
     * 回路编号
     */
    private String no;
    /**
     * 交窜直支路信息
     */
    private String acdcInfo;
    /**
     * 故障类型
     */
    private String faultType;
    /**
     * 支路位置
     */
    private String branchLocation;
    /**
     * 故障判定 0：正常 1：故障
     */
    private int fault_decide;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 运行状态
     */
    @TableField(exist = false)
    private boolean runningStatus;
    /**
     * 输出电压
     */
    @TableField(exist = false)
    private BigDecimal outputVoltage;
    /**
     * 输出电流
     */
    @TableField(exist = false)
    private BigDecimal outputCurrent;
    /**
     * 通讯状态
     */
    @TableField(exist = false)
    private boolean communicationStatus;
    /**
     * 开关机状态（AC/DC信息项显示）
     */
    @TableField(exist = false)
    private boolean onOffCondition;
}
