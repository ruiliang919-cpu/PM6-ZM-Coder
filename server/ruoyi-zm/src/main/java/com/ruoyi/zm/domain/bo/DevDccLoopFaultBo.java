package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 直流回路故障记录业务对象 dev_dcc_loop_fault
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
public class DevDccLoopFaultBo {

    /**
     * 系统编号
     */
    @NotNull(message = "系统编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long deviceId;

    /**
     * 回路编号
     */
    @NotNull(message = "回路编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long no;

    /**
     * 直流回路过压 0：正常 1：故障
     */
    @NotNull(message = "直流回路过压 0：正常 1：故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long dccOvervoltage;

    /**
     * 直流回路欠压
     */
    @NotNull(message = "直流回路欠压不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long dccUndervoltage;

    /**
     * 直流回路过流
     */
    @NotNull(message = "直流回路过流不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long dccOvercurrent;

    /**
     * 直流回路过温
     */
    @NotNull(message = "直流回路过温不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long dccOvertemperature;

    /**
     * 直流回路短路
     */
    @NotNull(message = "直流回路短路不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long dccShortcircuit;


}
