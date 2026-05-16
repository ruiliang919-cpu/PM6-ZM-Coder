package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 照明控制的分区控制的辅助对象 dev_base_district_assist
 *
 * @author ruoyi
 * @date 2024-08-31
 */
@Data

@TableName("dev_base_district_assist")
public class DevBaseDistrictAssist implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，对应控制分区主键
     */
    @TableId(value = "id")
    private Integer id;
    /**
     * 控制分区的亮度
     */
    private Integer lux;
    /**
     * 控制分区的开关状态
     */
    private Integer switchStatus;

}
