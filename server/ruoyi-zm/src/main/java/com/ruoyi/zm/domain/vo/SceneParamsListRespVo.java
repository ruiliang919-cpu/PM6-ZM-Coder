package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneParamsListRespVo {
    private Integer groupId;
    private Integer selectStatus;
    private String groupName;
    private Long lux;
    private Integer btnStatus;
}
