package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 普通时控模式与分组配置对象 dev_config_simple_group
 *
 * @author ruoyi
 * @date 2024-09-03
 */
@Data

@TableName("dev_config_simple_group")
public class DevConfigSimpleGroup implements Serializable {

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
     * 普通模式时控ID
     */
    private Integer simpleId;
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
