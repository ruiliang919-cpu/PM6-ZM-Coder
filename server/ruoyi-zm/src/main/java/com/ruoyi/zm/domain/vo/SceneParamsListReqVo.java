package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneParamsListReqVo {
    private Integer deviceId;
    private Integer sceneId;
    private Integer pageNum;
    private Integer pageSize;
}
