package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 馈线支路响应体
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchRespVo {
    /**
     * 系统编号
     */
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
    /**
     * 正极支路绝缘告警 0：正常 1：故障
     */
    private Integer positiveBranchInsulation;
    /**
     * 负极支路绝缘告警 0：正常 1：故障
     */
    private Integer negativeBranchInsulation;
    /**
     * 馈线支路开关状态 0：关 1：开
     */
    private Long switchStatus;
    /**
     * 馈线支路开关故障 0：正常 1：故障
     */
    private Long switchFault;

}
