package com.ruoyi.mqtt03.addr03.handler.addr0x0022;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0X0022 {
    private String ip;
    private String addr;
    private long time;
    private int luxNum;
    private int switchNum;
    private List<Data> data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Data{
        private Integer no;
        private int brightnessSetting;
        private int brightnessFeedback;
        private String outputVoltage;
        private String outputCurrent;
        private String internalTemperature;
//        private int switchSetting;
//        private int switchFeedback;
        private Double realTime;
    }
}
