package com.ruoyi.zm.domain.vo;

import lombok.Data;

@Data
public class TimeSaveReqVo {
    private Integer deviceId;
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer min;
    private Integer second;
}
