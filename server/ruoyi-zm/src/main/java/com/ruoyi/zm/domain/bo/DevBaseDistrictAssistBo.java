package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 照明控制的分区控制的辅助业务对象 dev_base_district_assist
 *
 * @author ruoyi
 * @date 2024-08-31
 */

@Data

public class DevBaseDistrictAssistBo {

    /**
     * 主键，对应控制分区主键
     */
    @NotNull(message = "主键，对应控制分区主键不能为空", groups = {EditGroup.class})
    private Integer id;

    /**
     * 控制分区的亮度
     */
    @NotNull(message = "控制分区的亮度不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer lux;

    /**
     * 控制分区的开关状态
     */
    @NotNull(message = "控制分区的开关状态不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer switchStatus;


}
