package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 控制分区业务对象 dev_base_district
 *
 * @author mophi
 * @date 2024-07-11
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DevBaseDistrictBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空")
    private Integer orderNo;

    /**
     * 分区码
     */
    @NotNull(message = "分区码不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;


}
