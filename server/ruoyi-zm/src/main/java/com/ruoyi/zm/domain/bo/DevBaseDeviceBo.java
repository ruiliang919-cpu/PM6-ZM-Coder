package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 机柜业务对象 dev_base_device
 *
 * @author mophi
 * @date 2024-08-24
 */

@Data

public class DevBaseDeviceBo {

    /**
     * 系统编号
     */

    @NotNull(message = "系统编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceName;

    /**
     * 设备唯一标识
     */
    @NotNull(message = "设备唯一标识不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long deviceNo;

    /**
     * 设备通讯编号 即设备ID
     */
    @NotNull(message = "设备通讯编号 即设备ID 不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer deviceId;

    /**
     * 设备所在区域
     */
    @NotNull(message = "设备所在区域不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long regionId;

    /**
     * 通信状态（0离红; 1在线）由设备自行检测
     */
    // @NotNull(message = "通信状态（0离红; 1在线）由设备自行检测不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer onlineStatus;

    /**
     * 设备状态（0故障/告警; 1正常）由设备自行检测
     */
    // @NotNull(message = "设备状态（0故障/告警; 1正常）由设备自行检测不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer deviceStatus;

    /**
     * 运行开关（0关; 1开）设备系统开关
     */
    // @NotNull(message = "运行开关（0关; 1开）设备系统开关不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer runStatus;

    /**
     * 运行模式（0自动; 1手动）运行模式
     */
    // @NotNull(message = "运行模式（0自动; 1手动）运行模式不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer runMode;

    /**
     * 运行模式类型1，普通时控；2，场景时控；3，红外传感；4，照度传感；
     */
    // @NotNull(message = "运行模式类型1，普通时控；2，场景时控；3，红外传感；4，照度传感；不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer runModeType;

    /**
     * 环境温度
     */
    // @NotNull(message = "环境温度不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal temperature;

    /**
     * 交流输入路数
     */
    @NotNull(message = "交流输入路数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer acInputLoop;

    /**
     * 交流电流显示
     */
    @NotNull(message = "交流电流显示不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer acCurrentDisplay;

    /**
     * 交流采样模式
     */
    @NotNull(message = "交流采样模式不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer acSamplingMode;

    /**
     * 直流模块个数
     */
    @NotNull(message = "直流模块个数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long dcModuleNum;

    /**
     * 交流回路数量
     */
    @NotNull(message = "交流回路数量不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long acLoopNum;

    /**
     * 其他开关数量
     */
    @NotNull(message = "其他开关数量不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long otherSwitchNum;

    /**
     * 交流模块回路数
     */
    @NotNull(message = "交流模块回路数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long acModuleNum;

    /**
     * 调光回路数
     */
    @NotNull(message = "调光回路数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long dimmerNum;

    /**
     * 直流开关回路数
     */
    @NotNull(message = "直流开关回路数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long dcSwitchNum;

    /**
     * 直流母线电压
     */
    @NotNull(message = "直流母线电压不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal dcBusVoltage;

    /**
     * 直流母线电流
     */
    @NotNull(message = "直流母线电流不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal dcBusCurrent;

    /**
     * 母线正对地电压
     */
    @NotNull(message = "母线正对地电压不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal busDirectVoltageToEarth;

    /**
     * 母线负对地电压
     */
    @NotNull(message = "母线负对地电压不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal busNegativeVoltageToEarth;

    /**
     * 母线正极绝缘阻值
     */
    @NotNull(message = "母线正极绝缘阻值不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal positivePoleResistance;

    /**
     * 母线负极绝缘阻值
     */
    @NotNull(message = "母线负极绝缘阻值不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal negativePoleResistance;

    /**
     * 母线交窜直电压
     */
    @NotNull(message = "母线交窜直电压不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal busbarCrossoverVoltage;

    /**
     * 母线交窜直电流
     */
    @NotNull(message = "母线交窜直电流不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal busbarCrossoverCurrent;

    /**
     * 最后一次心跳时间
     */
    @NotNull(message = "最后一次心跳时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date lastTime;

    /**
     * IP地址
     */
    @NotBlank(message = "IP地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String ip;

    /**
     * 端口号
     */
    @NotNull(message = "端口号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long port;

    /**
     * 机柜类型 （1:电源柜 0:配电柜）
     */
    @NotNull(message = "机柜类型 （1:电源柜 0:配电柜）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer type;

    /**
     * AC/DC输出电压，为-1时表示未设置
     */
    @NotNull(message = "AC/DC输出电压，为-1时表示未设置不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer acDcOutputVoltage;

    /**
     * DC/DC输出电压，为-1时表示未设置
     */
    @NotNull(message = "DC/DC输出电压，为-1时表示未设置不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer dcDcOutputVoltage;

    /**
     * 当前机柜所在场景
     */
    @NotNull(message = "当前机柜所在场景不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer sceneSelect;

}
