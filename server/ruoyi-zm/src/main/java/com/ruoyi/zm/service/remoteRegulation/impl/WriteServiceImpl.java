// package com.ruoyi.zm.service.remoteRegulation.impl;
//
// import com.ruoyi.utils.ModbusTCPManeger;
// import com.ruoyi.zm.domain.DevProtocol06;
// import com.ruoyi.zm.service.remoteRegulation.WriteService;
// import com.ruoyi.zm.utils.ModbusTcpUtil;
// import com.ruoyi.zm.utils.ScaleUtil;
// import com.serotonin.modbus4j.exception.ModbusTransportException;
// import org.springframework.stereotype.Service;
//
// import javax.annotation.Resource;
// import java.nio.charset.StandardCharsets;
// import java.util.Arrays;
//
// import static com.serotonin.modbus4j.locator.StringLocator.ASCII;
//
// @Service
// public class WriteServiceImpl implements WriteService {
//     @Resource
//     private ModbusTCPManeger masterTcp;
//     @Resource
//     private Protocol06Service protocol06Service;
//
//     @Override
//     public void writeACDCModuleOutputVoltage(int salveId, short[] shorts) throws ModbusTransportException {
//         DevProtocol06 devProtocol06 = protocol06Service.getByProtocolName("AC/DC模块输出电压");
//         int addr = ScaleUtil.from16To10Min1(devProtocol06.getAddr());
//         // TODO:写入数据是否需要转换倍率？
//         shorts[0] = (short) (shorts[0] * devProtocol06.getMagnification());
//         shorts[1] = (short) (shorts[1] * devProtocol06.getMagnification());
//         ModbusTcpUtil.WriteRegisters(masterTcp.getSlave(salveId), salveId, addr, shorts);
//     }
//
//     @Override
//     public void writeDateTime(int slaveId, short[] shorts) throws ModbusTransportException {
//         DevProtocol06 devProtocol06 = protocol06Service.getByProtocolName("时间设置-年");
//         int addr = ScaleUtil.from16To10Min1(devProtocol06.getAddr());
//         ModbusTcpUtil.WriteRegisters(masterTcp.getSlave(slaveId), slaveId, addr, shorts);
//     }
//
//     @Override
//     public void writeSystemLoopAdd(int salveId, int group, int[] loops) throws ModbusTransportException {
//         DevProtocol06 devProtocol06;
//         String data = "";
//         if (group > 9) {
//             devProtocol06 = protocol06Service.getByProtocolName("系统设置-回路分组-添加回路_" + group);
//             data = data + group + " ";
//             for (int loop : loops) {
//                 if (loop > 9) {
//                     data = data + loop + " ";
//                 } else {
//                     String tempStr = "0" + loop + " ";
//                     data += tempStr;
//                 }
//             }
//         } else {
//             devProtocol06 = protocol06Service.getByProtocolName("系统设置-回路分组-添加回路_0" + group);
//             data = data + "0" + group + " ";
//             for (int loop : loops) {
//                 if (loop > 9) {
//                     data = data + loop + " ";
//                 } else {
//                     String tempStr = "0" + loop + " ";
//                     data += tempStr;
//                 }
//             }
//         }
//         int addr = ScaleUtil.from16To10Min1(devProtocol06.getAddr());
//
//         // 将字符串转换为字节数组
//         byte[] bytes = data.getBytes(StandardCharsets.US_ASCII);
//
//         // 将字节数组转换为寄存器数组
//         short[] registers = new short[bytes.length / 2];
//         for (int i = 0; i < bytes.length; i += 2) {
//             registers[i / 2] = (short) ((bytes[i] << 8) & 0xFF00 | (bytes[i + 1] & 0xFF));
//         }
//
//         ModbusTcpUtil.WriteRegisters(masterTcp.getSlave(salveId), salveId, addr, registers);
//     }
//
//     @Override
//     public void writeSystemLoopDel(int salveId, int group, int[] loops) throws ModbusTransportException {
//         DevProtocol06 devProtocol06;
//         String data = "";
//         if (group > 9) {
//             devProtocol06 = protocol06Service.getByProtocolName("系统设置-回路分组-删除回路_" + group);
//             data = data + group + " ";
//             for (int loop : loops) {
//                 if (loop > 9) {
//                     data = data + loop + " ";
//                 } else {
//                     String tempStr = "0" + loop + " ";
//                     data += tempStr;
//                 }
//             }
//         } else {
//             devProtocol06 = protocol06Service.getByProtocolName("系统设置-回路分组-删除回路_0" + group);
//             data = data + "0" + group + " ";
//             for (int loop : loops) {
//                 if (loop > 9) {
//                     data = data + loop + " ";
//                 } else {
//                     String tempStr = "0" + loop + " ";
//                     data += tempStr;
//                 }
//             }
//         }
//         int addr = ScaleUtil.from16To10Min1(devProtocol06.getAddr());
//
//         // 将字符串转换为字节数组
//         byte[] bytes = data.getBytes(StandardCharsets.US_ASCII);
//
//         // 将字节数组转换为寄存器数组
//         short[] registers = new short[bytes.length / 2];
//         for (int i = 0; i < bytes.length; i += 2) {
//             registers[i / 2] = (short) ((bytes[i] << 8) & 0xFF00 | (bytes[i + 1] & 0xFF));
//         }
//
//         ModbusTcpUtil.WriteRegisters(masterTcp.getSlave(salveId), salveId, addr, registers);
//     }
//
//     @Override
//     public void writeSystemGroupName(int salveId, int group, String groupName) throws ModbusTransportException {
//         DevProtocol06 devProtocol06;
//         if (group > 9) {
//             devProtocol06 = protocol06Service.getByProtocolName("系统设置-回路分组-分组命名_" + group + "01");
//         } else {
//             devProtocol06 = protocol06Service.getByProtocolName("系统设置-回路分组-分组命名_0" + group + "01");
//         }
//         int addr = ScaleUtil.from16To10Min1(devProtocol06.getAddr());
//         // 字符串转为字节数组
//         byte[] bytes = groupName.getBytes(ASCII);
//         // 如果名称不足 20 字节，补足至 20 字节
//         while (bytes.length < 20) {
//             bytes = Arrays.copyOf(bytes, bytes.length + 1);
//         }
//         // 将字节数组转换为寄存器数组
//         // 每个寄存器16位，所以 20 字节需要10个寄存器
//         short[] registers = new short[10];
//         for (int i = 0; i < 20; i++) {
//             registers[i / 2] |= (short) ((bytes[i] & 0xFF) << ((i % 2 == 0) ? 8 : 0));
//         }
//         ModbusTcpUtil.WriteRegisters(masterTcp.getSlave(salveId), salveId, addr, registers);
//     }
//
//
// }
