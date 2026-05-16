package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordOneReqVo {
    private Integer deviceId;
    private Long startTime;
    private Long endTime;
    private Integer pageSize;
    private Integer pageNum;
}
