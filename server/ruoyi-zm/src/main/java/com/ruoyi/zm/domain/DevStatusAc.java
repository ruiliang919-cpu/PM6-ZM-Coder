package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 交流信息对象 dev_status_ac
 *
 * @author ruoyi
 * @date 2024-09-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dev_status_ac")
public class DevStatusAc extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 系统编号
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 设备id
     */
    private Long deviceId;
    /**
     * 电路数：1路；2路
     */
    private Long circuitNum;
    /**
     * AB线电压
     */
    private BigDecimal uab;
    /**
     * BC线电压
     */
    private BigDecimal ubc;
    /**
     * CA线电压
     */
    private BigDecimal uac;
    /**
     * A相电流
     */
    private BigDecimal ia;
    /**
     * B相电流
     */
    private BigDecimal ib;
    /**
     * C相电流
     */
    private BigDecimal ic;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

}
