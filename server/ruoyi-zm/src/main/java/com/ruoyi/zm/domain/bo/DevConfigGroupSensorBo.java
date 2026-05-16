package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 传感器与分组对应业务对象 dev_config_group_sensor
 *
 * @author ruoyi
 * @date 2024-08-27
 */

@Data
public class DevConfigGroupSensorBo   {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer deviceId;

    /**
     * 分组编号
     */
    @NotNull(message = "分组编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer groupId;

    /**
     * 红外传感器编号
     */
    @NotNull(message = "红外传感器编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer infraredSensorId;

    /**
     * 照度传感器编号
     */
    @NotNull(message = "照度传感器编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer illuminanceSensorId;


}
