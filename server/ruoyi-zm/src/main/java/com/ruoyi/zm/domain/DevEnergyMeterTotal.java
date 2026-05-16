package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 十六电总耗电量对象 dev_energy_meter_total
 *
 * @author ruoyi
 * @date 2024-09-12
 */
@Data

@TableName("dev_energy_meter_total")
public class DevEnergyMeterTotal {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 设备编号
     */
    private Integer deviceNo;
    /**
     * 电能表编号
     */
    private Integer energyMeterNo;
    /**
     * 总耗电量
     */
    private BigDecimal power;
    /**
     * 时间戳
     */
    private Long timestamp;

}
