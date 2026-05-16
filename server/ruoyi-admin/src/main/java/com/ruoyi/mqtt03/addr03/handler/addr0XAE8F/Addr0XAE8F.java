package com.ruoyi.mqtt03.addr03.handler.addr0XAE8F;

import com.ruoyi.zm.domain.DevConfigIlluminanceSensor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Addr0XAE8F {
    private String ip;
    private String addr;
    private Integer select;
    private LinkedList<DevConfigIlluminanceSensor> data;
    private Integer[] data1;
    private Integer[] data2;
    private Integer[] data3;
    private Integer[] data4;
    private Integer[] data5;
}
