package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 遥调协议业务对象 dev_protocol_06
 *
 * @author ruoyi
 * @date 2024-07-19
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DevProtocol06Bo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 地址
     */
    @NotBlank(message = "地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String addr;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 倍率
     */
    @NotNull(message = "倍率不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long magnification;

    /**
     * 单位
     */
    @NotBlank(message = "单位不能为空", groups = { AddGroup.class, EditGroup.class })
    private String unit;

    /**
     * 读写属性
     */
    @NotBlank(message = "读写属性不能为空", groups = { AddGroup.class, EditGroup.class })
    private String readWrite;

    /**
     * 说明
     */
    @NotBlank(message = "说明不能为空", groups = { AddGroup.class, EditGroup.class })
    private String memo;


}
