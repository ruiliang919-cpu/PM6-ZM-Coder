package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;



/**
 * 设备区域视图对象 dev_base_region
 *
 * @author ruoyi
 * @date 2024-07-11
 */
@Data
@ExcelIgnoreUnannotated
public class DevBaseRegionVo {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @ExcelProperty(value = "系统编号")
    private Long id;

    /**
     * 区域名称
     */
    @ExcelProperty(value = "区域名称")
    private String name;

    /**
     * 排序字段
     */
    private Integer orderNo;
}
