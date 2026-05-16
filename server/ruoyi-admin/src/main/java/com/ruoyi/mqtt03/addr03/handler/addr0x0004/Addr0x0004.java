package com.ruoyi.mqtt03.addr03.handler.addr0x0004;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0x0004 {
    private String ip;
    private String addr;
    private long time;
    private List<Data> data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Data {
        private Integer type;
        private String value;
        private String unit;
    }
}
