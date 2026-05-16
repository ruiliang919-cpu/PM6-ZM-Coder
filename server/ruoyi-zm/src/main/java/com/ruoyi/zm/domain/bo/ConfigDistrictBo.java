package com.ruoyi.zm.domain.bo;


import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ConfigDistrictBo extends BaseEntity {
    // 系统编号 主键
    private Long id;
    // 设备编号
    private Integer deviceId;
    // 分区编号ID
    private Integer districtId;
    // 名称
    private String name;
}
