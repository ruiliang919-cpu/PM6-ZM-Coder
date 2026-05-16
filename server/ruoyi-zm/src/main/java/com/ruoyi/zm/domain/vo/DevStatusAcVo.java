package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;



/**
 * 交流信息视图对象 dev_status_ac
 *
 * @author ruoyi
 * @date 2024-09-19
 */
@Data
@ExcelIgnoreUnannotated
public class DevStatusAcVo {

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
     * 电路数：1路；2路
     */
    @ExcelProperty(value = "电路数：1路；2路")
    private Long circuitNum;

    /**
     * AB线电压
     */
    @ExcelProperty(value = "AB线电压")
    private BigDecimal uab;

    /**
     * BC线电压
     */
    @ExcelProperty(value = "BC线电压")
    private BigDecimal ubc;

    /**
     * CA线电压
     */
    @ExcelProperty(value = "CA线电压")
    private BigDecimal uac;

    /**
     * A相电流
     */
    @ExcelProperty(value = "A相电流")
    private BigDecimal ia;

    /**
     * B相电流
     */
    @ExcelProperty(value = "B相电流")
    private BigDecimal ib;

    /**
     * C相电流
     */
    @ExcelProperty(value = "C相电流")
    private BigDecimal ic;

    /**
     * 工作状态
     */
    private String workStatus;


}
