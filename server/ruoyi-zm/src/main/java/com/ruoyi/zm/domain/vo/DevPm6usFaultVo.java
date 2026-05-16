package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * PM6US开关故障记录视图对象 dev_pm6us_fault
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
@ExcelIgnoreUnannotated
public class DevPm6usFaultVo implements Serializable {


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
     * PM6US开关故障编号
     */
    @ExcelProperty(value = "PM6US开关故障编号")
    private Long switchNo;

    /**
     * 故障判定 0：正常 1：故障
     */
    @ExcelProperty(value = "故障判定 0：正常 1：故障")
    private Long faultDecide;


}
