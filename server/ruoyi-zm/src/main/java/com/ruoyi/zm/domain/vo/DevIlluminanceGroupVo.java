package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 照度模式传感器与分组选择视图对象 dev_illuminance_group
 *
 * @author ruoyi
 * @date 2024-09-04
 */
@Data
@ExcelIgnoreUnannotated
public class DevIlluminanceGroupVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Integer id;

    /**
     * 设备ID
     */
    @ExcelProperty(value = "设备ID")
    private Integer deviceId;

    /**
     * 照度模式传感器ID
     */
    @ExcelProperty(value = "照度模式传感器ID")
    private Integer sensorId;

    /**
     * 分组ID
     */
    @ExcelProperty(value = "分组ID")
    private Integer groupId;

    /**
     * 分组名称
     */
    @ExcelProperty(value = "分组名称")
    private String groupName;

    /**
     * 是否选中分组 0未选中 1已选中
     */
    @ExcelProperty(value = "是否选中分组 0未选中 1已选中")
    private Integer selectStatus;


}
