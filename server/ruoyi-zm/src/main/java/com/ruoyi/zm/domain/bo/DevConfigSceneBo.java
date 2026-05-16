package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 场景设置业务对象 dev_config_scene
 *
 * @author ruoyi
 * @date 2024-09-03
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DevConfigSceneBo extends BaseEntity {

    /**
     * 系统编号
     */
    @NotNull(message = "系统编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 设备编号
     */
    @NotNull(message = "设备编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long deviceId;

    /**
     * 场景编号
     */
    @NotNull(message = "场景编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long sceneId;

    /**
     * 分组编号
     */
    @NotNull(message = "分组编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long groupId;

    /**
     * 亮度，亮度100
     */
    @NotNull(message = "亮度，亮度100不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long lux;

    /**
     * 开关类型；0-开/1-关
     */
    @NotNull(message = "开关类型；0关；1开不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer btnStatus;

    /**
     * 分组信息 是否选中 0不选中 1选中
     */
    @NotNull(message = "分组信息 是否选中 0不选中 1选中不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer selectStatus;


}
