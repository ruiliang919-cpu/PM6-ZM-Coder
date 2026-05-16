package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ExcelIgnoreUnannotated
public class DevFaultRecordExcelVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "序号")
    private Integer id;

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
    private String stime;
}
