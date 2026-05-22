package com.ruoyi.mqtt03.addr03.handler.addr0XAF1E;

import com.ruoyi.zm.domain.DevConfigTimeControlAc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0XAF1E {
    private String ip;
    private String addr;
    private int acSwitchNum;
    private LinkedList<DevConfigTimeControlAc> data;
}
