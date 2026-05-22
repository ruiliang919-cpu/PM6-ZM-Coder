package com.ruoyi.mqtt.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MqBody {
    private Integer deviceNo;
    private String end;
    private String body;
}
