package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * PM6U故障记录业务对象 dev_pm6u_fault
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
public class DevPm6uFaultBo {

    /**
     * 系统编号
     */
    @NotNull(message = "系统编号不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 设备编号
     */
    @NotNull(message = "设备编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long deviceId;

    /**
     * PM6US交流一路输入状态 0：正常 1：故障
     */
    @NotNull(message = "PM6US交流一路输入状态 0：正常 1：故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long loopOneInput;

    /**
     * PM6US交流二路输入状态 0：正常 1：故障
     */
    @NotNull(message = "PM6US交流二路输入状态 0：正常 1：故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long loopTwoInput;

    /**
     * PM6US交流一路掉电
     */
    @NotNull(message = "PM6US交流一路掉电不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long loopOneDump;

    /**
     * PM6US交流一路欠压
     */
    @NotNull(message = "PM6US交流一路欠压不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long loopOneUndervoltage;

    /**
     * PM6US交流一路过压
     */
    @NotNull(message = "PM6US交流一路过压不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long loopOneOvervoltage;

    /**
     * PM6US交流一路缺相
     */
    @NotNull(message = "PM6US交流一路缺相不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long loopOneDefaultphase;

    /**
     * PM6US交流二路掉电
     */
    @NotNull(message = "PM6US交流二路掉电不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long loopTwoDump;

    /**
     * PM6US交流二路欠压
     */
    @NotNull(message = "PM6US交流二路欠压不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long loopTwoUndervoltage;

    /**
     * PM6US交流二路过压
     */
    @NotNull(message = "PM6US交流二路过压不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long loopTwoOvervoltage;

    /**
     * PM6US交流二路缺相
     */
    @NotNull(message = "PM6US交流二路缺相不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long loopTwoDefaultphase;

    /**
     * 母线正极绝缘故障
     */
    @NotNull(message = "母线正极绝缘故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long busPositiveInsulation;

    /**
     * 母线负极绝缘故障
     */
    @NotNull(message = "母线负极绝缘故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long busNegativeInsulation;

    /**
     * 支路正极绝缘故障
     */
    @NotNull(message = "支路正极绝缘故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long branchPositiveInsulation;

    /**
     * 支路负极绝缘故障
     */
    @NotNull(message = "支路负极绝缘故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long branchNegativeInsulation;

    /**
     * 母线差压故障
     */
    @NotNull(message = "母线差压故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long busDifferentialPressure;

    /**
     * 支路开关总故障
     */
    @NotNull(message = "支路开关总故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long branchSwitchFault;

    /**
     * 母线正极交窜直故障
     */
    @NotNull(message = "母线正极交窜直故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long busPositiveAcdcFault;

    /**
     * 母线负极交窜直故障
     */
    @NotNull(message = "母线负极交窜直故障 不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long busNegativeAcdcFault;

    /**
     * 支路交窜直故障
     */
    @NotNull(message = "支路交窜直故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long branchAcdcFault;

    /**
     * 母线过压故障
     */
    @NotNull(message = "母线过压故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long busOvervoltage;

    /**
     * 母线欠压故障
     */
    @NotNull(message = "母线欠压故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long busUndervoltage;

    /**
     * 系统总故障
     */
    @NotNull(message = "系统总故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long systemFault;

    /**
     * 系统总开关状态 1：开启 0：关闭
     */
    @NotNull(message = "系统总开关状态 1：开启 0：关闭不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long systemSwitch;

    /**
     * 系统工作模式 1：手动 0：自动
     */
    @NotNull(message = "系统工作模式 1：手动 0：自动不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long systemModule;

    /**
     * BTC单元有无
     */
    @NotNull(message = "BTC单元有无不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long btc;


}
