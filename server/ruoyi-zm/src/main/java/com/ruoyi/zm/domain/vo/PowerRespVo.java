package com.ruoyi.zm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Schema(description = "每一个机柜的总耗电量或日耗电量 Response VO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class PowerRespVo {
    /**
     * 机柜编号（主机号）
     */
    private Integer deviceId;

    /**
     * 机柜名称
     */
    private String name;

    /**
     * 机柜总耗电量或日耗电量
     */
    private BigDecimal power;
}
