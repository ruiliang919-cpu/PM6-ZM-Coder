package com.ruoyi.mqtt03.addr03.handler.addr0XAE7A;

import com.ruoyi.zm.domain.DevConfigInfraredSensor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0XAE7A {
    private String ip;
    private String addr;
    private Integer select;
    private LinkedList<DevConfigInfraredSensor> data;
    private Integer[] data1;
    private Integer[] data2;
    private Integer[] data3;
}
