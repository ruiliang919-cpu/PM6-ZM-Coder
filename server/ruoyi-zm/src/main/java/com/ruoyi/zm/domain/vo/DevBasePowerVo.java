package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 机柜耗电量 功率 视图对象 dev_base_power
 *
 * @author ruoyi
 * @date 2024-08-29
 */
@Data
@ExcelIgnoreUnannotated
public class DevBasePowerVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Long id;

    /**
     * 设备ID
     */
    @ExcelProperty(value = "设备ID")
    private Integer deviceId;

    /**
     * 类型 1:总电量 2:日耗电量 3:周耗电量 4:月耗电量 5:季耗电量 6:年耗电量 7:总功率
     */
    @ExcelProperty(value = "类型 1:总电量 2:日耗电量 3:周耗电量 4:月耗电量 5:季耗电量 6:年耗电量 7:总功率")
    private Integer type;

    /**
     * 数值
     */
    @ExcelProperty(value = "数值")
    private BigDecimal value;

    /**
     * 时间戳
     */
    @ExcelProperty(value = "时间戳")
    private Long timestamp;

    @TableField(exist = false)
    private BigDecimal power;
}
