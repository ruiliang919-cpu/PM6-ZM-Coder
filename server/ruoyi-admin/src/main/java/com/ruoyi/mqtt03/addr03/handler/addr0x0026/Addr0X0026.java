package com.ruoyi.mqtt03.addr03.handler.addr0x0026;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0X0026 {
    private String ip;
    private String addr;
    private long time;
    private int num;
    private List<Data> data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Data{
        private Integer no;
        private String voltage;
        private String current;
    }
}
