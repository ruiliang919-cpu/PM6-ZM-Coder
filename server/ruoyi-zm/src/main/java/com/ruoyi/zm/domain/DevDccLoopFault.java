package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 直流回路故障记录对象 dev_dcc_loop_fault
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
@TableName("dev_dcc_loop_fault")
public class DevDccLoopFault implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private Long no;

    /**
     * 直流回路过压 0：正常 1：故障
     */
    private Long dccOvervoltage;

    /**
     * 直流回路欠压
     */
    private Long dccUndervoltage;

    /**
     * 直流回路过流
     */
    private Long dccOvercurrent;

    /**
     * 直流回路过温
     */
    private Long dccOvertemperature;

    /**
     * 直流回路短路
     */
    private Long dccShortcircuit;


}
