package com.ruoyi.mqtt03.addr03.handler.addr0XAAEE;

import com.ruoyi.zm.domain.DevConfigTimeControlScene;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0XAAEE {
    private String ip;
    private String addr;
    private int controlId;
    private Boolean enabled;
    private LinkedList<DevConfigTimeControlScene> data;
}
