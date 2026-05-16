package com.ruoyi.zm.domain.vo;

import lombok.Data;

@Data
public class SceneParamsReqVo {
    private Integer deviceId;
    private Integer sceneId;
    private String name;
    // 选中的分组ID
    private Integer[] selectGroupIdArr;
    // 选中的分组亮度
    private Integer[] selectLuxArr;
    // 选中的分组开关
    private Integer[] selectSwitchArr;
}
