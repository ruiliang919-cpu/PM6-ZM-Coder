package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * AC/DC故障信息对象 dev_fault_acdc
 *
 * @author ruoyi
 * @date 2024-08-24
 */
@Data

@TableName("dev_fault_acdc")
public class DevFaultAcdc implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 系统编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 设备ID
     */
    private Integer deviceId;
    /**
     * 整流模块（通讯）故障编号
     */
    private Integer no;
    /**
     * 模块故障判定，即运行状态（0正常 1故障）
     */
    private Integer moduleType;
    /**
     * 通讯模块故障判定，即通讯状态（0正常 1故障）
     */
    private Integer onlineType;
    /**
     * 整流模块输出电压
     */
    private BigDecimal voltage;
    /**
     * 整流模块输出电流
     */
    private BigDecimal electricity;
    /**
     * 整流模块开关机状态（0关 1开）
     */
    private Integer switchStatus;

}
