package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;



/**
 * 交流回路视图对象 dev_status_ac_loop
 *
 * @author ruoyi
 * @date 2024-08-24
 */
@Data
@ExcelIgnoreUnannotated
public class DevStatusAcLoopVo {

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
     * 开关设定值(0 关闭 1打开)
     */
    @ExcelProperty(value = "开关设定值(0 关闭 1打开)")
    private Integer switchSetting;

    /**
     * 开关反馈值(0 关闭 1打开)
     */
    @ExcelProperty(value = "开关反馈值(0 关闭 1打开)")
    private Integer switchFeedback;

    /**
     * 整流模块输出电压
     */
    @ExcelProperty(value = "整流模块输出电压")
    private BigDecimal outputVoltage;

    /**
     * 整流模块输出电流
     */
    @ExcelProperty(value = "整流模块输出电流")
    private BigDecimal outputCurrent;

    /**
     * 分组编号,与config_group编号对应
     */
    @ExcelProperty(value = "分组编号,与config_group编号对应")
    private Long groupId;


}
