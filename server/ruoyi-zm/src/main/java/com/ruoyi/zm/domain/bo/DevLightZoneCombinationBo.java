package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 上位机控制照明的照明分区组合业务对象 dev_light_zone_combination
 *
 * @author ruoyi
 * @date 2024-11-29
 */

@Data

public class DevLightZoneCombinationBo {

    /**
     * 排序编号/系统编号
     */
    @NotNull(message = "排序编号/系统编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long orderNo;

    /**
     * 控制照明组合编号
     */
    @NotNull(message = "控制照明组合编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long zoneId;

    /**
     * 控制照明组合名称
     */
    @NotBlank(message = "控制照明组合名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 对应的分区编号集合
     */
    @NotBlank(message = "对应的分区编号集合不能为空", groups = { AddGroup.class, EditGroup.class })
    private String zoneList;


}
