package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 红外模式传感器与分组选择业务对象 dev_infrared_group
 *
 * @author ruoyi
 * @date 2024-09-04
 */

@Data

public class DevInfraredGroupBo {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Integer id;

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer deviceId;

    /**
     * 红外模式传感器ID
     */
    @NotNull(message = "红外模式传感器ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer sensorId;

    /**
     * 分组ID
     */
    @NotNull(message = "分组ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer groupId;

    /**
     * 分组名称
     */
    @NotBlank(message = "分组名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String groupName;

    /**
     * 是否选择分组 0未选中 1已选中
     */
    @NotNull(message = "是否选择分组 0未选中 1已选中不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer selectStatus;


}
