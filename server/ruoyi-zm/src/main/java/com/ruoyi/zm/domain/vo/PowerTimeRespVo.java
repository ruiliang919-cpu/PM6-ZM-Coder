package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PowerTimeRespVo {
    // 设备ID
    private Integer deviceId;
    // 时间戳
    private Long timestamp;
    // 总年季月周日耗电量
    private BigDecimal power;
}
