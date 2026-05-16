package com.ruoyi.vo;

import lombok.Data;

@Data
public class ModbusReqMsg {
    /**
     * 事务标识符
     */
    private String id;
    /**
     * 协议标识符
     */
    private String protocolId;
    /**
     * 数据长度
     */
    private Integer len;

    /**
     * 设备标识符
     */
    private Integer slaveId;
    /**
     * 功能码
     */
    private Integer funCode;
    /**
     * 超始地址
     */
    private Integer startOffset;
    /**
     * 寄存器数量
     */
    private Integer numberOfRegisters;
    /**
     * 原始帧
     */
    private String originalFrame;

    public ModbusReqMsg(String msgHex) {
        this.originalFrame = msgHex;
        int start = 0;
        int end = start + 4;
        this.id = msgHex.substring(start, end);
        start = end;
        end = start + 4;
        this.protocolId = msgHex.substring(start, end);
        start = end;
        end = start + 4;
        this.len = Integer.parseInt(msgHex.substring(start, end));
        start = end;
        end = start + 2;
        this.slaveId = Integer.parseInt(msgHex.substring(start, end));
        start = end;
        end = start + 2;
        this.funCode = Integer.parseInt(msgHex.substring(start, end));
        start = end;
        end = start + 4;
        this.startOffset = Integer.parseInt(msgHex.substring(start, end));
        start = end;
        end = start + 4;
        String hexString = msgHex.substring(start, end);
        this.numberOfRegisters = Integer.parseInt(hexString, 16);
    }
}
