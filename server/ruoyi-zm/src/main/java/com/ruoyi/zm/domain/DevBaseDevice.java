package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 机柜对象 dev_base_device
 *
 * @author mophi
 * @date 2024-07-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("dev_base_device")
public class DevBaseDevice extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备唯一标识
     */
    private Long deviceNo;
    /**
     * 设备通讯编号 即设备ID
     */
    private Integer deviceId;
    /**
     * 设备所在区域
     */
    private Long regionId;
    /**
     * 通信状态（0离红; 1在线）由设备自行检测
     */
    private Integer onlineStatus;
    /**
     * 设备状态（1故障/告警; 0正常）由设备自行检测
     */
    private Integer deviceStatus;
    /**
     * 运行开关（0关; 1开）设备系统开关
     */
    private Integer runStatus;
    /**
     * 运行模式（0自动; 1手动）运行模式
     */
    private Integer runMode;
    /**
     * 运行模式类型1，普通时控；2，场景时控；3，红外传感；4，照度传感；
     */
    private Integer runModeType;

    /**
     * 环境温度
     */
    private BigDecimal temperature;
    /**
     * 直流开关回路数
     */
    private Integer dcSwitchNum;

    /**
     * 交流回路数量
     */
    private Integer acLoopNum;

    /**
     * 其他开关数量
     */
    private Integer otherSwitchNum;
    /**
     * 交流输入路数
     */
    private Integer acInputLoop;
    /**
     * 交流电流显示
     */
    private Integer acCurrentDisplay;
    /**
     * 交流采样模式
     */
    private Integer acSamplingMode;

    /**
     * 直流模块个数
     */
    private Integer dcModuleNum;

    /**
     * 交流模块回路数
     */
    private Integer acModuleNum;
    /**
     * 调光回路数
     */
    private Integer dimmerNum;

    /**
     * 直流母线电压
     */
    private BigDecimal dcBusVoltage;
    /**
     * 直流母线电流
     */
    private BigDecimal dcBusCurrent;
    /**
     * 母线正对地电压
     */
    private BigDecimal busDirectVoltageToEarth;
    /**
     * 母线负对地电压
     */
    private BigDecimal busNegativeVoltageToEarth;

    /**
     * 母线正极绝缘阻值
     */
    private BigDecimal positivePoleResistance;
    /**
     * 母线负极绝缘阻值
     */
    private BigDecimal negativePoleResistance;
    /**
     * 母线交窜直电压
     */
    private BigDecimal busbarCrossoverVoltage;
    /**
     * 母线交窜直电流
     */
    private BigDecimal busbarCrossoverCurrent;
    /**
     * 主监控版本
     */
    private String version;
    /**
     * 最后一次心跳时间
     */
    private Date lastTime;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    // @TableLogic
    private String delFlag;
    /**
     * IP地址
     */
    private String ip;
    /**
     * 端口号
     */
    private Integer port;
    /**
     * 机柜类型 机柜类型 （1:电源柜 0:配电柜）
     */
    private Integer type;

    /**
     * AC/DC输出电压，为null时表示未设置
     */
    private Integer acDcOutputVoltage;
    /**
     * DC/DC输出电压，为null时表示未设置
     */
    private Integer dcDcOutputVoltage;
    /**
     * 当前机柜所在场景
     */
    private Integer sceneSelect;
}
