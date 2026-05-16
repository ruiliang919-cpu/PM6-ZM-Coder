package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 照明控制的分区控制的辅助视图对象 dev_base_district_assist
 *
 * @author ruoyi
 * @date 2024-08-31
 */
@Data
@ExcelIgnoreUnannotated
public class DevBaseDistrictAssistVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，对应控制分区主键
     */
    @ExcelProperty(value = "主键，对应控制分区主键")
    private Integer id;

    /**
     * 控制分区的亮度
     */
    @ExcelProperty(value = "控制分区的亮度")
    private Integer lux;

    /**
     * 控制分区的开关状态
     */
    @ExcelProperty(value = "控制分区的开关状态")
    private Integer switchStatus;


}
