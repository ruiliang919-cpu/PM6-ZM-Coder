package com.ruoyi.mqtt03.addr03.handler.addr0x001A;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0x001A {
    private String ip;
    private String addr;
    private List<Data> data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Data{
        private String addr;
        private String value;
    }
}
