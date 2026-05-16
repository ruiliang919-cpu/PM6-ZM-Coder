package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 时控业务对象 dev_config_infrared_sensor
 *
 * @author ruoyi
 * @date 2024-08-27
 */

@Data

public class DevConfigInfraredSensorBo   {

    /**
     * 系统编号
     */
    @NotNull(message = "系统编号不能为空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 设备编号
     */
    @NotNull(message = "设备编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer deviceId;

    /**
     * 传感器编号
     */
    @NotNull(message = "传感器编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer sensorId;

    /**
     * 分组信息 16位对应分组1-16
     */
    // @NotNull(message = "分组信息 16位对应分组1-16不能为空", groups = { AddGroup.class, EditGroup.class })
    // private Integer groupId;

    /**
     * 启用 0-未启用/1-启用
     */
    @NotNull(message = "启用 0-未启用/1-启用不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer enabled;

    /**
     * 感应信息亮度 100对应100%
     */
    @NotNull(message = "感应信息亮度 100对应100%不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer inductiveLux;

    /**
     * 感应信息开关 0-开/1-关
     */
    @NotNull(message = "感应信息开关 0-开/1-关不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer inductiveSwitchStatus;

    /**
     * 未感应信息亮度 100对应100%
     */
    @NotNull(message = "未感应信息亮度 100对应100%不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer uninductionLux;

    /**
     * 未感应信息开关 0-开/1-关
     */
    @NotNull(message = "未感应信息开关 0-开/1-关不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer uninductionSwitchStatus;

    /**
     * 延时 单位S
     */
    @NotNull(message = "延时 单位S不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer delayedTime;


}
