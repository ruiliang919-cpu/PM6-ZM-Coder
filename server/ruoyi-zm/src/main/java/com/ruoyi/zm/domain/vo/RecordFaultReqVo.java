package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordFaultReqVo {
    private Long startTime;
    private String deviceName;
    private String eventName;
    private Integer pageSize;
    private Integer pageNum;
}
