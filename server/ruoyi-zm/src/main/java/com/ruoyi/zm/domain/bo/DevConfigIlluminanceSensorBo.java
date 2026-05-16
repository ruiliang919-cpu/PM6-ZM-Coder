package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 系统设置-控制方式-照度模式 传感器业务对象 dev_config_illuminance_sensor
 *
 * @author ruoyi
 * @date 2024-08-29
 */

@Data

public class DevConfigIlluminanceSensorBo   {

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
     * 照度值 单位Lux
     */
    @NotNull(message = "照度值 单位Lux不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer illuminanceLux;

    /**
     * 恒照值 单位Lux
     */
    @NotNull(message = "恒照值 单位Lux不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer constantIlluminanceLux;

    /**
     * 回差值 单位Lux
     */
    @NotNull(message = "回差值 单位Lux不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer hysteresisLux;

    /**
     * 调节时间 单位S
     */
    @NotNull(message = "调节时间 单位S不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer adjustmentTimeSeconds;

    /**
     * 外控使能 0-不启用外控/1-启用外控 05功能码
     */
    @NotNull(message = "外控使能 0-不启用外控/1-启用外控 05功能码不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer externalControlEnabled;

    /**
     * 分组编号 16位对应分组1-16
     */
    // @NotNull(message = "分组编号 16位对应分组1-16不能为空", groups = { AddGroup.class, EditGroup.class })
    // private Integer groupId;

    /**
     * 启用 0-未启用/1-启用
     */
    @NotNull(message = "启用 0-未启用/1-启用不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer enabled;

    /**
     * 外控状态 0未启用；1启用  01功能码
     */
    @NotNull(message = "外控状态 0未启用；1启用  01功能码不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer outControlStatus;

    /**
     * 外控通道 03
     */
    @NotNull(message = "外控通道 03 不能为空", groups = {AddGroup.class, EditGroup.class})
    private BigDecimal outControlChannel;

    /**
     * 外控地址 03
     */
    @NotNull(message = "外控地址 03不能为空", groups = {AddGroup.class, EditGroup.class})
    private BigDecimal outControlAddr;


}
