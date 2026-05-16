package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 正极支路与负极支路绝缘告警记录业务对象 dev_base_branch_insulation
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
public class DevBaseBranchInsulationBo {

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
     * 支路编号（正级/负极）
     */
    @NotNull(message = "支路编号（正级/负极）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long branchNo;

    /**
     * 正极支路绝缘告警 0：正常 1：故障
     */
    @NotNull(message = "正极支路绝缘告警 0：正常 1：故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long positiveBranchInsulation;

    /**
     * 负极支路绝缘告警 0：正常 1：故障
     */
    @NotNull(message = "负极支路绝缘告警 0：正常 1：故障不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long negativeBranchInsulation;


}
