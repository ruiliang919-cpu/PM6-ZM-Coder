package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 支路信息记录业务对象 dev_status_branch
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
public class DevStatusBranchBo {

    /**
     * 系统编号
     */
    @NotNull(message = "系统编号不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 设备编号
     */
    @NotNull(message = "设备编号不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long deviceId;

    /**
     * 支路编号
     */
    @NotNull(message = "支路编号不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long branchNo;

    /**
     * 支路开关状态 0：关 1：开
     */
    @NotNull(message = "支路开关状态 0：关 1：开不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long switchStatus;

    /**
     * 支路开关故障 0：正常 1：故障
     */
    @NotNull(message = "支路开关故障 0：正常 1：故障不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long switchFault;


}
