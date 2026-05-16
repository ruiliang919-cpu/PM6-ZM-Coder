package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分区配置对象 dev_config_district
 *
 * @author ruoyi
 * @date 2024-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dev_config_district")
public class DevConfigDistrict extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 系统编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 设备编号
     */
    private Long deviceId;
    /**
     * 分区编号ID（实际表示分组ID）分区编号 1 表示 第一分组
     */
    private Long districtId;
    /**
     * 分区地址 即分组所在的区域地址 若为 5 表示 分区编号所在的地区为 5，若分区编号为 1 ，则表示第一分组所在的区域为 5
     */
    private Integer addr;
    /**
     * 名称（不显示，因为分区即是分组，分组已有名称，所以分区不需要名称）
     */
    private String name;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

}
