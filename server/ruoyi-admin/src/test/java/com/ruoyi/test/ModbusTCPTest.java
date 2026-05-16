package com.ruoyi.test;


import com.ruoyi.zm.domain.DevFaultRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

@Slf4j
@SpringBootTest
@DisplayName("Modbus-TCP 读写请求 单元测试")
public class ModbusTCPTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void keys() {
        Set<String> keys = redisTemplate.keys("zm:fault");
        if (keys != null) {
            for (String key : keys) {
                DevFaultRecord record = (DevFaultRecord) redisTemplate.opsForValue().get(key);
                System.out.println(record);
            }
        }
    }

    // @Test
    // @DisplayName("测试读请求")
    // void read() throws ModbusTransportException, InterruptedException {
    //     while (true) {
    //         ModbusMaster master = masterTcp.getSlave("192.168.100.123", 502);
    //         // 读取保持寄存器的值，功能码 03
    //         // 从设备地址 1 中读取 100 个寄存器，0000：表示从第 1 个寄存器开始读取
    //         // ReadHoldingRegistersRequest request =  new ReadHoldingRegistersRequest(1, 1383, 10);
    //
    //         // 读取离散状态（布尔值/开关状态），功能码 02
    //         ReadDiscreteInputsRequest request = new ReadDiscreteInputsRequest(1, 2815, 1);
    //
    //         // 读取一组线圈的状态，功能码 01
    //         // ReadCoilsRequest request = new ReadCoilsRequest(1, 2815, 1);
    //
    //         // 读取输入寄存器中的数据，功能码 04
    //         // ReadInputRegistersRequest request =new ReadInputRegistersRequest(1, 2815, 1);
    //
    //         // response = (ReadHoldingRegistersResponse) master.send(request);
    //         ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) master.send(request);
    //         // response = (ReadCoilsResponse) master.send(request);
    //         // response = (ReadInputRegistersResponse) master.send(request);
    //
    //         short[] values = response.getShortData();
    //         log.info("values:{}", values);
    //
    //         byte[] valuesByByte = response.getData();
    //         log.info("valuesByByte:{}", valuesByByte);
    //
    //         boolean[] valuesByBool = response.getBooleanData();
    //         log.info("valuesByBool:{}", valuesByBool);
    //
    //         Thread.sleep(1000);
    //     }
    // }

    // @Test
    // @DisplayName("测试写请求")
    // void write() throws ModbusTransportException, InterruptedException {
    //     ModbusMaster master1 = masterTcp.getSlave("192.168.100.123", 502);
    //     // 写入单个线圈的状态，功能码 05
    //     WriteCoilRequest request1 = new WriteCoilRequest(1, 2815, true);
    //     // 写单个保持寄存器，功能码 06
    //     // WriteRegisterRequest singleRegisterRequest = new WriteRegisterRequest(1, 40960, 2023);
    //     // 写多个保持寄存器，功能码 10
    //     // WriteRegistersRequest multiRegisterRequest = new WriteRegistersRequest(1, 2815, new short[]{0, 0, 0});
    //
    //     WriteCoilResponse response1 = (WriteCoilResponse) master1.send(request1);
    //     // WriteRegisterResponse singleRegisterResponse = (WriteRegisterResponse) master1.send(request1);
    //     // WriteRegistersResponse multiRegisterResponse = (WriteRegistersResponse) master1.send(request1);
    //
    //     int writeOffset = response1.getWriteOffset();
    //     log.info("writeOffset:{}", writeOffset);
    //
    //     boolean writeValue = response1.isWriteValue();
    //     log.info("writeValue:{}", writeValue);
    //
    //     if (response1.isException()) {
    //         log.info("写入失败，返回 false.. {}", response1.getExceptionMessage());
    //     } else {
    //         log.info("写入成功，返回 true..");
    //     }
    // }
}
