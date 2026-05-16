package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 告警记录视图对象 dev_fault_record
 *
 * @author ruoyi
 * @date 2024-08-29
 */
@Data
@ExcelIgnoreUnannotated
public class DevFaultRecordVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "序号")
    private Integer id;

    /**
     * 设备编号 主机号
     */
    // @ExcelProperty(value = "设备编号 主机号")
    private Integer deviceId;

    /**
     * 设备名称
     */
    @ExcelProperty(value = "设备名称")
    private String name;

    /**
     * 告警信息
     */
    @ExcelProperty(value = "事件/告警信息")
    private String message;

    /**
     * 开始时间
     */
    @ExcelProperty(value = "开始时间")
    private Long stime;

    /**
     * 是否显示 0不显示 1显示
     */
    // @ExcelProperty(value = "是否显示 0不显示 1显示")
    // private Integer showType;


}
