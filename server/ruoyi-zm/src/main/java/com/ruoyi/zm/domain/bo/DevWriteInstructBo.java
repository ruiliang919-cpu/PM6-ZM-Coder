package com.ruoyi.zm.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 写入协议的指令下发业务对象 dev_write_instruct
 *
 * @author ruoyi
 * @date 2024-09-05
 */

@Data

public class DevWriteInstructBo {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 设备IP
     */
    @NotBlank(message = "设备IP不能为空", groups = {AddGroup.class, EditGroup.class})
    private String ip;

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long salveId;

    /**
     * 功能码
     */
    @NotNull(message = "功能码不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long code;

    /**
     * 协议地址
     */
    @NotBlank(message = "协议地址不能为空", groups = {AddGroup.class, EditGroup.class})
    private String addr;

    /**
     * 操作的地址数量
     */
    @NotNull(message = "操作的地址数量不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long addrNum;

    /**
     * 写入地址的值
     */
    @NotBlank(message = "写入地址的值不能为空", groups = {AddGroup.class, EditGroup.class})
    private String writeValue;

    /**
     * 类型 1：发送指令 2：接收指令 4：机柜初始化指令
     */
    @NotNull(message = "类型 1：发送指令 2：接收指令 4：机柜初始化指令 不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long type;

    /**
     * 反馈 0：正常 1：异常 2：在等待队列中
     */
    @NotNull(message = "反馈 0：正常 1：异常 2：在等待队列中不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long feedback;

    /**
     * 时间戳
     */
    @NotNull(message = "时间戳不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long timestamp;


}
