package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevConfigTimeControlRespVo {
    private Integer controlId;
    private Integer frameId;
    private Integer lux;
    private Integer switchStatus;
    private Integer enabledStatus;
    private String stime;
    private String etime;
    // private Integer enabled;
}
