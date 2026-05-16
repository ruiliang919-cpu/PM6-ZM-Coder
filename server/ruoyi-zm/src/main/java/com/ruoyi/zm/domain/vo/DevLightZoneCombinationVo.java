package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;



/**
 * 上位机控制照明的照明分区组合视图对象 dev_light_zone_combination
 *
 * @author ruoyi
 * @date 2024-11-29
 */
@Data
@ExcelIgnoreUnannotated
public class DevLightZoneCombinationVo {

    private static final long serialVersionUID = 1L;

    /**
     * 排序编号/系统编号
     */
    @ExcelProperty(value = "排序编号/系统编号")
    private Long orderNo;

    /**
     * 控制照明组合编号
     */
    @ExcelProperty(value = "控制照明组合编号")
    private Long zoneId;

    /**
     * 控制照明组合名称
     */
    @ExcelProperty(value = "控制照明组合名称")
    private String name;

    /**
     * 对应的分区编号集合
     */
    @ExcelProperty(value = "对应的分区编号集合")
    private String zoneList;


}
