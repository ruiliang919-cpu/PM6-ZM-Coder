package com.ruoyi.zm.service.telemetering.impl;

import com.ruoyi.zm.config.ModbusTCPManager;
import com.ruoyi.zm.domain.DevProtocol03;
import com.ruoyi.zm.service.telemetering.LoopService;
import com.ruoyi.zm.utils.ScaleUtil;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class LoopServiceImpl implements LoopService {
    @Resource
    private ModbusTCPManager masterTcp;
    @Resource
    private Protocol03Service protocol03Service;

    @Override
    public Map<String, String> FeederBranchName(int slaveId) {
        DevProtocol03 devProtocol03 = protocol03Service.getByProtocolName("馈线支路名称01_01");
        int addr = ScaleUtil.from16To10Min1(devProtocol03.getAddr());

        ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
        // 创建异步方法，组合成六十四个馈线支路名称返回
//        CompletableFuture.allOf(IntStream.rangeClosed(1, 64).mapToObj(i -> {
//            // 根据馈线编号计算寄存器起始地址
//            int registerStart = addr + (i - 1) * 10;
//            return CompletableFuture.supplyAsync(() -> {
////                try {
//////                    byte[] bytes = ModbusTcpUtil.ReadHRByData(masterTcp.getSlave(slaveId), slaveId, registerStart, 10);
//////                    return new String(bytes, "GBK");
////                } catch (ModbusTransportException | UnsupportedEncodingException e) {
////                    log.error("读取馈线支路名称失败，设备ID：" + slaveId
////                        + " 当前读取的寄存器：" + registerStart
////                        + " 馈线支路名称" + String.format("%02d", i)
////                        + " 原因：" + e.getMessage(), e);
////                    return "error";
////                }
////            }).thenAccept(text -> {
////                result.put("馈线支路名称" + String.format("%02d", i), text);
////            });
//        }).toArray(CompletableFuture[]::new)).join();

        return result;
    }

    @Override
    public Map<String, String> MasterMonitorVersion(int slaveId) throws ModbusTransportException, UnsupportedEncodingException {
        DevProtocol03 devProtocol03 = protocol03Service.getByProtocolName("主监控版本_01");
        int addr = ScaleUtil.from16To10Min1(devProtocol03.getAddr());

        TreeMap<String, String> result = new TreeMap<>();
//        byte[] bytes = ModbusTcpUtil.ReadHRByData(masterTcp.getSlave(slaveId), slaveId, addr, 10);
//        String asciiText = new String(bytes, US_ASCII);
//        result.put("主监控版本", asciiText);
        return result;
    }

    @Override
    public Map<String, Integer> IlluminanceSensorExternalControlChannel(int slaveId) throws ModbusTransportException {
        DevProtocol03 devProtocol03 = protocol03Service.getByProtocolName("照度传感器外控通道01");
        int addr = ScaleUtil.from16To10Min1(devProtocol03.getAddr());

//        short[] shorts = ModbusTcpUtil.ReadHR(masterTcp.getSlave(slaveId), slaveId, addr, 10);
//        TreeMap<String, Integer> result = new TreeMap<>();
//
//         TODO：照度传感器外控通道01 照度传感器外控地址01 即 外控通道与外控地址在一起，而不是单独通道或地址连续
//        for (int i = 0; i < shorts.length; i++) {
//            result.put("照度传感器外控通道0" + (i + 1), ((shorts[i * 2] & 0xFFFF) << 16) | (shorts[i * 2 + 1] & 0xFFFF));
//        }

//        return result;
        return null;
    }

    @Override
    public Map<String, String> selectionGroup(int slaveId) throws ModbusTransportException {
        DevProtocol03 devProtocol03 = protocol03Service.getByProtocolName("系统设置 -回路分组-分组选择_01");
        int addr = ScaleUtil.from16To10Min1(devProtocol03.getAddr());

        TreeMap<String, String> result = new TreeMap<>();
//        byte[] bytes = ModbusTcpUtil.ReadHRByData(masterTcp.getSlave(slaveId), slaveId, addr, 16);
//        String asciiText = new String(bytes, US_ASCII);
//         TODO：ASCII字符串长度32字节，示例：”01020314“ 表示有4个分组，分别是1、2、3、14
//        result.put("系统设置 -回路分组-分组选择", asciiText);
        return result;
    }

    @Override
    public Map<String, String> PacketLoopNumber(int slaveId) {
        DevProtocol03 devProtocol03 = protocol03Service.getByProtocolName("系统设置 -回路分组-分组回路编号01_01");
        int addr = ScaleUtil.from16To10Min1(devProtocol03.getAddr());

        ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
        // 创建异步方法，组合成十六个个系统设置 -回路分组-分组回路编号返回
//        CompletableFuture.allOf(IntStream.rangeClosed(1, 16).mapToObj(i -> {
//            // 根据分组回路编号计算寄存器起始地址
//            int registerStart = addr + (i - 1) * 40;
//            return CompletableFuture.supplyAsync(() -> {
//                try {
//                    byte[] bytes = ModbusTcpUtil.ReadHRByData(masterTcp.getSlave(slaveId), slaveId, registerStart, 40);
//                    return new String(bytes, US_ASCII);
//                } catch (ModbusTransportException e) {
//                    log.error("读取系统设置 -回路分组-分组回路编号，设备ID：" + slaveId
//                        + " 当前读取的寄存器：" + registerStart
//                        + " 读取系统设置 -回路分组-分组回路编号" + String.format("%02d", i)
//                        + " 原因：" + e.getMessage(), e);
//                    return "error";
//                }
//            }).thenAccept(text -> {
//                // TODO：ASCII字符串长度80字节，示例：”01030517“ 表示分组01有4个回路，分别是1、3、5、17
//                result.put("系统设置 -回路分组-分组回路编号" + String.format("%02d", i), text);
//            });
//        }).toArray(CompletableFuture[]::new)).join();

        return result;
    }

    @Override
    public List<Integer> getLoopsByPacketNo(int slaveId, int packetNo) throws ModbusTransportException {
        DevProtocol03 devProtocol03;
        if (packetNo <= 9) {
            devProtocol03 = protocol03Service.getByProtocolName("系统设置 -回路分组-分组回路编号0" + packetNo + "_01");
        } else {
            devProtocol03 = protocol03Service.getByProtocolName("系统设置 -回路分组-分组回路编号" + packetNo + "_01");
        }
        int addr = ScaleUtil.from16To10Min1(devProtocol03.getAddr());

//        byte[] bytes = ModbusTcpUtil.ReadHRByData(masterTcp.getSlave(slaveId), slaveId, addr, 40);
//        String asciiStr = new String(bytes, US_ASCII);

//        return IntStream.range(0, asciiStr.length())
//            .filter(i -> i % 2 == 0)
//            .mapToObj(i -> Integer.valueOf(asciiStr.substring(i, Math.min(i + 2, asciiStr.length()))))
//            .collect(Collectors.toList());
        return null;
    }
}
