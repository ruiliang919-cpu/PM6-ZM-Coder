package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;

import java.io.Serializable;



/**
 * 普通模式-时控配置视图对象 dev_config_time_control
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@Data
@ExcelIgnoreUnannotated
public class DevConfigTimeControlVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @ExcelProperty(value = "系统编号")
    private Long id;

    /**
     * 设备编号
     */
    @ExcelProperty(value = "设备编号")
    private Long deviceId;

    /**
     * 时控编号
     */
    @ExcelProperty(value = "时控编号")
    private Integer timeControlId;

    /**
     * 时段信息编号
     */
    @ExcelProperty(value = "时段信息编号")
    private Integer timeFrameId;

    /**
     * 亮度，100对应100%
     */
    @ExcelProperty(value = "亮度，100对应100%")
    private Integer lux;

    /**
     * 开关 0-开/1-关
     */
    @ExcelProperty(value = "开关 0-开/1-关")
    private Integer switchStatus;

    /**
     * 使能（启用状态）；0-使能（启用）/1-禁止
     */
    @ExcelProperty(value = "使能", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "启=用状态")
    private Integer enabledStatus;

    /**
     * 开始时间 ASCII字符串长度6个字节，示例：”12:00“
     */
    @ExcelProperty(value = "开始时间 ASCII字符串长度6个字节，示例：”12:00“")
    private String stime;

    /**
     * 结束时间 ASCII字符串长度6个字节，示例：”12:00“
     */
    @ExcelProperty(value = "结束时间 ASCII字符串长度6个字节，示例：”12:00“")
    private String etime;


}
