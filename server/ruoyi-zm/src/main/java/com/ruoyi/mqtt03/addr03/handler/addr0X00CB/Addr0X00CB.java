package com.ruoyi.mqtt03.addr03.handler.addr0X00CB;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0X00CB {
    private String ip;
    private String addr;
    private long time;
    private List<Data> data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Data{
        private Integer no;
        private String value1;
        private String value2;
        private String value3;
        private String value4;
        private String value5;
    }
}
