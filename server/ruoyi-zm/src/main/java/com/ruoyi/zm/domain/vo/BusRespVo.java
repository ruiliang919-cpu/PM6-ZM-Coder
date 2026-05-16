package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 母线信息包装类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusRespVo {
    // 母线电压
    private BigDecimal busVoltage = BigDecimal.ZERO;
    // 母线电流
    private BigDecimal busElectricity= BigDecimal.ZERO;
    // 母线正对地电压
    private BigDecimal positivePoleResistance= BigDecimal.ZERO;
    // 母线负对地电压
    private BigDecimal negativePoleResistance= BigDecimal.ZERO;
    // 环境温度
    private BigDecimal temperature= BigDecimal.ZERO;
}
