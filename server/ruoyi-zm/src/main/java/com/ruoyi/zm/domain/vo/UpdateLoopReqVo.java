package com.ruoyi.zm.domain.vo;

import lombok.Data;

@Data
public class UpdateLoopReqVo {
    private Integer deviceId;
    private Integer groupId;
    private String groupName;
    private Short zoneId;
    private Integer[] loopNo;
}
