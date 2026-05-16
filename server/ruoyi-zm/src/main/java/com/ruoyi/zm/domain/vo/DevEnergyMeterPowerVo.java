package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 十六电总功率视图对象 dev_energy_meter_power
 *
 * @author ruoyi
 * @date 2024-09-12
 */
@Data
@ExcelIgnoreUnannotated
public class DevEnergyMeterPowerVo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Integer id;

    /**
     * 设备编号
     */
    @ExcelProperty(value = "设备编号")
    private Integer deviceNo;

    /**
     * 电能表编号
     */
    @ExcelProperty(value = "电能表编号")
    private Integer energyMeterNo;

    /**
     * 功率
     */
    @ExcelProperty(value = "功率")
    private BigDecimal power;


}
