package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 母线绝缘信息包装类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusInsulationRespVo {
    /**
     * 母线正极绝缘阻值
     */
    private BigDecimal positivePoleResistance;

    /**
     * 母线负极绝缘阻值
     */
    private BigDecimal negativePoleResistance;
}
