package com.ruoyi.zm.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class WriteLoopReqVo {
    private Integer deviceId;
    private Integer lux;
    private Integer switchStatus;
    private List<Integer> loopControl;
}

