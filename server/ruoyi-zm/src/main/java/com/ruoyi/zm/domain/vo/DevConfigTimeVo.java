package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;



/**
 * 机柜时间设置视图对象 dev_config_time
 *
 * @author ruoyi
 * @date 2024-08-26
 */
@Data
@ExcelIgnoreUnannotated
public class DevConfigTimeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Integer id;

    /**
     * 设备通讯编号
     */
    @ExcelProperty(value = "设备通讯编号")
    private Integer deviceNo;

    /**
     * 年
     */
    @ExcelProperty(value = "年")
    private Integer year;

    /**
     * 月
     */
    @ExcelProperty(value = "月")
    private Integer month;

    /**
     * 日
     */
    @ExcelProperty(value = "日")
    private Integer day;

    /**
     * 时
     */
    @ExcelProperty(value = "时")
    private Integer hour;

    /**
     * 分
     */
    @ExcelProperty(value = "分")
    private Integer minute;

    /**
     * 秒
     */
    @ExcelProperty(value = "秒")
    private Integer second;

    /**
     * 对时标志位 0不启用 1启用
     */
    @ExcelProperty(value = "对时标志位 0不启用 1启用")
    private Integer timeSwitch;


}
