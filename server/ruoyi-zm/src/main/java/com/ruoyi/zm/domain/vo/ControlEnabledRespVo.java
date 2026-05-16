package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControlEnabledRespVo {
    private Integer deviceId;
    private Integer controlId;
    private Boolean enabled;
}
