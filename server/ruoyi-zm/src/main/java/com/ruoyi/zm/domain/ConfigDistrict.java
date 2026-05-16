package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dev_config_district")
public class ConfigDistrict extends BaseEntity implements Serializable {
    // 系统编号 主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    // 设备编号
    private Integer deviceId;
    // 分区编号ID
    private Integer districtId;
    // 名称
    private String name;
}
