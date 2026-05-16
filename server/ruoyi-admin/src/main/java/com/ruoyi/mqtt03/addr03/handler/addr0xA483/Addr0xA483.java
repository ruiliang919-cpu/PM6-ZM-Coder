package com.ruoyi.mqtt03.addr03.handler.addr0xA483;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0xA483 {
    private String ip;
    private String addr;
    private List<Data> data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        private int sceneId;
        private String name;
        private Integer[] groupIds;
        private Integer[] luxArr;
        private Boolean[] switchArr;
        private Integer[] switchIntArr;
    }
}
