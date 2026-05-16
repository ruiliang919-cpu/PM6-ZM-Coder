package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


/**
 * 分区组合视图对象 dev_zone_combination
 *
 * @author ruoyi
 * @date 2024-10-08
 */
@Data
@ExcelIgnoreUnannotated
public class DevZoneCombinationVo {

    private static final long serialVersionUID = 1L;

    /**
     * 排序编号/系统编号
     */
    @ExcelProperty(value = "排序编号/系统编号")
    private Long orderNo;

    /**
     * 分区组合编号
     */
    @ExcelProperty(value = "分区组合编号")
    private Long zoneId;

    // 分区组合名称
    @ExcelProperty(value = "分区组合名称")
    private String name;

    /**
     * 对应的分区编号集合
     */
    @ExcelProperty(value = "对应的分区编号集合")
    private String zoneList;


}
