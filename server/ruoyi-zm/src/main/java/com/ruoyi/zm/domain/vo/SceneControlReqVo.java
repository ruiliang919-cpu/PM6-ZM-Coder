package com.ruoyi.zm.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class SceneControlReqVo {
    private Integer deviceId;
    // 启用按钮旁边的时控ID
    private Integer controlId;
    // 启用状态，true启用 false不启用
    private Boolean enabled;
    // 整个时序表
    private List<SceneControlTable> table;
}
