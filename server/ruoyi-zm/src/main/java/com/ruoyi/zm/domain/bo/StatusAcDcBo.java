package com.ruoyi.zm.domain.bo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StatusAcDcBo extends BaseEntity {
    /**
     * 系统编号
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 设备ID
     */
    private Long deviceId;
    /**
     * 回路编号
     */
    private String no;
    /**
     * 交窜直支路信息
     */
    private String acdcInfo;
    /**
     * 故障类型
     */
    private String faultType;
    /**
     * 支路位置
     */
    private String branchLocation;
    /**
     * 故障判定 0：正常 1：故障
     */
    private Integer fault_decide;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;
}
