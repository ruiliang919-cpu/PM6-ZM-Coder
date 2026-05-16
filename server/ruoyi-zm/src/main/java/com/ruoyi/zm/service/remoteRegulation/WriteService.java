package com.ruoyi.zm.service.remoteRegulation;

import com.serotonin.modbus4j.exception.ModbusTransportException;

// 遥调协议的写协议
public interface WriteService {
    // 写入AC/DC模块输出电压、DC/DC模块输出电压
    // salveId：设备ID
    // shorts：写入的数据
    void writeACDCModuleOutputVoltage(int salveId, short[] shorts) throws ModbusTransportException;

    // 写入 时间设置- 年 月 日 时 分 秒 启用对时标志位
    // shorts：要写入的数据
    void writeDateTime(int slaveId, short[] shorts) throws ModbusTransportException;

    // 写入系统设置-回路分组-添加回路
    // ASCII字符串，长度82字节，示例：“01 01 02 03 04 05 16”,
    //      表示分组01添加回路1、2、3、4、5、16（前两个字节表示分组号，
    //      后面每个字符表示回路编号）
    // salveId：设备ID
    // group：组ID，1 ~ 41 取一个
    // loops：回路数组，例如：[1, 2, 3, 4, ... ]
    void writeSystemLoopAdd(int salveId, int group, int[] loops) throws ModbusTransportException;

    // 写入系统设置-回路分组-删除回路
    // ASCII字符串，长度82字节，示例：“0201020340”,
    //      表示分组02删除回路1、2、3、40（前两个字节表示分组号，
    //      后面每个字节表示回路编号）
    // salveId：设备ID
    // group：组ID，1 ~ 41 取一个
    // loops：回路数组，例如：[1, 2, 3, 4, ... ]
    void writeSystemLoopDel(int salveId, int group, int[] loops) throws ModbusTransportException;

    // 写入 系统设置-回路分组-分组命名
    // ASCII字符串长度20个字节，示例：”A区街道“
    // group：组ID，1 ~ 41 取一个
    // groupName：修改的组名称
    void writeSystemGroupName(int salveId, int group, String groupName) throws ModbusTransportException;
}
