package com.ruoyi.mqtt.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TelecommandBody {
    private String ip;
    private boolean[] data;
}
