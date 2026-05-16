package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 控制分区对象 dev_base_district
 *
 * @author mophi
 * @date 2024-07-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dev_base_district")
public class DevBaseDistrict extends BaseEntity {

    private static final long serialVersionUID=1L;
    /**
     * 排序编号
     */
    @TableId
    private Integer orderNo;
    /**
     * 系统编号
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 分区亮度
     */
    private Integer lux;
    /**
     * 开关状态
     */
    private Integer switchStatus;
    // /**
    //  * 删除标志（0代表存在 2代表删除）
    //  */
    // @TableLogic
    // private String delFlag;

}
