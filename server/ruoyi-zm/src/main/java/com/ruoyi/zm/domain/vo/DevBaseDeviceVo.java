package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 机柜视图对象 dev_base_device
 *
 * @author mophi
 * @date 2024-07-11
 */
@Data
public class DevBaseDeviceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @ExcelProperty(value = "系统编号")
    private Long id;

    /**
     * 设备名称
     */
    @ExcelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备唯一标识
     */
    @ExcelProperty(value = "设备唯一标识")
    private Long deviceNo;

    /**
     * 设备通讯编号 即设备ID
     */
    @ExcelProperty(value = "设备通讯编号 即设备ID ")
    private Integer deviceId;
    /**
     * 设备所在区域
     */
    @ExcelProperty(value = "设备所在区域")
    private Long regionId;

    /**
     * 通信状态（0离红; 1在线）由设备自行检测
     */
    @ExcelProperty(value = "通信状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=离红;,1=在线")
    private Integer onlineStatus;

    /**
     * 设备状态（0故障/告警; 1正常）由设备自行检测
     */
    @ExcelProperty(value = "设备状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=故障/告警;,1=正常")
    private Integer deviceStatus;

    /**
     * 运行开关（0关; 1开）设备系统开关
     */
    @ExcelProperty(value = "运行开关", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=关;,1=开")
    private Integer runStatus;

    /**
     * 运行模式（0自动; 1手动）运行模式
     */
    @ExcelProperty(value = "运行模式", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=自动;,1=手动")
    private Integer runMode;

    /**
     * 环境温度
     */
    @ExcelProperty("环境温度")
    private BigDecimal temperature;
    /**
     * 直流开关回路数
     */
    private Integer dcSwitchNum;
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
     * 运行模式类型1，普通时控；2，场景时控；3，红外传感；4，照度传感；
     */
    @ExcelProperty(value = "运行模式类型1，普通时控；2，场景时控；3，红外传感；4，照度传感；")
    private Integer runModeType;

    /**
     * 直流模块个数
     */
    @ExcelProperty(value = "直流模块个数")
    private Integer dcModuleNum;

    /**
     *交流模块回路数
     */
    @ExcelProperty(value = "交流模块回路数")
    private Integer acModuleNum;

    /**
     * 交流模块回路数
     */
    @ExcelProperty(value = "交流回路数量")
    private Integer acLoopNum;

    /**
     * 调光回路数
     */
    @ExcelProperty(value = "调光回路数")
    private Integer dimmerNum;

    /**
     * 直流母线电压
     */
    @ExcelProperty(value = "直流母线电压")
    private BigDecimal dcBusVoltage;
    /**
     * 直流母线电流
     */
    @ExcelProperty(value = "直流母线电流")
    private BigDecimal dcBusCurrent;
    /**
     * 母线正对地电压
     */
    @ExcelProperty(value = "母线正对地电压")
    private BigDecimal busDirectVoltageToEarth;
    /**
     * 母线负对地电压
     */
    @ExcelProperty(value = "母线负对地电压")
    private BigDecimal busNegativeVoltageToEarth;

    /**
     * 母线正极绝缘阻值
     */
    @ExcelProperty(value = "母线正极绝缘阻值")
    private BigDecimal positivePoleResistance;

    /**
     * 母线负极绝缘阻值
     */
    @ExcelProperty(value = "母线负极绝缘阻值")
    private BigDecimal negativePoleResistance;

    /**
     * 母线交窜直电压
     */
    @ExcelProperty(value = "母线交窜直电压")
    private BigDecimal busbarCrossoverVoltage;

    /**
     * 母线交窜直电流
     */
    @ExcelProperty(value = "母线交窜直电流")
    private BigDecimal busbarCrossoverCurrent;

    /**
     * 最后一次心跳时间
     */
    @ExcelProperty(value = "最后一次心跳时间")
    private Date lastTime;

    /**
     * IP地址
     */
    @ExcelProperty(value = "IP地址")
    private String ip;
    /**
     * 端口号
     */
    @ExcelProperty(value = "端口号")
    private Integer port;

    /**
     * 机柜类型 机柜类型 （1:电源柜 0:配电柜）
     */
    @ExcelProperty(value = "机柜类型")
    private Integer type;

    /**
     * 主监控版本
     */
    @ExcelProperty(value = "主监控版本")
    private String version;

    /**
     * AC/DC输出电压，为-1时表示未设置
     */
    @ExcelProperty(value = "AC/DC输出电压，为-1时表示未设置")
    private Integer acDcOutputVoltage;

    /**
     * DC/DC输出电压，为-1时表示未设置
     */
    @ExcelProperty(value = "DC/DC输出电压，为-1时表示未设置")
    private Integer dcDcOutputVoltage;

    /**
     * 当前机柜所在场景
     */
    @ExcelProperty(value = "当前机柜所在场景")
    private Integer sceneSelect;
}
