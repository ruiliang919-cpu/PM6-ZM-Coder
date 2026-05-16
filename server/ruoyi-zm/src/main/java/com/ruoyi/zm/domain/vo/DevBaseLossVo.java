package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 总功率视图对象 dev_base_loss
 *
 * @author ruoyi
 * @date 2024-08-29
 */
@Data
@ExcelIgnoreUnannotated
public class DevBaseLossVo implements Serializable {

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
     * 总功率
     */
    @ExcelProperty(value = "总功率")
    private BigDecimal loss;


}
