// package com.ruoyi.zm.service.remoteRegulation.impl;
//
// import com.ruoyi.utils.ModbusTCPManeger;
// import com.ruoyi.zm.domain.DevProtocol06;
// import com.ruoyi.zm.service.remoteRegulation.ReadService;
// import com.ruoyi.zm.utils.ModbusTcpUtil;
// import com.ruoyi.zm.utils.ScaleUtil;
// import com.serotonin.modbus4j.exception.ModbusTransportException;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;
//
// import javax.annotation.Resource;
// import java.io.UnsupportedEncodingException;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.Map;
// import java.util.TreeMap;
// import java.util.concurrent.CompletableFuture;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.stream.IntStream;
//
// @Slf4j
// @Service
// public class ReadServiceImpl implements ReadService {
//     @Resource
//     private ModbusTCPManeger masterTcp;
//     @Resource
//     private Protocol06Service protocol06Service;
//
//     @Override
//     public Map<String, Short> readDateTime(int slaveId) throws ModbusTransportException {
//         DevProtocol06 devProtocol06 = protocol06Service.getByProtocolName("时间设置-年");
//         int addr = ScaleUtil.from16To10Min1(devProtocol06.getAddr());
//
//         Map<String, Short> result = new TreeMap<>();
//         short[] shorts = ModbusTcpUtil.ReadHR(masterTcp.getSlave(slaveId), slaveId, addr, 7);
//
//         for (int i = 0; i < shorts.length; i++) {
//             result.put("时间设置-年", shorts[0]);
//             result.put("时间设置-月", shorts[1]);
//             result.put("时间设置-日", shorts[2]);
//             result.put("时间设置-时", shorts[3]);
//             result.put("时间设置-分", shorts[4]);
//             result.put("时间设置-秒", shorts[5]);
//             result.put("启用对时标志位", shorts[5]);
//         }
//
//         return result;
//     }
//
//     @Override
//     public Map<String, String> readGroupName(int slaveId) {
//         DevProtocol06 devProtocol06 = protocol06Service.getByProtocolName("系统设置-回路分组-分组命名01_01");
//         int addr = ScaleUtil.from16To10Min1(devProtocol06.getAddr());
//
//         ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
//         // 创建异步方法，组合成十六个分组名称返回
//         CompletableFuture.allOf(IntStream.rangeClosed(1, 16).mapToObj(i -> {
//             // 根据馈线编号计算寄存器起始地址
//             int registerStart = addr + (i - 1) * 10;
//             return CompletableFuture.supplyAsync(() -> {
//                 try {
//                     byte[] bytes = ModbusTcpUtil.ReadHRByData(masterTcp.getSlave(slaveId), slaveId, registerStart, 10);
//                     return new String(bytes, "GBK");
//                 } catch (ModbusTransportException | UnsupportedEncodingException e) {
//                     log.error("读取系统设置-回路分组-分组命名失败，设备ID：" + slaveId
//                         + " 当前读取的寄存器：" + registerStart
//                         + " 系统设置-回路分组-分组命名" + String.format("%02d", i)
//                         + " 原因：" + e.getMessage(), e);
//                     return "error";
//                 }
//             }).thenAccept(text -> {
//                 result.put("系统设置-回路分组-分组命名" + String.format("%02d", i), text);
//             });
//         }).toArray(CompletableFuture[]::new)).join();
//
//         return result;
//     }
//
//     @Override
//     public List<Integer> readGroupNo(int slaveId) throws ModbusTransportException {
//         DevProtocol06 devProtocol06 = protocol06Service.getByProtocolName("系统设置-回路分组-分区编号01");
//         int addr = ScaleUtil.from16To10Min1(devProtocol06.getAddr());
//
//         short[] shorts = ModbusTcpUtil.ReadHR(masterTcp.getSlave(slaveId), slaveId, addr, 16);
//         List<Integer> result = new LinkedList<>();
//         for (short aShort : shorts) {
//             result.add((int) aShort);
//         }
//
//         return result;
//     }
//
//
// }
