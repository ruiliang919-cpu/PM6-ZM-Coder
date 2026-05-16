package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;

import java.math.BigDecimal;



/**
 * 直流回路视图对象 dev_status_dcc_loop
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Data
@ExcelIgnoreUnannotated
public class DevStatusDccLoopVo {

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
     * 回路编号，从协议读取
     */
    @ExcelProperty(value = "回路编号，从协议读取")
    private String no;

    /**
     * 分组编号,与config_group编号对应
     */
    @ExcelProperty(value = "分组编号,与config_group编号对应")
    private Long groupId;

    /**
     * 亮度设定值
     */
    @ExcelProperty(value = "亮度设定值")
    private Long brightnessSetting;

    /**
     * 亮度反馈值
     */
    @ExcelProperty(value = "亮度反馈值")
    private Long brightnessFeedback;

    /**
     * 输出电压
     */
    @ExcelProperty(value = "输出电压")
    private BigDecimal outputVoltage;

    /**
     * 输出电流
     */
    @ExcelProperty(value = "输出电流")
    private BigDecimal outputCurrent;

    /**
     * 内部温度（摄氏度）
     */
    @ExcelProperty(value = "内部温度", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "摄=氏度")
    private BigDecimal internalTemperature;

    /**
     * 开关设定
     */
    @ExcelProperty(value = "开关设定")
    private Integer switchSetting;

    /**
     * 开关反馈
     */
    @ExcelProperty(value = "开关反馈")
    private Integer switchFeedback;

    /**
     * 实时功率
     */
    private BigDecimal realTime;
}
