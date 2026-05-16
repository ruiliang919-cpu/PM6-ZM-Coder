package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupPartitionVo {
    // 分区编号
    private Integer partitionNo;
    // 分区名称
    private String partitionName;
    // 分组编号
    private Integer groupNo;
}
