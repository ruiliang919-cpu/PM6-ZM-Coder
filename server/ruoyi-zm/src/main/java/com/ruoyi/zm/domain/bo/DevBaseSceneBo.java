package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 场景业务对象 dev_base_scene
 *
 * @author mophi
 * @date 2024-08-26
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DevBaseSceneBo extends BaseEntity {

    /**
     * 系统编号
     */
    @NotNull(message = "系统编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 场景编号
     */
    @NotNull(message = "场景编号不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer sceneId;

    /**
     * 场景名称
     */
    @NotBlank(message = "场景名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;


}
