package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 十六电总耗电量业务对象 dev_energy_meter_total
 *
 * @author ruoyi
 * @date 2024-09-12
 */

@Data

public class DevEnergyMeterTotalBo {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 设备编号
     */
    @NotNull(message = "设备编号不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer deviceNo;

    /**
     * 电能表编号
     */
    @NotNull(message = "电能表编号不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer energyMeterNo;

    /**
     * 总耗电量
     */
    @NotNull(message = "总耗电量不能为空", groups = {AddGroup.class, EditGroup.class})
    private BigDecimal power;

    /**
     * 时间戳
     */
    @NotNull(message = "时间戳不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long timestamp;


}
