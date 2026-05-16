package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * PM6U故障记录视图对象 dev_pm6u_fault
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
@ExcelIgnoreUnannotated
public class DevPm6uFaultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @ExcelProperty(value = "系统编号")
    private Long id;

    /**
     * 设备编号
     */
    @ExcelProperty(value = "设备编号")
    private Long deviceId;

    /**
     * PM6US交流一路输入状态 0：正常 1：故障
     */
    @ExcelProperty(value = "PM6US交流一路输入状态 0：正常 1：故障")
    private Long loopOneInput;

    /**
     * PM6US交流二路输入状态 0：正常 1：故障
     */
    @ExcelProperty(value = "PM6US交流二路输入状态 0：正常 1：故障")
    private Long loopTwoInput;

    /**
     * PM6US交流一路掉电
     */
    @ExcelProperty(value = "PM6US交流一路掉电")
    private Long loopOneDump;

    /**
     * PM6US交流一路欠压
     */
    @ExcelProperty(value = "PM6US交流一路欠压")
    private Long loopOneUndervoltage;

    /**
     * PM6US交流一路过压
     */
    @ExcelProperty(value = "PM6US交流一路过压")
    private Long loopOneOvervoltage;

    /**
     * PM6US交流一路缺相
     */
    @ExcelProperty(value = "PM6US交流一路缺相")
    private Long loopOneDefaultphase;

    /**
     * PM6US交流二路掉电
     */
    @ExcelProperty(value = "PM6US交流二路掉电")
    private Long loopTwoDump;

    /**
     * PM6US交流二路欠压
     */
    @ExcelProperty(value = "PM6US交流二路欠压")
    private Long loopTwoUndervoltage;

    /**
     * PM6US交流二路过压
     */
    @ExcelProperty(value = "PM6US交流二路过压")
    private Long loopTwoOvervoltage;

    /**
     * PM6US交流二路缺相
     */
    @ExcelProperty(value = "PM6US交流二路缺相")
    private Long loopTwoDefaultphase;

    /**
     * 母线正极绝缘故障
     */
    @ExcelProperty(value = "母线正极绝缘故障")
    private Long busPositiveInsulation;

    /**
     * 母线负极绝缘故障
     */
    @ExcelProperty(value = "母线负极绝缘故障")
    private Long busNegativeInsulation;

    /**
     * 支路正极绝缘故障
     */
    @ExcelProperty(value = "支路正极绝缘故障")
    private Long branchPositiveInsulation;

    /**
     * 支路负极绝缘故障
     */
    @ExcelProperty(value = "支路负极绝缘故障")
    private Long branchNegativeInsulation;

    /**
     * 母线差压故障
     */
    @ExcelProperty(value = "母线差压故障")
    private Long busDifferentialPressure;

    /**
     * 支路开关总故障
     */
    @ExcelProperty(value = "支路开关总故障")
    private Long branchSwitchFault;

    /**
     * 母线正极交窜直故障
     */
    @ExcelProperty(value = "母线正极交窜直故障")
    private Long busPositiveAcdcFault;

    /**
     * 母线负极交窜直故障
     */
    @ExcelProperty(value = "母线负极交窜直故障 ")
    private Long busNegativeAcdcFault;

    /**
     * 支路交窜直故障
     */
    @ExcelProperty(value = "支路交窜直故障")
    private Long branchAcdcFault;

    /**
     * 母线过压故障
     */
    @ExcelProperty(value = "母线过压故障")
    private Long busOvervoltage;

    /**
     * 母线欠压故障
     */
    @ExcelProperty(value = "母线欠压故障")
    private Long busUndervoltage;

    /**
     * 系统总故障
     */
    @ExcelProperty(value = "系统总故障")
    private Long systemFault;

    /**
     * 系统总开关状态 1：开启 0：关闭
     */
    @ExcelProperty(value = "系统总开关状态 1：开启 0：关闭")
    private Long systemSwitch;

    /**
     * 系统工作模式 1：手动 0：自动
     */
    @ExcelProperty(value = "系统工作模式 1：手动 0：自动")
    private Long systemModule;

    /**
     * BTC单元有无
     */
    @ExcelProperty(value = "BTC单元有无")
    private Long btc;


}
