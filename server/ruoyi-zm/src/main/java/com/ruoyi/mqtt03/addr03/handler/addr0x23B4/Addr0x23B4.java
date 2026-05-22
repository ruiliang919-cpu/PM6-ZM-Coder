package com.ruoyi.mqtt03.addr03.handler.addr0x23B4;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0x23B4 {
    private String ip;
    private String addr;
    private Data data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Data{
        private Integer[] group;
        private int num;
    }
}
