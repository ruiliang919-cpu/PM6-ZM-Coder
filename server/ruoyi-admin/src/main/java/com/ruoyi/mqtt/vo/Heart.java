package com.ruoyi.mqtt.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Heart {
    private String ip;
    private Data data;

    @Getter
    @Setter
    @ToString
    public static class Data {
        private Boolean online;
        private String version;
        private Long time;
    }
}
