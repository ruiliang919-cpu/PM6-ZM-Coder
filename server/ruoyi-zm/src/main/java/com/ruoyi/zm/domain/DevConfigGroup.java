package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备对象 dev_config_group
 *
 * @author ruoyi
 * @date 2024-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dev_config_group")
public class DevConfigGroup extends BaseEntity {

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
     * 分组ID
     */
    private Long groupId;
    /**
     * 名称
     */
    private String name;
    /**
     * 回路数
     */
    private Long loopNum;
    /**
     * 分区ID
     */
    private Integer districtAddr;
    /**
     * 分组亮度，用于机柜控制-照明控制-分组控制
     */
    private Integer lux;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

}
