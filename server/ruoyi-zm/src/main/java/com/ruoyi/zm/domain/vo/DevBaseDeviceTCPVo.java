package com.ruoyi.zm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevBaseDeviceTCPVo {
    private Long id;
    // private Long deviceNo;
    private String ip;
    private Integer port;
    private Integer onlineStatus;
    private String version;
    private Date lastTime;
}
