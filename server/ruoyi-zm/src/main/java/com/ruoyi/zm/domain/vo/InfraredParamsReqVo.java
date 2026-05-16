package com.ruoyi.zm.domain.vo;

import lombok.Data;

@Data
public class InfraredParamsReqVo {
    private Integer deviceId;
    // 启用按钮
    private Boolean enabled;
    // 启用按钮旁边的红外传感器ID
    private Integer sensorId;
    // 选中的分组ID
    private Integer[] groupIds;
    // 照明参数
    private InfraredParamsTable table;
}
