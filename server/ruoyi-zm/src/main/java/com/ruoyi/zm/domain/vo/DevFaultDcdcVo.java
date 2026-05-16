package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;



/**
 * DC/DC故障信息视图对象 dev_fault_dcdc
 *
 * @author ruoyi
 * @date 2024-08-24
 */
@Data
@ExcelIgnoreUnannotated
public class DevFaultDcdcVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @ExcelProperty(value = "系统编号")
    private Long id;

    /**
     * 设备ID
     */
    @ExcelProperty(value = "设备ID")
    private Integer deviceId;

    /**
     * DCDC模块（通讯）故障编号
     */
    @ExcelProperty(value = "DCDC模块", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "通=讯")
    private Integer no;

    /**
     * 模块故障判定，即运行状态（0正常 1故障）
     */
    @ExcelProperty(value = "模块故障判定，即运行状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=正常,1=故障")
    private Integer moduleType;

    /**
     * 通讯模块故障判定，即通讯状态（0正常 1故障）
     */
    @ExcelProperty(value = "通讯模块故障判定，即通讯状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=正常,1=故障")
    private Integer onlineType;

    /**
     * DCDC模块输出电压
     */
    @ExcelProperty(value = "DCDC模块输出电压")
    private BigDecimal voltage;

    /**
     * DCDC模块输出电流
     */
    @ExcelProperty(value = "DCDC模块输出电流")
    private BigDecimal electricity;


}
