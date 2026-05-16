package com.ruoyi.zm.service.telemetering;

import com.serotonin.modbus4j.exception.ModbusTransportException;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

// 遥测 ASCII 数据
public interface LoopService {

    // 单个机柜：馈线支路名称 ASCII字符串 20 字节例如：“直流回路 01”
    Map<String, String> FeederBranchName(int slaveId);

    // 单个机柜：主监控版本
    Map<String, String> MasterMonitorVersion(int slaveId) throws ModbusTransportException, UnsupportedEncodingException;

    // 单个机柜：照度传感器外控通道
    Map<String, Integer> IlluminanceSensorExternalControlChannel(int slaveId) throws ModbusTransportException;

    // 单个机柜：系统设置 -回路分组-分组选择
    Map<String, String> selectionGroup(int slaveId) throws ModbusTransportException;

    // 单个机柜：系统设置 -回路分组-分组回路编号
    Map<String, String> PacketLoopNumber(int slaveId) throws ModbusTransportException;

    // 单个机柜：系统设置-回路分组-分组回路编号
    // 根据设备ID与分组编号获取单个分组下的回路
    // slaveId：设备ID
    // packetNo：分组编号
    List<Integer> getLoopsByPacketNo(int slaveId, int packetNo) throws ModbusTransportException;
}
