package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;



/**
 * 时控视图对象 dev_config_infrared_sensor
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@Data
@ExcelIgnoreUnannotated
public class DevConfigInfraredSensorVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @ExcelProperty(value = "系统编号")
    private Integer id;

    /**
     * 设备编号
     */
    @ExcelProperty(value = "设备编号")
    private Integer deviceId;

    /**
     * 传感器编号
     */
    @ExcelProperty(value = "传感器编号")
    private Integer sensorId;

    /**
     * 分组信息 16位对应分组1-16
     */
    // @ExcelProperty(value = "分组信息 16位对应分组1-16")
    // private Integer groupId;

    /**
     * 启用 0-未启用/1-启用
     */
    @ExcelProperty(value = "启用 0-未启用/1-启用")
    private Integer enabled;

    /**
     * 感应信息亮度 100对应100%
     */
    @ExcelProperty(value = "感应信息亮度 100对应100%")
    private Integer inductiveLux;

    /**
     * 感应信息开关 0-开/1-关
     */
    @ExcelProperty(value = "感应信息开关 0-开/1-关")
    private Integer inductiveSwitchStatus;

    /**
     * 未感应信息亮度 100对应100%
     */
    @ExcelProperty(value = "未感应信息亮度 100对应100%")
    private Integer uninductionLux;

    /**
     * 未感应信息开关 0-开/1-关
     */
    @ExcelProperty(value = "未感应信息开关 0-开/1-关")
    private Integer uninductionSwitchStatus;

    /**
     * 延时 单位S
     */
    @ExcelProperty(value = "延时 单位S")
    private Integer delayedTime;


}
