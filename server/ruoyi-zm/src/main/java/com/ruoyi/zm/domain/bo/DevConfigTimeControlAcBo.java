package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 系统设置-控制方式-交流开关-时段信息 业务对象 dev_config_time_control_ac
 *
 * @author ruoyi
 * @date 2024-08-27
 */

@Data

public class DevConfigTimeControlAcBo   {

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
     * 时段信息ID
     */
    @NotNull(message = "时段信息ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer frameId;

    /**
     * 开关
     */
    @NotNull(message = "开关不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer switchStatus;

    /**
     * 使能
     */
    @NotNull(message = "使能不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer enable;

    /**
     * 开始时间_时
     */
    @NotNull(message = "开始时间_时不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer stimeHour;

    /**
     * 开始时间_分
     */
    @NotNull(message = "开始时间_分不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer stimeMin;

    /**
     * 结束时间_时
     */
    @NotNull(message = "结束时间_时不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer etimeHour;

    /**
     * 结束时间_分
     */
    @NotNull(message = "结束时间_分不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer etimeMin;


}
