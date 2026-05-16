package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 告警记录业务对象 dev_fault_record
 *
 * @author ruoyi
 * @date 2024-09-04
 */

@Data

public class DevFaultRecordBo {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Integer id;

    /**
     * 设备编号 主机号
     */
    @NotNull(message = "设备编号 主机号不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer deviceId;

    /**
     * 告警/事件信息
     */
    @NotBlank(message = "告警/事件信息不能为空", groups = {AddGroup.class, EditGroup.class})
    private String message;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long stime;

    /**
     * 结束时间
     */
    private Long etime;

    /**
     * 是否显示 0不显示 1显示
     */
    @NotNull(message = "是否显示 0不显示 1显示不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer showType;

    /**
     * 类型 1告警记录 2事件记录
     */
    @NotNull(message = "类型 1告警记录 2事件记录不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer type;


}
