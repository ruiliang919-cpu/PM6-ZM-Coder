package com.ruoyi.zm.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseDeviceReqBo {
    // @NotNull(message = "系统编号不能为空", groups = {EditGroup.class})
    private Integer id;
    // @NotBlank(message = "机柜名称不能为空", groups = {EditGroup.class, AddGroup.class})
    private String name;
    // @NotNull(message = "机柜区域ID不能为空", groups = {EditGroup.class, AddGroup.class})
    private Integer regionId;
    // @NotBlank(message = "机柜IP不能为空", groups = {EditGroup.class, AddGroup.class})
    private String ip;
    // @NotNull(message = "机柜端口号不能为空", groups = {EditGroup.class, AddGroup.class})
    private Integer port;
    // @NotNull(message = "机柜主机号不能为空", groups = {EditGroup.class, AddGroup.class})
    private Integer deviceId;
    // @NotNull(message = "机柜运行模式不能为空", groups = {EditGroup.class, AddGroup.class})
    private Integer runMode;
    // @NotNull(message = "直流模块个数不能为空", groups = {EditGroup.class, AddGroup.class})
    private Integer dcModuleNum;
    // @NotNull(message = "调光模块个数不能为空", groups = {EditGroup.class, AddGroup.class})
    private Integer dimmerNum;
    // @NotNull(message = "机柜类型不能为空", groups = {EditGroup.class, AddGroup.class})
    private Integer type;
}
