package com.ruoyi.zm.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 分区组合业务对象 dev_zone_combination
 *
 * @author ruoyi
 * @date 2024-10-08
 */

@Data
public class DevZoneCombinationBo {

    /**
     * 排序编号/系统编号
     */
    @NotNull(message = "排序编号/系统编号不能为空", groups = {EditGroup.class})
    private Long orderNo;

    /**
     * 分区组合编号
     */
    @NotNull(message = "分区组合编号不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long zoneId;
    // 分区组合名称
    @ExcelProperty(value = "分区组合名称")
    private String name;

    /**
     * 对应的分区编号集合
     */
    @NotBlank(message = "对应的分区编号集合不能为空", groups = {AddGroup.class, EditGroup.class})
    private String zoneList;


}
