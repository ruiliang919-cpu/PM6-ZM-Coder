package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 十六电年耗电量视图对象 dev_energy_meter_year
 *
 * @author ruoyi
 * @date 2024-09-12
 */
@Data
@ExcelIgnoreUnannotated
public class DevEnergyMeterYearVo {

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
     * 年耗电量
     */
    @ExcelProperty(value = "年耗电量")
    private BigDecimal power;

    /**
     * 时间戳
     */
    @ExcelProperty(value = "时间戳")
    private Long timestamp;


}
