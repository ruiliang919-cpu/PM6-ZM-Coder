package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统设置-控制方式-交流开关-时段信息 对象 dev_config_time_control_ac
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@Data
@TableName("dev_config_time_control_ac")
public class DevConfigTimeControlAc implements Serializable {

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
     * 时段信息ID
     */
    private Integer frameId;
    /**
     * 开关
     */
    private Integer switchStatus;
    /**
     * 使能
     */
    private Integer enable;
    /**
     * 开始时间_时
     */
    private Integer stimeHour;
    /**
     * 开始时间_分
     */
    private Integer stimeMin;
    /**
     * 结束时间_时
     */
    private Integer etimeHour;
    /**
     * 结束时间_分
     */
    private Integer etimeMin;

}
