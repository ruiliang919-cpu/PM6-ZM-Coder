package com.ruoyi.mqtt03.addr03.handler.addr0x2100;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0x2100 {
    private String ip;
    private String addr;
    private int select;
    private List<Data> data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Data{
        private int sensorId;
        private int illuminanceLux;
        private Boolean outControlStatus;
        private String outControlChannel;
        private String outControlAddr;
    }
}
