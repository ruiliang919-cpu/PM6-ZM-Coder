package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 支路信息记录对象 dev_status_branch
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
@TableName("dev_status_branch")
public class DevStatusBranch implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备编号
     */
    private Long deviceId;

    /**
     * 支路编号
     */
    private Long branchNo;

    /**
     * 支路开关状态 0：关 1：开
     */
    private Long switchStatus;

    /**
     * 支路开关故障 0：正常 1：故障
     */
    private Long switchFault;


}
