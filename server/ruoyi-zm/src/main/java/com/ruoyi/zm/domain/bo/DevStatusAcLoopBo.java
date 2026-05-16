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
 * 交流回路业务对象 dev_status_ac_loop
 *
 * @author ruoyi
 * @date 2024-08-24
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class DevStatusAcLoopBo extends BaseEntity {

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
     * 开关设定值(0 关闭 1打开)
     */
    @NotNull(message = "开关设定值(0 关闭 1打开)不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer switchSetting;

    /**
     * 开关反馈值(0 关闭 1打开)
     */
    @NotNull(message = "开关反馈值(0 关闭 1打开)不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer switchFeedback;

    /**
     * 整流模块输出电压
     */
    @NotNull(message = "整流模块输出电压不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal outputVoltage;

    /**
     * 整流模块输出电流
     */
    @NotNull(message = "整流模块输出电流不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal outputCurrent;

    /**
     * 分组编号,与config_group编号对应
     */
    @NotNull(message = "分组编号,与config_group编号对应不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long groupId;


}
