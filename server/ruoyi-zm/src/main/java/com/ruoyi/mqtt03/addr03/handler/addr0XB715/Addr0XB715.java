package com.ruoyi.mqtt03.addr03.handler.addr0XB715;

import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0XB715 {
    private Long msgId = IdGenerator.UUIDId();
    private String ip;
    private String addr = "0XB715";
    private Data data;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data{
        private String timeModule;
        private String handModule;
        private Boolean illuminanceSensorModule;
        private Boolean infraredSensorModule;
    }
}
