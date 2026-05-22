package com.ruoyi.mqttwrite.loopcontrol;

import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoopControlVO {
    private Long msgId = IdGenerator.UUIDId();
    private String addr = "0xC046";
    // 1/2/3 : 1为设置开关；2为设置亮度；3为设置开关+亮度
    private int module;
    private D data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class D {
        private int value;
        private int lux;
        private int[] loopArr;
        private int[] groupArr;
    }
}
