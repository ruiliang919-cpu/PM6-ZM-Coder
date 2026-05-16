package com.ruoyi.mqttwrite.common;

import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonDataString {
    private Long msgId = IdGenerator.UUIDId();
    private String addr;
    private D data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class D {
        private int id;
        private String value;
    }
}
