package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 调光版故障记录视图对象 dev_dimmer_plate_fault
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
@ExcelIgnoreUnannotated
public class DevDimmerPlateFaultVo implements Serializable {


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
     * 调光模块编号
     */
    @ExcelProperty(value = "调光模块编号")
    private Long moduleNo;

    /**
     * 故障判定 0：正常 1：故障
     */
    @ExcelProperty(value = "故障判定 0：正常 1：故障")
    private Long faultDecide;


}
