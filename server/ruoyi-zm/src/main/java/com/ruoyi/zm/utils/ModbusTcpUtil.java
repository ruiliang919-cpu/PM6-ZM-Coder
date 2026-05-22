package com.ruoyi.zm.utils;

import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Modbus-TCP 读写寄存器数据
 */
@Slf4j
public class ModbusTcpUtil {


    // 读取寄存器的值，功能码 03（遥测）
    // master：modbus-tcp连接
    // slaveId：从机地址
    // start：表示从第 start+1 个寄存器开始读取
    // number：读取寄存器的数量
    // 返回整型数据数组
    // 注意：所有 10 个读写方法均使用传入的 slaveId 参数，不再硬编码
    public static short[] ReadHR(ModbusMaster master, int slaveId, int start, int number) throws ModbusTransportException {
        if (master == null) {
            throw new NullPointerException("ModbusMaster cannot be null");
        }
        ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, start, number);
        ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(request);
        if (response == null) {
            throw new NullPointerException("Modbus response is null");
        }
        return response.getShortData();
    }

    // 读取寄存器的值，功能码 03（遥测）
    // 返回二进制数据数组
    public static byte[] ReadHRByData(ModbusMaster master, int slaveId, int start, int number) throws ModbusTransportException {
        // if (null == master) return null;
        ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, start, number);
        ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(request);
        return response.getData();
    }

    // 读取一组线圈的状态，功能码 01（遥信）
    public static byte[] ReadCoils(ModbusMaster master, int slaveId, int start, int number) throws ModbusTransportException {
        ReadCoilsRequest request = new ReadCoilsRequest(slaveId, start, number);
        ReadCoilsResponse response = (ReadCoilsResponse) master.send(request);
        // System.out.println("~~~~~~~~~~ReadCoilsResponse~~~~~~~~~~~" + Arrays.toString(response.getData()));
        return response.getData();
    }

    // 读取一组线圈的状态，功能码 01（遥信）
    // 返回二进制数据数组
    public static byte[] ReadCoilsByData(ModbusMaster master, int slaveId, int start, int number) throws ModbusTransportException {
        ReadCoilsRequest request = new ReadCoilsRequest(slaveId, start, number);
        ReadCoilsResponse response = (ReadCoilsResponse) master.send(request);
        return response.getData();
    }

    // 读取一组线圈的状态，功能码 01（遥信）
    // 返回布尔数据数组
    public static boolean[] ReadCoilsByBool(ModbusMaster master, int slaveId, int start, int number) throws ModbusTransportException {
        ReadCoilsRequest request = new ReadCoilsRequest(slaveId, start, number);
        ReadCoilsResponse response = (ReadCoilsResponse) master.send(request);
        return response.getBooleanData();
    }

    // 读取离散输入，功能码 02（遥信）
    // 返回一组布尔值状态（true/false）
    public static boolean[] ReadDI(ModbusMaster master, int slaveId, int start, int number) throws ModbusTransportException {
        // if (null == master) return null;
        ReadDiscreteInputsRequest request = new ReadDiscreteInputsRequest(slaveId, start, number);
        ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) master.send(request);
        return response.getBooleanData();
    }

    // 功能码06，写入单个寄存器数据
    public static void WriteRegister(ModbusMaster master, int slaveId, int writeOffset, int writeValue) throws ModbusTransportException {
        WriteRegisterRequest request = new WriteRegisterRequest(slaveId, writeOffset, writeValue);
        WriteRegisterResponse response = (WriteRegisterResponse) master.send(request);
        // System.err.println(response);
        if (response.isException()) {
            throw new RuntimeException("发送写入请求出错，设备ID：" + slaveId
                + " 寄存器地址：" + writeOffset
                + " 具体原因：" + response.getExceptionMessage());
        }
    }

    // 功能码10，写入多个寄存器数据
    public static void WriteRegisters(ModbusMaster master, int slaveId, int writeOffset, short[] writeValue) throws ModbusTransportException {
        WriteRegistersRequest request = new WriteRegistersRequest(slaveId, writeOffset, writeValue);
        WriteRegistersResponse response = (WriteRegistersResponse) master.send(request);
        if (response.isException()) {
            throw new RuntimeException("发送写入请求出错，设备ID：" + slaveId
                + " 寄存器地址：" + writeOffset
                + " 具体原因：" + response.getExceptionMessage());
        }
    }

    // 功能码05，写入单个线圈数据
    public static void WriteCoil(ModbusMaster master, int slaveId, int writeOffset, boolean writeValue) throws ModbusTransportException {
        WriteCoilRequest request = new WriteCoilRequest(slaveId, writeOffset, writeValue);
        WriteCoilResponse response = (WriteCoilResponse) master.send(request);
        if (response.isException()) {
            throw new RuntimeException("发送写入请求出错，设备ID：" + slaveId
                + " 位地址：" + writeOffset
                + " 具体原因：" + response.getExceptionMessage());
        }
    }

    // 功能码15/0F，写入多个线圈数据
    public static void WriteCoils(ModbusMaster master, int slaveId, int writeOffset, boolean[] writeValue) throws ModbusTransportException {
        WriteCoilsRequest request = new WriteCoilsRequest(slaveId, writeOffset, writeValue);
        WriteCoilsResponse response = (WriteCoilsResponse) master.send(request);
        if (response.isException()) {
            throw new RuntimeException("发送写入请求出错，设备ID：" + slaveId
                + " 位地址：" + writeOffset
                + " 具体原因：" + response.getExceptionMessage());
        }
    }
}
