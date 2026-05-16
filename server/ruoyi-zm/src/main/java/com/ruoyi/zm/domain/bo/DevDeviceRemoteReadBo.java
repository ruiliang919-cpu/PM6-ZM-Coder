package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 交流开关开关数量 回路亮度 分组亮度 红外传感模式 照度传感模式 手动模式选择 与 机柜 对应业务对象 dev_device_remote_read
 *
 * @author ruoyi
 * @date 2024-08-27
 */

@Data

public class DevDeviceRemoteReadBo   {

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
     * 系统设置-控制方式-交流开关-开关数量 0-8
     */
    @NotNull(message = "系统设置-控制方式-交流开关-开关数量 0-8不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer acSwitchNum;

    /**
     * 系统控制-回路控制-回路亮度
     */
    @NotNull(message = "系统控制-回路控制-回路亮度不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer loopLux;

    /**
     * 系统控制-分组控制-分组亮度
     */
    @NotNull(message = "系统控制-分组控制-分组亮度不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer groupLux;

    /**
     * 系统设置-控制方式-红外传感模式 0-关闭/1-开启
     */
    @NotNull(message = "系统设置-控制方式-红外传感模式 0-关闭/1-开启不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer infraredSensingMode;

    /**
     * 系统设置-控制方式-照度传感模式 0-关闭/1-开启
     */
    @NotNull(message = "系统设置-控制方式-照度传感模式 0-关闭/1-开启不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer illuminanceSensingMode;

    /**
     * 系统控制-照明控制-手动模式选择 0-未选中/1-回路控制/2-分组控制/3-场景控制
     */
    @NotNull(message = "系统控制-照明控制-手动模式选择 0-未选中/1-回路控制/2-分组控制/3-场景控制不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer manualMode;


}
