package com.ruoyi.mqttwrite.loop;

import com.ruoyi.web.controller.zm.WriteController;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLoop {
    private Long msgId = IdGenerator.UUIDId();
    private String addr;

    private D data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class D {
        private int id;
        private Integer[] value = WriteController.DELETE_LOOP_NOS;
    }
}
