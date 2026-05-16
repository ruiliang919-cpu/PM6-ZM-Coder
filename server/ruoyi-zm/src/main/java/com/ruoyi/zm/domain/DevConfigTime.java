package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 机柜时间设置对象 dev_config_time
 *
 * @author ruoyi
 * @date 2024-08-26
 */
@Data
@TableName("dev_config_time")
public class DevConfigTime implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 设备通讯编号
     */
    private Integer deviceNo;
    /**
     * 年
     */
    private Integer year;
    /**
     * 月
     */
    private Integer month;
    /**
     * 日
     */
    private Integer day;
    /**
     * 时
     */
    private Integer hour;
    /**
     * 分
     */
    private Integer minute;
    /**
     * 秒
     */
    private Integer second;
    /**
     * 对时标志位 0不启用 1启用
     */
    private Integer timeSwitch;

}

