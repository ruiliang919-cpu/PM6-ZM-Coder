package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 传感器与分组对应对象 dev_config_group_sensor
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@Data

@TableName("dev_config_group_sensor")
public class DevConfigGroupSensor implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 设备ID
     */
    private Integer deviceId;
    /**
     * 分组编号
     */
    private Integer groupId;
    /**
     * 红外传感器编号
     */
    private Integer infraredSensorId;
    /**
     * 照度传感器编号
     */
    private Integer illuminanceSensorId;

}
