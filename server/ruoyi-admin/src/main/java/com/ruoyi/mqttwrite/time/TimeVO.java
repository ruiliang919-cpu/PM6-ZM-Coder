package com.ruoyi.mqttwrite.time;

import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeVO {
    private Long msgId = IdGenerator.UUIDId();
    private String addr = "0xA000";
    private long data = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
}
