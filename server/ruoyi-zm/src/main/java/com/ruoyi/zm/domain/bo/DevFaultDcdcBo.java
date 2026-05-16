package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DC/DC故障信息业务对象 dev_fault_dcdc
 *
 * @author ruoyi
 * @date 2024-08-24
 */

@Data

public class DevFaultDcdcBo  {

    /**
     * 系统编号
     */
    @NotNull(message = "系统编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer deviceId;

    /**
     * DCDC模块（通讯）故障编号
     */
    @NotNull(message = "DCDC模块（通讯）故障编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer no;

    /**
     * 模块故障判定，即运行状态（0正常 1故障）
     */
    @NotNull(message = "模块故障判定，即运行状态（0正常 1故障）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer moduleType;

    /**
     * 通讯模块故障判定，即通讯状态（0正常 1故障）
     */
    @NotNull(message = "通讯模块故障判定，即通讯状态（0正常 1故障）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer onlineType;

    /**
     * DCDC模块输出电压
     */
    @NotNull(message = "DCDC模块输出电压不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal voltage;

    /**
     * DCDC模块输出电流
     */
    @NotNull(message = "DCDC模块输出电流不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal electricity;


}
