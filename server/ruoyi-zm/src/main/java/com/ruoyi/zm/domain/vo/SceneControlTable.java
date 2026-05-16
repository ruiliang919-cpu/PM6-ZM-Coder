package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneControlTable {
    // 时段ID
    private Integer timeFrameId;
    // 使能
    private Integer enabledStatus;
    // 场景选择
    private Integer sceneSelect;
    // 开始时间 例如："12:00"
    private String stime;
    // 结束时间 例如："14:44"
    private String etime;
}
