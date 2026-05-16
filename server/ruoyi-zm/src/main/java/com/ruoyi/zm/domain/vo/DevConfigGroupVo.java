package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;



/**
 * 设备视图对象 dev_config_group
 *
 * @author ruoyi
 * @date 2024-08-26
 */
@Data
@ExcelIgnoreUnannotated
public class DevConfigGroupVo {

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
     * 名称
     */
    @ExcelProperty(value = "名称")
    private String name;

    /**
     * 回路数
     */
    @ExcelProperty(value = "回路数")
    private Long loopNum;

    /**
     * 分区ID
     */
    @ExcelProperty(value = "分区ID")
    private Integer districtAddr;

    /**
     * 分组亮度，用于机柜控制-照明控制-分组控制
     */
    @ExcelProperty(value = "分组亮度，用于机柜控制-照明控制-分组控制")
    private Integer lux;


    /**
     * 调光控制列表的选中状态 0不选中 1选中  null 未选中
     */
    private Integer selectStatus;
}
