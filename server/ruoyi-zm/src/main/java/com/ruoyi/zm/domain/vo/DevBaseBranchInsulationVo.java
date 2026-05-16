package com.ruoyi.zm.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import lombok.Data;

import java.io.Serializable;

/**
 * 正极支路与负极支路绝缘告警记录视图对象 dev_base_branch_insulation
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
@ExcelIgnoreUnannotated
public class DevBaseBranchInsulationVo implements Serializable {

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
     * 支路编号（正级/负极）
     */
    @ExcelProperty(value = "支路编号", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "正级/负极")
    private Long branchNo;

    /**
     * 正极支路绝缘告警 0：正常 1：故障
     */
    @ExcelProperty(value = "正极支路绝缘告警 0：正常 1：故障")
    private Long positiveBranchInsulation;

    /**
     * 负极支路绝缘告警 0：正常 1：故障
     */
    @ExcelProperty(value = "负极支路绝缘告警 0：正常 1：故障")
    private Long negativeBranchInsulation;


}
