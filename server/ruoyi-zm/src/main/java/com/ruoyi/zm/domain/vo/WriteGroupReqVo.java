package com.ruoyi.zm.domain.vo;

import lombok.Data;

@Data
public class WriteGroupReqVo {
    private Integer deviceId;
    private Integer lux;
    private Integer switchStatus;
    private Integer[] groupSelectArr;
}
