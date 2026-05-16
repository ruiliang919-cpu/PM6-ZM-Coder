// package com.ruoyi.zm.service.remoteRegulation;
//
// import com.serotonin.modbus4j.exception.ModbusTransportException;
//
// import java.io.UnsupportedEncodingException;
// import java.util.List;
// import java.util.Map;
//
// // 遥调协议的读协议
// public interface ReadService {
//     // 读取 时间设置- 年 月 日 时 分 秒 启用对时标志位
//     // slaveId：设备ID
//     Map<String, Short> readDateTime(int slaveId) throws ModbusTransportException;
//
//     // 读取 系统设置-回路分组-分组命名
//     // ASCII字符串长度20个字节，示例：”A区街道“
//     Map<String, String> readGroupName(int slaveId);
//
//     // 读取 系统设置-回路分组-分区编号
//     // 数值型，示例05表示分区01属于第5分区，也是分区地址
//     List<Integer> readGroupNo(int slaveId) throws ModbusTransportException;
// }
