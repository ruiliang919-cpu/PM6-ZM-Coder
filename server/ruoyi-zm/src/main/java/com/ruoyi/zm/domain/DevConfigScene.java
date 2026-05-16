package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场景设置对象 dev_config_scene
 *
 * @author ruoyi
 * @date 2024-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dev_config_scene")
public class DevConfigScene extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 系统编号
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 设备编号
     */
    private Long deviceId;
    /**
     * 场景编号
     */
    private Long sceneId;
    /**
     * 分组编号
     */
    private Long groupId;
    /**
     * 亮度，亮度100
     */
    private Long lux;
    /**
     * 开关类型；0-开/1-关
     */
    private Integer btnStatus;
    /**
     * 分组信息 是否选中 0不选中 1选中
     */
    private Integer selectStatus;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

}
