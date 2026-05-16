package com.ruoyi.zm.domain.vo;

import lombok.Data;

@Data
public class IlluminanceParamsReqVo {
    private Integer deviceId;
    // 启用按钮 0未启用 1启用
    private Boolean enabled;
    // 启用按钮旁边的照度传感器ID
    private Integer sensorId;
    // 选中的分组ID
    private Integer[] groupIds;
    // 照明参数
    private IlluminanceParamsTable table;
}
