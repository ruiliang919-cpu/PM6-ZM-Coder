package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 设备区域业务对象 dev_base_region
 *
 * @author ruoyi
 * @date 2024-07-11
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DevBaseRegionBo extends BaseEntity {

    /**
     * 系统编号
     */
    @NotNull(message = "系统编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 区域名称
     */
    @NotBlank(message = "区域名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;


}
