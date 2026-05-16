package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备区域对象 dev_base_region
 *
 * @author ruoyi
 * @date 2024-07-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dev_base_region")
public class DevBaseRegion extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 系统编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 区域名称
     */
    private String name;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    // @TableLogic
    private String delFlag;

}
