package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 支路信息记录视图对象 dev_status_branch
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
@ExcelIgnoreUnannotated
public class DevStatusBranchVo implements Serializable {

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
     * 支路编号
     */
    @ExcelProperty(value = "支路编号")
    private Long branchNo;

    /**
     * 支路开关状态 0：关 1：开
     */
    @ExcelProperty(value = "支路开关状态 0：关 1：开")
    private Long switchStatus;

    /**
     * 支路开关故障 0：正常 1：故障
     */
    @ExcelProperty(value = "支路开关故障 0：正常 1：故障")
    private Long switchFault;


}
