package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 馈线支路名称业务对象 dev_bash_branch
 *
 * @author Lion Li
 * @date 2024-08-20
 */
@Data
public class DevBaseBranchBo {

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
     * 馈线支路编号
     */
    @NotNull(message = "馈线支路编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long branchNo;

    /**
     * 馈线支路名称
     */
    @NotBlank(message = "馈线支路名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String branchName;


}
