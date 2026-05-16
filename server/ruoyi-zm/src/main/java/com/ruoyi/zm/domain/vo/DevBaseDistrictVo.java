package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;



/**
 * 控制分区视图对象 dev_base_district
 *
 * @author mophi
 * @date 2024-07-11
 */
@Data
@ExcelIgnoreUnannotated
public class DevBaseDistrictVo {

    private static final long serialVersionUID = 1L;

    /**
     * 分区码
     */
    @ExcelProperty(value = "分区码")
    private Long id;

    /**
     * 名称
     */
    @ExcelProperty(value = "名称")
    private String name;

    /**
     * 排序字段
     */
    @ExcelProperty(value = "排序字段")
    private Integer orderNo;

    private Integer lux;
    private Integer switchStatus;
}
