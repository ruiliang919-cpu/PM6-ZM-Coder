package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


/**
 * 普通时控启用视图对象 dev_base_simple_control
 *
 * @author ruoyi
 * @date 2024-09-09
 */
@Data
@ExcelIgnoreUnannotated
public class DevBaseSimpleControlVo {

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
     * 普通时控ID
     */
    @ExcelProperty(value = "普通时控ID")
    private Integer controlId;

    /**
     * 是否启用 0不启用 1已启用
     */
    @ExcelProperty(value = "是否启用 0不启用 1已启用")
    private Integer enabled;


}
