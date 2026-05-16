package com.ruoyi.zm.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnergyMeterRespVo {
    private Integer id;
    private Long timestamp;
    private BigDecimal power;
}
