package com.ruoyi.mqtt03.addr03.handler.addr0xA5AE;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0xA5AE {
    private String ip;
    private String addr;
    private List<Data> data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Data{
        private int id;
        private int no;
    }
}
