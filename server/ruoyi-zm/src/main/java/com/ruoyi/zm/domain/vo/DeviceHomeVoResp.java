package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceHomeVoResp {
    private Integer orderNo;
    private Integer deviceId;
    private String deviceName;
    private String area;
    private Integer runStatus;
    private Integer deviceStatus;
    private Integer onlineStatus;
    private Integer runMode;
    private BigDecimal dcBusVoltage;
    private Integer dimmerNum;
    private Integer dcModuleNum;
    private Integer acSwitchNum;
    private String version;
}
