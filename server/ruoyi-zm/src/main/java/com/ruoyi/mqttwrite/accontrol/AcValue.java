package com.ruoyi.mqttwrite.accontrol;

import com.ruoyi.zm.domain.DevConfigTimeControlAc;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcValue {
    private Long msgId = IdGenerator.UUIDId();
    private String addr = "0XAF1E";
    private int acSwitchNum = 8;
    private List<DevConfigTimeControlAc> data;
}
