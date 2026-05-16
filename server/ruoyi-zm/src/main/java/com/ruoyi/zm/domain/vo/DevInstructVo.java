package com.ruoyi.zm.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevInstructVo {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 设备IP
     */
    private String ip;

    /**
     * 设备ID
     */
    private Integer salveId;

    /**
     * 功能码
     */
    private Integer code;

    /**
     * 协议地址
     */
    private String addr;

    /**
     * 操作的地址数量
     */
    private Integer addrNum;

    /**
     * 写入地址的值
     */
    private String writeValue;

    /**
     * 类型 1：发送指令 2：接收指令
     */
    private Integer type;

    /**
     * 反馈 0：正常 1：异常 2：在等待队列中
     */
    private Integer feedback;

    /**
     * 时间戳
     */
    private Long timestamp;
}
