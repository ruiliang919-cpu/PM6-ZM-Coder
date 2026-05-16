package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 机柜耗电量 功率 业务对象 dev_base_power
 *
 * @author ruoyi
 * @date 2024-08-29
 */

@Data

public class DevBasePowerBo {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer deviceId;

    /**
     * 类型 1:总电量 2:日耗电量 3:周耗电量 4:月耗电量 5:季耗电量 6:年耗电量 7:总功率
     */
    @NotNull(message = "类型 1:总电量 2:日耗电量 3:周耗电量 4:月耗电量 5:季耗电量 6:年耗电量 7:总功率不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer type;

    /**
     * 数值
     */
    @NotNull(message = "数值不能为空", groups = {AddGroup.class, EditGroup.class})
    private BigDecimal value;

    /**
     * 时间戳
     */
    @NotNull(message = "时间戳不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long timestamp;


}
