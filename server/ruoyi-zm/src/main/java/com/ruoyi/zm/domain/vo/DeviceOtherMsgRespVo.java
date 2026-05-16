package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOtherMsgRespVo {
    private String name;
    private Integer zoneId;
    private String area;
    private String ip;
    private Integer deviceId;
    private Integer host;
}
