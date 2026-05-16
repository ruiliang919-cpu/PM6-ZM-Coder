package com.ruoyi.zm.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ElectricVo {
    private String name;
    private String ip;
    private String time;
    private BigDecimal power;
}
