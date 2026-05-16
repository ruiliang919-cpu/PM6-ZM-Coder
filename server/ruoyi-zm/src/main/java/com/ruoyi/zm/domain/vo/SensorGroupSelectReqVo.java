package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorGroupSelectReqVo {
    private Integer deviceId;
    private Integer sensorId;
    private Integer pageNum;
    private Integer pageSize;
}
