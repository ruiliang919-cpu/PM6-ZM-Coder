package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 红外模式传感器与分组选择对象 dev_infrared_group
 *
 * @author ruoyi
 * @date 2024-09-04
 */
@Data

@TableName("dev_infrared_group")
public class DevInfraredGroup implements Serializable {

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
     * 红外模式传感器ID
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
     * 是否选择分组 0未选中 1已选中
     */
    private Integer selectStatus;

}
