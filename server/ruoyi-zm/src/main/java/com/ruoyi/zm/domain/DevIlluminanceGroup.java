package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 照度模式传感器与分组选择对象 dev_illuminance_group
 *
 * @author ruoyi
 * @date 2024-09-04
 */
@Data

@TableName("dev_illuminance_group")
public class DevIlluminanceGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Integer id;
    /**
     * 设备ID
     */
    private Integer deviceId;
    /**
     * 照度模式传感器ID
     */
    private Integer sensorId;
    /**
     * 分组ID
     */
    private Integer groupId;
    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 是否选中分组 0未选中 1已选中
     */
    private Integer selectStatus;

}
