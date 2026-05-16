package com.ruoyi.zm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 正极支路与负极支路绝缘告警记录对象 dev_base_branch_insulation
 *
 * @author Lion Li
 * @date 2024-08-21
 */
@Data
@TableName("dev_base_branch_insulation")
public class DevBaseBranchInsulation implements Serializable {


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
     * 支路编号（正级/负极）
     */
    private Long branchNo;

    /**
     * 正极支路绝缘告警 0：正常 1：故障
     */
    private Long positiveBranchInsulation;

    /**
     * 负极支路绝缘告警 0：正常 1：故障
     */
    private Long negativeBranchInsulation;


}
