package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DevBaseDeviceReqBo {
    private Long id;
    @NotBlank(message = "设备名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;
    @NotNull(message = "设备区域不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long regionId;
    @NotBlank(message = "设备IP地址不能为空", groups = {AddGroup.class, EditGroup.class})
    private String ip;
    @NotNull(message = "机柜端口号不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer port;
    @NotNull(message = "机柜主机号不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer deviceId;
    @NotNull(message = "机柜运行模式不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer runMode;
    @NotNull(message = "直流模块个数不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer dcModuleNum;
    @NotNull(message = "调光模块个数不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer dimmerNum;
    @NotNull(message = "设备类型不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer type;
}
