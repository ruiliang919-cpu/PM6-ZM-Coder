package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleControlTable {
    private Integer controlId;
    // 时段ID
    private Integer frameId;
    private Integer lux;
    // 开关
    private Integer switchStatus;
    // 使能
    private Integer enabledStatus;
    // 开始时间 例如："12:31"
    private String stime;
    // 结束时间 例如："14:44"
    private String etime;
}
