package com.ruoyi.vo;

public class ModbusRespMsg {
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
     * 字节计数
     */
    private Integer byteCount;

    /**
     * 寄存器值
     */
    private String  regHex;

    public ModbusRespMsg(String msgHex) {
        int start = 0;
        int end = start+4;
        this.id = msgHex.substring(start,end);
        start = end;
        end = start+4;
        this.protocolId= msgHex.substring(start,end);
        start = end;
        end = start+4;
        this.len= Integer.parseInt(msgHex.substring(start,end));
        start = end;
        end = start+2;
        this.slaveId= Integer.parseInt(msgHex.substring(start,end));
        start = end;
        end = start+2;
        this.funCode= Integer.parseInt(msgHex.substring(start,end));
        start = end;
        end = start+2;
        this.byteCount= Integer.parseInt(msgHex.substring(start,end));
        start = end;
        end = byteCount*2-4;
        this.regHex= msgHex.substring(start,end);
    }

    @Override
    public String toString() {
        return "ModbusRespMsg{" +
            "id='" + id + '\'' +
            ", protocolId='" + protocolId + '\'' +
            ", len=" + len +
            ", slaveId=" + slaveId +
            ", funCode=" + funCode +
            ", byteCount=" + byteCount +
            ", regHex='" + regHex + '\'' +
            '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(String protocolId) {
        this.protocolId = protocolId;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public Integer getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(Integer slaveId) {
        this.slaveId = slaveId;
    }

    public Integer getFunCode() {
        return funCode;
    }

    public void setFunCode(Integer funCode) {
        this.funCode = funCode;
    }

    public Integer getByteCount() {
        return byteCount;
    }

    public void setByteCount(Integer byteCount) {
        this.byteCount = byteCount;
    }

    public String getRegHex() {
        return regHex;
    }

    public void setRegHex(String regHex) {
        this.regHex = regHex;
    }
}
