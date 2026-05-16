package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;



/**
 * 交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应视图对象 dev_device_remote_read
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@Data
@ExcelIgnoreUnannotated
public class DevDeviceRemoteReadVo implements Serializable {

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
     * 系统设置-控制方式-交流开关-开关数量 0-8
     */
    @ExcelProperty(value = "系统设置-控制方式-交流开关-开关数量 0-8")
    private Integer acSwitchNum;

    /**
     * 系统控制-回路控制-回路亮度
     */
    @ExcelProperty(value = "系统控制-回路控制-回路亮度")
    private Integer loopLux;

    /**
     * 系统控制-分组控制-分组亮度
     */
    @ExcelProperty(value = "系统控制-分组控制-分组亮度")
    private Integer groupLux;

    /**
     * 系统设置-控制方式-红外传感模式 0-关闭/1-开启
     */
    @ExcelProperty(value = "系统设置-控制方式-红外传感模式 0-关闭/1-开启")
    private Integer infraredSensingMode;

    /**
     * 系统设置-控制方式-照度传感模式 0-关闭/1-开启
     */
    @ExcelProperty(value = "系统设置-控制方式-照度传感模式 0-关闭/1-开启")
    private Integer illuminanceSensingMode;

    /**
     * 系统控制-照明控制-手动模式选择 0-未选中/1-回路控制/2-分组控制/3-场景控制
     */
    @ExcelProperty(value = "系统控制-照明控制-手动模式选择 0-未选中/1-回路控制/2-分组控制/3-场景控制")
    private Integer manualMode;


}
