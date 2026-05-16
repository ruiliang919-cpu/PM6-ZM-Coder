package com.ruoyi.zm.domain.vo;

import lombok.Data;

@Data
public class DevZoneCombinationOneVo {
    /**
     * 分区组合编号
     */
    private Long zoneId;
    // 分区组合名称
    private String name;
    /**
     * 对应的分区编号数组
     */
    private String[] zoneList;
}
