package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigDistrictRespVo {
    private Integer orderNo;
    private Integer id;
    private String name;
    private Integer lux;
    private Integer switchStatus;
}
