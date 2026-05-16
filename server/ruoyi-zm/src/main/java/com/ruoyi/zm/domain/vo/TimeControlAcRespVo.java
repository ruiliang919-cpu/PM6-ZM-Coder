package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeControlAcRespVo {
    private Integer deviceId;
    private Integer no;
    private String stime;
    private String etime;
    private Integer status;
    private Integer enabled;
}
