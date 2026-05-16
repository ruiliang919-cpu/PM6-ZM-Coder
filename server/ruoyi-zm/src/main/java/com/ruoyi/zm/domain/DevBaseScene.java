package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 场景对象 dev_base_scene
 *
 * @author mophi
 * @date 2024-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dev_base_scene")
@AllArgsConstructor
@NoArgsConstructor
public class DevBaseScene extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 系统编号
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 场景编号
     */
    private Integer sceneId;
    /**
     * 场景名称
     */
    private String name;
    // /**
    //  * 删除标志（0代表存在 2代表删除）
    //  */
    // @TableLogic
    // private String delFlag;

}
