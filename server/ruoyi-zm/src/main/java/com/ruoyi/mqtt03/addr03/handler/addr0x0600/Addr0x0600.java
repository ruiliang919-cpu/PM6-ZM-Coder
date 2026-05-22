package com.ruoyi.mqtt03.addr03.handler.addr0x0600;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0x0600 {
    private String ip;
    private String addr;
    private int num;
    private List<Data> data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Data{
        private int no;
        private String name;
        private String current;
    }
}
