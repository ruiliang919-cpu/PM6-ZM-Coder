package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 馈线支路名称视图对象 dev_bash_branch
 *
 * @author Lion Li
 * @date 2024-08-20
 */
@Data
@ExcelIgnoreUnannotated
public class DevBaseBranchVo implements Serializable {

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
     * 馈线支路编号
     */
    @ExcelProperty(value = "馈线支路编号")
    private Long branchNo;

    /**
     * 馈线支路名称
     */
    @ExcelProperty(value = "馈线支路名称")
    private String branchName;


}
