package com.ruoyi.mqtt03.addr03.handler.addr0XA8E5;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0XA8E5 {
    private String ip;
    private String addr;
    private List<Data> data1;
    private List<Data> data2;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        private int controlId;
        private Boolean enabled;
    }
}
