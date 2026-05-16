package com.ruoyi.zm.domain.bo;


import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 调光版故障记录业务对象 dev_dimmer_plate_fault
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
public class DevDimmerPlateFaultBo {

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
     * 调光模块编号
     */
    @NotNull(message = "调光模块编号不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long moduleNo;

    /**
     * 故障判定 0：正常 1：故障
     */
    @NotNull(message = "故障判定 0：正常 1：故障不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long faultDecide;


}
