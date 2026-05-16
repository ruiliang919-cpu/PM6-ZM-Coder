package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 调光版故障记录对象 dev_dimmer_plate_fault
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
@TableName("dev_dimmer_plate_fault")
public class DevDimmerPlateFault implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备编号
     */
    private Long deviceId;

    /**
     * 调光模块通讯故障编号
     */
    private Long moduleNo;

    /**
     * 故障判定 0：正常 1：故障
     */
    private Long faultDecide;


}
