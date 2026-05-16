package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 直流信息对象 dev_status_dcc
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dev_status_dcc")
public class DevStatusDcc extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 系统编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 设备id
     */
    private Long deviceId;
    /**
     * 编号
     */
    private String no;
    /**
     * 运行状态（0故障; 1正常）
     */
    private Integer runStatus;
    /**
     * 通信状态（0故障; 1正常）
     */
    private Integer onlineStatus;
    /**
     * 开关机状态（0关; 1开）
     */
    private Integer onOffStatus;
    /**
     * 输出电压
     */
    private BigDecimal outputVoltage;
    /**
     * 输出电流
     */
    private BigDecimal outputCurrent;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

}
