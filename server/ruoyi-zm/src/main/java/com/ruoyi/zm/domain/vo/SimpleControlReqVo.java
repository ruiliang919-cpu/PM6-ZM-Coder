package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleControlReqVo {
    private Integer deviceId;
    // 启用按钮旁边的时控ID
    private Integer controlId;
    // 启用状态，true启用 false不启用
    private Boolean enabled;
    // 选中的分组ID
    private Integer[] groupIds;
    // 整个时序表
    private List<SimpleControlTable> table;
}

