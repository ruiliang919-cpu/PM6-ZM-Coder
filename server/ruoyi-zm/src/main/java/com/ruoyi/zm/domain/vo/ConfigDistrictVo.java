package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigDistrictVo {
    // 系统编号 主键
    private Long id;
    // 设备编号
    private Integer deviceId;
    // 分区编号ID
    private Integer districtId;
    // 分区名称
    private String name;
    // 分组编号
    private Integer groupNo;
}
