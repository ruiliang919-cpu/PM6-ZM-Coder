package com.ruoyi.mqttwrite.module;


import com.ruoyi.mqtt03.addr03.handler.addr0XB715.Addr0XB715;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleSelect {
    private Long msgId = IdGenerator.UUIDId();
    private String addr = "0XB715";
    private List<Addr0XB715.Data> data;
}
