package com.ruoyi.zm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 设备信息类，用于保持Modbus连接
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModbusInfo {
    // 设备IP地址
    private String ip;
    // 设备端口号
    private Integer port;
    // 设备ID
    private int slaveId;
}
