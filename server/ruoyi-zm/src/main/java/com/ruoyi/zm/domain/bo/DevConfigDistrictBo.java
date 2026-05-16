package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 分区配置业务对象 dev_config_district
 *
 * @author ruoyi
 * @date 2024-08-26
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DevConfigDistrictBo extends BaseEntity {

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
     * 分区编号ID（实际表示分组ID）分区编号 1 表示 第一分组
     */
    @NotNull(message = "分区编号ID（实际表示分组ID）分区编号 1 表示 第一分组不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long districtId;

    /**
     * 分区地址 即分组所在的区域地址 若为 5 表示 分区编号所在的地区为 5，若分区编号为 1 ，则表示第一分组所在的区域为 5
     */
    @NotNull(message = "分区地址 即分组所在的区域地址 若为 5 表示 分区编号所在的地区为 5，若分区编号为 1 ，则表示第一分组所在的区域为 5不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer addr;

    /**
     * 名称（不显示，因为分区即是分组，分组已有名称，所以分区不需要名称）
     */
    @NotBlank(message = "名称（不显示，因为分区即是分组，分组已有名称，所以分区不需要名称）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;


}
