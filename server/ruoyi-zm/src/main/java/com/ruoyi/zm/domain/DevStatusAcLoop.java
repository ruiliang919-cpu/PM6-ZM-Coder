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
 * 交流回路对象 dev_status_ac_loop
 *
 * @author ruoyi
 * @date 2024-08-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dev_status_ac_loop")
public class DevStatusAcLoop extends BaseEntity {

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
     * 回路编号，从协议读取
     */
    private String no;
    /**
     * 开关设定值(0 关闭 1打开)
     */
    private Integer switchSetting;
    /**
     * 开关反馈值(0 关闭 1打开)
     */
    private Integer switchFeedback;
    /**
     * 整流模块输出电压
     */
    private BigDecimal outputVoltage;
    /**
     * 整流模块输出电流
     */
    private BigDecimal outputCurrent;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;
    /**
     * 分组编号,与config_group编号对应
     */
    private Long groupId;

}
