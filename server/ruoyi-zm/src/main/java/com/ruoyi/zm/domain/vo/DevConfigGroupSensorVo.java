package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;



/**
 * 传感器与分组对应视图对象 dev_config_group_sensor
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@Data
@ExcelIgnoreUnannotated
public class DevConfigGroupSensorVo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Integer id;

    /**
     * 设备ID
     */
    @ExcelProperty(value = "设备ID")
    private Integer deviceId;

    /**
     * 分组编号
     */
    @ExcelProperty(value = "分组编号")
    private Integer groupId;

    /**
     * 红外传感器编号
     */
    @ExcelProperty(value = "红外传感器编号")
    private Integer infraredSensorId;

    /**
     * 照度传感器编号
     */
    @ExcelProperty(value = "照度传感器编号")
    private Integer illuminanceSensorId;


}
