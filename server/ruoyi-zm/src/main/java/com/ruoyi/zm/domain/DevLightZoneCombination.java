package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上位机控制照明的照明分区组合对象 dev_light_zone_combination
 *
 * @author ruoyi
 * @date 2024-11-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dev_light_zone_combination")
public class DevLightZoneCombination {

    private static final long serialVersionUID=1L;

    /**
     * 排序编号/系统编号
     */
    private Long orderNo;
    /**
     * 控制照明组合编号
     */
    private Long zoneId;
    /**
     * 控制照明组合名称
     */
    private String name;
    /**
     * 对应的分区编号集合
     */
    private String zoneList;

}
