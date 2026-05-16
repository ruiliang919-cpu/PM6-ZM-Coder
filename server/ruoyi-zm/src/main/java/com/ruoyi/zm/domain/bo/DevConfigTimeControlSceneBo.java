package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 场景模式-时控配置业务对象 dev_config_time_control_scene
 *
 * @author ruoyi
 * @date 2024-08-27
 */

@Data

public class DevConfigTimeControlSceneBo {

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
     * 时控编号
     */
    @NotNull(message = "时控编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer timeControlId;

    /**
     * 时段信息编号
     */
    @NotNull(message = "时段信息编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer timeFrameId;

    /**
     * 使能（启用状态）；0-使能（启用）/1-禁止
     */
    @NotNull(message = "使能（启用状态）；0-使能（启用）/1-禁止不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer enabledStatus;

    /**
     * 设置“1”则选择场景01：1-10对应场景1-10
     */
    @NotNull(message = "设置“1”则选择场景01：1-10对应场景1-10不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer sceneSelect;

    /**
     * 开始时间 ASCII字符串长度6个字节，示例：”12:00“
     */
    @NotBlank(message = "开始时间 ASCII字符串长度6个字节，示例：”12:00“不能为空", groups = { AddGroup.class, EditGroup.class })
    private String stime;

    /**
     * 结束时间 ASCII字符串长度6个字节，示例：”12:00“
     */
    @NotBlank(message = "结束时间 ASCII字符串长度6个字节，示例：”12:00“不能为空", groups = { AddGroup.class, EditGroup.class })
    private String etime;


}
