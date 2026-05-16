package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 红外传感器对象 dev_config_infrared_sensor
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@Data

@TableName("dev_config_infrared_sensor")
public class DevConfigInfraredSensor implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 系统编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 设备编号
     */
    private Integer deviceId;
    /**
     * 传感器编号
     */
    private Integer sensorId;
    /**
     * 分组信息 16位对应分组1-16
     */
    // private Integer groupId;
    /**
     * 启用 0-未启用/1-启用
     */
    private Integer enabled;
    /**
     * 感应信息亮度 100对应100%
     */
    private Integer inductiveLux;
    /**
     * 感应信息开关 0-开/1-关
     */
    private Integer inductiveSwitchStatus;
    /**
     * 未感应信息亮度 100对应100%
     */
    private Integer uninductionLux;
    /**
     * 未感应信息开关 0-开/1-关
     */
    private Integer uninductionSwitchStatus;
    /**
     * 延时 单位S
     */
    private Integer delayedTime;

}
