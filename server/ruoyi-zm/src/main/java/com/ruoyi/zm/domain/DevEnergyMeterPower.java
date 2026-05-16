package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 十六电总功率对象 dev_energy_meter_power
 *
 * @author ruoyi
 * @date 2024-09-12
 */
@Data

@TableName("dev_energy_meter_power")
public class DevEnergyMeterPower {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Integer id;
    /**
     * 设备编号
     */
    private Integer deviceNo;
    /**
     * 电能表编号
     */
    private Integer energyMeterNo;
    /**
     * 功率
     */
    private BigDecimal power;

}
