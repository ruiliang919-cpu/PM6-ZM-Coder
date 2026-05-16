package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 直流回路业务对象 dev_status_dcc_loop
 *
 * @author ruoyi
 * @date 2024-07-19
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DevStatusDccLoopBo extends BaseEntity {

    /**
     * 系统编号
     */
    @NotNull(message = "系统编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 设备id
     */
    @NotNull(message = "设备id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long deviceId;

    /**
     * 回路编号，从协议读取
     */
    @NotBlank(message = "回路编号，从协议读取不能为空", groups = { AddGroup.class, EditGroup.class })
    private String no;

    /**
     * 分组编号,与config_group编号对应
     */
    @NotNull(message = "分组编号,与config_group编号对应不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long groupId;

    /**
     * 亮度设定值
     */
    @NotNull(message = "亮度设定值不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long brightnessSetting;

    /**
     * 亮度反馈值
     */
    @NotNull(message = "亮度反馈值不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long brightnessFeedback;

    /**
     * 输出电压
     */
    @NotNull(message = "输出电压不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal outputVoltage;

    /**
     * 输出电流
     */
    @NotNull(message = "输出电流不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal outputCurrent;

    /**
     * 内部温度（摄氏度）
     */
    @NotNull(message = "内部温度（摄氏度）不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal internalTemperature;

    /**
     * 开关设定
     */
    @NotBlank(message = "开关设定不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer switchSetting;

    /**
     * 开关反馈
     */
    @NotBlank(message = "开关反馈不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer switchFeedback;

    /**
     * 实时功率
     */
    private BigDecimal realTime;


}
