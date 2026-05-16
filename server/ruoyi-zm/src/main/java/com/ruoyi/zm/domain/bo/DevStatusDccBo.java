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
 * 直流信息业务对象 dev_status_dcc
 *
 * @author ruoyi
 * @date 2024-07-19
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DevStatusDccBo extends BaseEntity {

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
     * 编号
     */
    @NotBlank(message = "编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String no;

    /**
     * 运行状态（0故障; 1正常）
     */
    @NotNull(message = "运行状态（0故障; 1正常）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer runStatus;

    /**
     * 通信状态（0故障; 1正常）
     */
    @NotNull(message = "通信状态（0故障; 1正常）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer onlineStatus;

    /**
     * 开关机状态（0关; 1开）
     */
    @NotNull(message = "开关机状态（0关; 1开）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer onOffStatus;



    /**
     * 输出电压
     */@NotNull(message = "输出电压不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal outputVoltage;
    /**
     * 输出电流
     */     @NotNull(message = "输出电流不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal outputCurrent;


}
