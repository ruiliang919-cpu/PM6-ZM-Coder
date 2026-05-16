package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 设备业务对象 dev_config_group
 *
 * @author ruoyi
 * @date 2024-08-26
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DevConfigGroupBo extends BaseEntity {

    /**
     * 系统编号
     */
    @NotNull(message = "系统编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 设备编号
     */
    @NotNull(message = "设备编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long deviceId;

    /**
     * 分组ID
     */
    @NotNull(message = "分组ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long groupId;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 回路数
     */
    @NotNull(message = "回路数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long loopNum;

    /**
     * 分区ID
     */
    @NotNull(message = "分区ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer districtAddr;

    /**
     * 分组亮度，用于机柜控制-照明控制-分组控制
     */
    @NotNull(message = "分组亮度，用于机柜控制-照明控制-分组控制不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer lux;


}
