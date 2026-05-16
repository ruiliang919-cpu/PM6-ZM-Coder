package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;

import java.math.BigDecimal;



/**
 * 直流信息视图对象 dev_status_dcc
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Data
@ExcelIgnoreUnannotated
public class DevStatusDccVo {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @ExcelProperty(value = "系统编号")
    private Long id;

    /**
     * 设备id
     */
    @ExcelProperty(value = "设备id")
    private Long deviceId;

    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    private String no;

    /**
     * 运行状态（0故障; 1正常）
     */
    @ExcelProperty(value = "运行状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=故障;,1=正常")
    private Integer runStatus;

    /**
     * 通信状态（0故障; 1正常）
     */
    @ExcelProperty(value = "通信状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=故障;,1=正常")
    private Integer onlineStatus;

    /**
     * 开关机状态（0关; 1开）
     */
    @ExcelProperty(value = "开关机状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=关;,1=开")
    private Integer onOffStatus;



    /**
     * 输出电压
     */@ExcelProperty(value = "输出电压")
    private BigDecimal outputVoltage;
    /**
     * 输出电流
     */    @ExcelProperty(value = "输出电流")
    private BigDecimal outputCurrent;

}
