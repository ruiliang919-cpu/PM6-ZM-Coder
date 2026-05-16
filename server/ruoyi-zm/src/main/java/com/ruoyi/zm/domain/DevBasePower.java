package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 机柜耗电量 功率 对象 dev_base_power
 *
 * @author ruoyi
 * @date 2024-08-29
 */
@Data

@TableName("dev_base_power")
public class DevBasePower implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 设备ID
     */
    private Integer deviceId;
    /**
     * 类型 1:总电量 2:日耗电量 3:周耗电量 4:月耗电量 5:季耗电量 6:年耗电量 7:总功率
     */
    private Integer type;
    /**
     * 数值
     */
    private BigDecimal value;
    /**
     * 时间戳
     */
    private Long timestamp;

}
