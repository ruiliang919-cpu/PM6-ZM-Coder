package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 分组回路编号视图对象 dev_group_loop
 *
 * @author Lion Li
 * @date 2024-08-20
 */
@Data
@ExcelIgnoreUnannotated
public class DevGroupLoopVo implements Serializable {

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
     * 分组ID
     */
    @ExcelProperty(value = "分组ID")
    private Long groupId;

    /**
     * 分组回路编号
     */
    @ExcelProperty(value = "分组回路编号")
    private Long loopNo;


}
