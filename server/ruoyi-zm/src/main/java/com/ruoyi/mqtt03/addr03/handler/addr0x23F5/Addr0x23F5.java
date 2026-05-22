package com.ruoyi.mqtt03.addr03.handler.addr0x23F5;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0x23F5 {
    private String ip;
    private String addr;
    private List<Data> data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Data{
        private int groupId;
        private String name;
        private Integer[] value;
        private int loopNum;
    }
}
