package com.ruoyi.mqttwrite.module;


import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleValue {
    private Long msgId = IdGenerator.UUIDId();
    private String addr = "0xA007";
    private String acdc;
    private String dcdc;
}
