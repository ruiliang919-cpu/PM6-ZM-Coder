package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 十六电总功率业务对象 dev_energy_meter_power
 *
 * @author ruoyi
 * @date 2024-09-12
 */

@Data

public class DevEnergyMeterPowerBo {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Integer id;

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
     * 功率
     */
    @NotNull(message = "功率不能为空", groups = {AddGroup.class, EditGroup.class})
    private BigDecimal power;


}
