package com.ruoyi.zm.domain.vo;

import lombok.Data;

@Data
public class DevZoneCombinationRespVo {
    /**
     * 排序编号/系统编号
     */
    private Long orderNo;
    /**
     * 分区组合编号
     */
    private Long zoneId;
    // 分区组合名称
    private String name;
    /**
     * 对应的分区编号集合
     */
    private String[] zoneList;
}
