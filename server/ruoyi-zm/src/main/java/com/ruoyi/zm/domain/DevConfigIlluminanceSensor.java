package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 系统设置-控制方式-照度模式 传感器对象 dev_config_illuminance_sensor
 *
 * @author ruoyi
 * @date 2024-08-29
 */
@Data

@TableName("dev_config_illuminance_sensor")
public class DevConfigIlluminanceSensor implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 系统编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 设备编号
     */
    private Integer deviceId;
    /**
     * 传感器编号
     */
    private Integer sensorId;
    /**
     * 照度值 单位Lux
     */
    private Integer illuminanceLux;
    /**
     * 恒照值 单位Lux
     */
    private Integer constantIlluminanceLux;
    /**
     * 回差值 单位Lux
     */
    private Integer hysteresisLux;
    /**
     * 调节时间 单位S
     */
    private Integer adjustmentTimeSeconds;
    /**
     * 外控使能 0-不启用外控/1-启用外控 05功能码
     */
    private Integer externalControlEnabled;
    /**
     * 分组编号 16位对应分组1-16
     */
    // private Integer groupId;
    /**
     * 启用 0-未启用/1-启用
     */
    private Integer enabled;
    /**
     * 外控状态 0未启用；1启用  01功能码
     */
    private Integer outControlStatus;
    /**
     * 外控通道 03
     */
    private BigDecimal outControlChannel;
    /**
     * 外控地址 03
     */
    private BigDecimal outControlAddr;

}
