package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 普通时控模式与分组配置视图对象 dev_config_simple_group
 *
 * @author ruoyi
 * @date 2024-09-03
 */
@Data
@ExcelIgnoreUnannotated
public class DevConfigSimpleGroupVo implements Serializable {

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
     * 普通模式时控ID
     */
    @ExcelProperty(value = "普通模式时控ID")
    private Integer simpleId;

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
