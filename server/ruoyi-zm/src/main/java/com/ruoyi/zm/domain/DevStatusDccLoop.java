package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 直流回路对象 dev_status_dcc_loop
 *
 * @author ruoyi
 * @date 2024-07-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dev_status_dcc_loop")
public class DevStatusDccLoop extends BaseEntity {

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
     * 分组编号,与config_group编号对应
     */
    private Long groupId;
    /**
     * 亮度设定值
     */
    private Long brightnessSetting;
    /**
     * 亮度反馈值
     */
    private Long brightnessFeedback;
    /**
     * 输出电压
     */
    private BigDecimal outputVoltage;
    /**
     * 输出电流
     */
    private BigDecimal outputCurrent;
    /**
     * 内部温度（摄氏度）
     */
    private BigDecimal internalTemperature;
    /**
     * 开关设定
     */
    private Integer switchSetting;
    /**
     * 开关反馈
     */
    private Integer switchFeedback;
    /**
     * 实时功率
     */
    private BigDecimal realTime;

    /**
     * 调光控制列表的选中状态 0不选中 1选中  null 未选中
     */
    private Integer selectStatus;

    /**
     * 类型，为1显示亮度反馈值，即为直流调光回路
     * 为2则不显示亮度反馈值，只显示开关反馈值，即为直流开关回路，只有开关
     */
    @TableField(exist = false)
    private Integer type;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

}
