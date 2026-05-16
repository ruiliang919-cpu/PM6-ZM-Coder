package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;



/**
 * 系统设置-控制方式-交流开关-时段信息 视图对象 dev_config_time_control_ac
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@Data
@ExcelIgnoreUnannotated
public class DevConfigTimeControlAcVo implements Serializable {

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
     * 时段信息ID
     */
    @ExcelProperty(value = "时段信息ID")
    private Integer frameId;

    /**
     * 开关
     */
    @ExcelProperty(value = "开关")
    private Integer switchStatus;

    /**
     * 使能
     */
    @ExcelProperty(value = "使能")
    private Integer enable;

    /**
     * 开始时间_时
     */
    @ExcelProperty(value = "开始时间_时")
    private Integer stimeHour;

    /**
     * 开始时间_分
     */
    @ExcelProperty(value = "开始时间_分")
    private Integer stimeMin;

    /**
     * 结束时间_时
     */
    @ExcelProperty(value = "结束时间_时")
    private Integer etimeHour;

    /**
     * 结束时间_分
     */
    @ExcelProperty(value = "结束时间_分")
    private Integer etimeMin;


}
