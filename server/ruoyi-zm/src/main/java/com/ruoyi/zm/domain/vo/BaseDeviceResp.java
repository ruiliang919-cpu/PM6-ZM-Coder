package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseDeviceResp {
    private Integer id;
    private String name;
    private String area;
    private String ip;
    private Integer port;
    private Integer deviceId;
    private Integer runMode;
    private Integer dcModuleNum;
    private Integer dimmerNum;
    private Integer type;
    /**
     * 排序字段
     */
    private Integer orderNo;
    private Integer deviceNo;
}
