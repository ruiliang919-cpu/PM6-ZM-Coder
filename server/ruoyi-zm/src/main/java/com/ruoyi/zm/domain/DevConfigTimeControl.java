package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 普通模式-时控配置对象 dev_config_time_control
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@Data
@TableName("dev_config_time_control")
public class DevConfigTimeControl implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 系统编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 设备编号
     */
    private Long deviceId;
    /**
     * 时控编号
     */
    private Integer timeControlId;
    /**
     * 时段信息编号
     */
    private Integer timeFrameId;
    /**
     * 亮度，100对应100%
     */
    private Integer lux;
    /**
     * 开关 0-开/1-关
     */
    private Integer switchStatus;
    /**
     * 使能（启用状态）；0-使能（启用）/1-禁止
     */
    private Integer enabledStatus;
    /**
     * 开始时间 ASCII字符串长度6个字节，示例：”12:00“
     */
    private String stime;
    /**
     * 结束时间 ASCII字符串长度6个字节，示例：”12:00“
     */
    private String etime;

}
