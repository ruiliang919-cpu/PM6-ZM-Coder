package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分区组合对象 dev_zone_combination
 *
 * @author ruoyi
 * @date 2024-10-08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dev_zone_combination")
public class DevZoneCombination {

    private static final long serialVersionUID = 1L;

    /**
     * 排序编号/系统编号
     */
    @TableId(value = "order_no")
    private Long orderNo;
    /**
     * 分区组合编号
     */
    private Long zoneId;
    // 分区组合名称
    private String name;
    /**
     * 对应的分区编号集合
     */
    private String zoneList;

}
