package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 分组回路编号业务对象 dev_group_loop
 *
 * @author Lion Li
 * @date 2024-08-20
 */
@Data
public class DevGroupLoopBo {

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
     * 分组ID
     */
    @NotNull(message = "分组ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long groupId;

    /**
     * 分组回路编号
     */
    @NotNull(message = "分组回路编号不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long loopNo;


}
