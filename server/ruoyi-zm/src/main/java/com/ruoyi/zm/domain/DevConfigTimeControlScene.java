package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 场景模式-时控配置对象 dev_config_time_control_scene
 *
 * @author ruoyi
 * @date 2024-08-27
 */
@Data

@TableName("dev_config_time_control_scene")
public class DevConfigTimeControlScene implements Serializable {

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
     * 时控编号
     */
    private Integer timeControlId;
    /**
     * 时段信息编号
     */
    private Integer timeFrameId;
    /**
     * 使能（启用状态）；0-使能（启用）/1-禁止
     */
    private Integer enabledStatus;
    /**
     * 设置“1”则选择场景01：1-10对应场景1-10
     */
    private Integer sceneSelect;
    private String sDate;
    private String eDate;
    /**
     * 开始时间 ASCII字符串长度6个字节，示例：”12:00“
     */
    private String stime;
    /**
     * 结束时间 ASCII字符串长度6个字节，示例：”12:00“
     */
    private String etime;

}
