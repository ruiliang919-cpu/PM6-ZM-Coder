package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 总功率业务对象 dev_base_loss
 *
 * @author ruoyi
 * @date 2024-08-29
 */

@Data

public class DevBaseLossBo {

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
     * 总功率
     */
    @NotNull(message = "总功率不能为空", groups = {AddGroup.class, EditGroup.class})
    private BigDecimal loss;


}
