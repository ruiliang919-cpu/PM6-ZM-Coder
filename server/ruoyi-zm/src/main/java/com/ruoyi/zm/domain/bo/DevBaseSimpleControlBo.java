package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 普通时控启用业务对象 dev_base_simple_control
 *
 * @author ruoyi
 * @date 2024-09-09
 */

@Data

public class DevBaseSimpleControlBo {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Integer id;

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer deviceId;

    /**
     * 普通时控ID
     */
    @NotNull(message = "普通时控ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer controlId;

    /**
     * 是否启用 0不启用 1已启用
     */
    @NotNull(message = "是否启用 0不启用 1已启用不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer enabled;


}
