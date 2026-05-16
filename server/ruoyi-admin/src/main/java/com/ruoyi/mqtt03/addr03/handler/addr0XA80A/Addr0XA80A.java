package com.ruoyi.mqtt03.addr03.handler.addr0XA80A;

import com.ruoyi.zm.domain.DevConfigSimpleGroup;
import com.ruoyi.zm.domain.DevConfigTimeControl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0XA80A {
    private String ip;
    private String addr;
    private int controlId;
    private Boolean enabled;
    private LinkedList<DevConfigTimeControl> data1;
    private LinkedList<DevConfigSimpleGroup> data2;
}
