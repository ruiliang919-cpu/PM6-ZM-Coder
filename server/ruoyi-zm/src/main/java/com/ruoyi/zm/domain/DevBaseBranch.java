package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 馈线支路名称对象 dev_bash_branch
 *
 * @author Lion Li
 * @date 2024-08-20
 */
@Data
@TableName("dev_base_branch")
public class DevBaseBranch implements Serializable {

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
     * 馈线支路编号
     */
    private Long branchNo;

    /**
     * 馈线支路名称
     */
    private String branchName;


}
