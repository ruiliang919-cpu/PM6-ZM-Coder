package com.ruoyi.zm.utils;

import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ModbusTcpUtil 单元测试
 *
 * 覆盖功能码: 01/02/03/04/05/06/0F/10
 * 测试场景: 正常流程、异常响应、传输异常
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ModbusTcpUtil - Modbus-TCP读写工具测试")
class ModbusTcpUtilTest {

    @Mock
    private ModbusMaster master;

    // ==================== FC03 读取保持寄存器 ====================

    @Test
    @DisplayName("FC03: 正常读取保持寄存器返回short数组")
    void testReadHR_Success() throws ModbusTransportException {
        // Given
        ReadHoldingRegistersResponse response = mock(ReadHoldingRegistersResponse.class);
        short[] expected = new short[]{100, 200, 300};
        when(response.getShortData()).thenReturn(expected);
        when(master.send(any(ReadHoldingRegistersRequest.class))).thenReturn(response);

        // When
        short[] result = ModbusTcpUtil.ReadHR(master, 1, 0, 3);

        // Then
        assertThat(result).isEqualTo(expected);
        verify(master).send(argThat((ReadHoldingRegistersRequest req) -> req.getSlaveId() == 1));
    }

    @Test
    @DisplayName("FC03: Modbus传输异常时向上抛出")
    void testReadHR_TransportException() throws ModbusTransportException {
        // Given
        when(master.send(any(ReadHoldingRegistersRequest.class)))
            .thenThrow(new ModbusTransportException("连接超时"));

        // Then
        assertThatThrownBy(() -> ModbusTcpUtil.ReadHR(master, 1, 0, 10))
            .isInstanceOf(ModbusTransportException.class)
            .hasMessageContaining("连接超时");
    }

    @Test
    @DisplayName("FC03(ReadHRByData): 正常读取返回byte数组")
    void testReadHRByData_Success() throws ModbusTransportException {
        // Given
        ReadHoldingRegistersResponse response = mock(ReadHoldingRegistersResponse.class);
        byte[] expected = new byte[]{0x01, 0x02, 0x03, 0x04};
        when(response.getData()).thenReturn(expected);
        when(master.send(any(ReadHoldingRegistersRequest.class))).thenReturn(response);

        // When
        byte[] result = ModbusTcpUtil.ReadHRByData(master, 1, 0, 2);

        // Then
        assertThat(result).isEqualTo(expected);
        verify(master).send(argThat((ReadHoldingRegistersRequest req) -> req.getSlaveId() == 1));
    }

    // ==================== FC01 读取线圈 ====================

    @Test
    @DisplayName("FC01: 正常读取线圈返回byte数组")
    void testReadCoils_Success() throws ModbusTransportException {
        // Given
        ReadCoilsResponse response = mock(ReadCoilsResponse.class);
        byte[] expected = new byte[]{0x05}; // 00000101 -> coil1=1, coil3=1
        when(response.getData()).thenReturn(expected);
        when(master.send(any(ReadCoilsRequest.class))).thenReturn(response);

        // When
        byte[] result = ModbusTcpUtil.ReadCoils(master, 1, 0, 8);

        // Then
        assertThat(result).isEqualTo(expected);
        verify(master).send(argThat((ReadCoilsRequest req) -> req.getSlaveId() == 1));
    }

    @Test
    @DisplayName("FC01(ReadCoilsByBool): 正常读取线圈返回boolean数组")
    void testReadCoilsByBool_Success() throws ModbusTransportException {
        // Given
        ReadCoilsResponse response = mock(ReadCoilsResponse.class);
        boolean[] expected = new boolean[]{true, false, true, false};
        when(response.getBooleanData()).thenReturn(expected);
        when(master.send(any(ReadCoilsRequest.class))).thenReturn(response);

        // When
        boolean[] result = ModbusTcpUtil.ReadCoilsByBool(master, 1, 0, 4);

        // Then
        assertThat(result).isEqualTo(expected);
        verify(master).send(argThat((ReadCoilsRequest req) -> req.getSlaveId() == 1));
    }

    // ==================== FC02 读取离散输入 ====================

    @Test
    @DisplayName("FC02: 正常读取离散输入返回boolean数组")
    void testReadDI_Success() throws ModbusTransportException {
        // Given
        ReadDiscreteInputsResponse response = mock(ReadDiscreteInputsResponse.class);
        boolean[] expected = new boolean[]{true, true, false, false};
        when(response.getBooleanData()).thenReturn(expected);
        when(master.send(any(ReadDiscreteInputsRequest.class))).thenReturn(response);

        // When
        boolean[] result = ModbusTcpUtil.ReadDI(master, 1, 0, 4);

        // Then
        assertThat(result).isEqualTo(expected);
        verify(master).send(argThat((ReadDiscreteInputsRequest req) -> req.getSlaveId() == 1));
    }

    @Test
    @DisplayName("FC02: 传输异常时向上抛出")
    void testReadDI_TransportException() throws ModbusTransportException {
        // Given
        when(master.send(any(ReadDiscreteInputsRequest.class)))
            .thenThrow(new ModbusTransportException("读取失败"));

        // Then
        assertThatThrownBy(() -> ModbusTcpUtil.ReadDI(master, 1, 0, 8))
            .isInstanceOf(ModbusTransportException.class);
    }

    // ==================== FC05 写入单个线圈 ====================

    @Test
    @DisplayName("FC05: 正常写入单个线圈不抛异常")
    void testWriteCoil_Success() throws ModbusTransportException {
        // Given
        WriteCoilResponse response = mock(WriteCoilResponse.class);
        when(response.isException()).thenReturn(false);
        when(master.send(any(WriteCoilRequest.class))).thenReturn(response);

        // When & Then (不抛异常即通过)
        ModbusTcpUtil.WriteCoil(master, 1, 100, true);
        verify(master).send(argThat((WriteCoilRequest req) -> req.getSlaveId() == 1));
    }

    @Test
    @DisplayName("FC05: 异常响应时抛出RuntimeException")
    void testWriteCoil_ExceptionResponse() throws ModbusTransportException {
        // Given
        WriteCoilResponse response = mock(WriteCoilResponse.class);
        when(response.isException()).thenReturn(true);
        when(response.getExceptionMessage()).thenReturn("非法数据地址");
        when(master.send(any(WriteCoilRequest.class))).thenReturn(response);

        // Then
        assertThatThrownBy(() -> ModbusTcpUtil.WriteCoil(master, 1, 100, true))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("发送写入请求出错")
            .hasMessageContaining("非法数据地址");
    }

    @Test
    @DisplayName("FC05: 传输异常时向上抛出")
    void testWriteCoil_TransportException() throws ModbusTransportException {
        // Given
        when(master.send(any(WriteCoilRequest.class)))
            .thenThrow(new ModbusTransportException("写入超时"));

        // Then
        assertThatThrownBy(() -> ModbusTcpUtil.WriteCoil(master, 1, 100, true))
            .isInstanceOf(ModbusTransportException.class);
    }

    // ==================== FC06 写入单个寄存器 ====================

    @Test
    @DisplayName("FC06: 正常写入单个寄存器不抛异常")
    void testWriteRegister_Success() throws ModbusTransportException {
        // Given
        WriteRegisterResponse response = mock(WriteRegisterResponse.class);
        when(response.isException()).thenReturn(false);
        when(master.send(any(WriteRegisterRequest.class))).thenReturn(response);

        // When & Then
        ModbusTcpUtil.WriteRegister(master, 1, 200, 500);
        verify(master).send(argThat((WriteRegisterRequest req) -> req.getSlaveId() == 1));
    }

    @Test
    @DisplayName("FC06: 异常响应时抛出RuntimeException并包含设备信息")
    void testWriteRegister_ExceptionResponse() throws ModbusTransportException {
        // Given
        WriteRegisterResponse response = mock(WriteRegisterResponse.class);
        when(response.isException()).thenReturn(true);
        when(response.getExceptionMessage()).thenReturn("从设备故障");
        when(master.send(any(WriteRegisterRequest.class))).thenReturn(response);

        // Then
        assertThatThrownBy(() -> ModbusTcpUtil.WriteRegister(master, 5, 4096, 100))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("设备ID：5")
            .hasMessageContaining("寄存器地址：4096")
            .hasMessageContaining("从设备故障");
    }

    // ==================== FC10 写入多个寄存器 ====================

    @Test
    @DisplayName("FC10: 正常写入多个寄存器不抛异常")
    void testWriteRegisters_Success() throws ModbusTransportException {
        // Given
        WriteRegistersResponse response = mock(WriteRegistersResponse.class);
        when(response.isException()).thenReturn(false);
        when(master.send(any(WriteRegistersRequest.class))).thenReturn(response);

        // When & Then
        ModbusTcpUtil.WriteRegisters(master, 1, 300, new short[]{1, 2, 3, 4});
        verify(master).send(argThat((WriteRegistersRequest req) -> req.getSlaveId() == 1));
    }

    @Test
    @DisplayName("FC10: 写入空数组不抛异常")
    void testWriteRegisters_EmptyArray() throws ModbusTransportException {
        // Given
        WriteRegistersResponse response = mock(WriteRegistersResponse.class);
        when(response.isException()).thenReturn(false);
        when(master.send(any(WriteRegistersRequest.class))).thenReturn(response);

        // When & Then
        ModbusTcpUtil.WriteRegisters(master, 1, 300, new short[]{});
        verify(master).send(argThat((WriteRegistersRequest req) -> req.getSlaveId() == 1));
    }

    @Test
    @DisplayName("FC10: 异常响应时抛出RuntimeException")
    void testWriteRegisters_ExceptionResponse() throws ModbusTransportException {
        // Given
        WriteRegistersResponse response = mock(WriteRegistersResponse.class);
        when(response.isException()).thenReturn(true);
        when(response.getExceptionMessage()).thenReturn("校验错误");
        when(master.send(any(WriteRegistersRequest.class))).thenReturn(response);

        // Then
        assertThatThrownBy(() -> ModbusTcpUtil.WriteRegisters(master, 2, 1000, new short[]{10, 20}))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("设备ID：2")
            .hasMessageContaining("校验错误");
    }

    // ==================== FC0F 写入多个线圈 ====================

    @Test
    @DisplayName("FC0F: 正常写入多个线圈不抛异常")
    void testWriteCoils_Success() throws ModbusTransportException {
        // Given
        WriteCoilsResponse response = mock(WriteCoilsResponse.class);
        when(response.isException()).thenReturn(false);
        when(master.send(any(WriteCoilsRequest.class))).thenReturn(response);

        // When & Then
        ModbusTcpUtil.WriteCoils(master, 1, 50, new boolean[]{true, false, true});
        verify(master).send(argThat((WriteCoilsRequest req) -> req.getSlaveId() == 1));
    }

    @Test
    @DisplayName("FC0F: 异常响应时抛出RuntimeException")
    void testWriteCoils_ExceptionResponse() throws ModbusTransportException {
        // Given
        WriteCoilsResponse response = mock(WriteCoilsResponse.class);
        when(response.isException()).thenReturn(true);
        when(response.getExceptionMessage()).thenReturn("数量超限");
        when(master.send(any(WriteCoilsRequest.class))).thenReturn(response);

        // Then
        assertThatThrownBy(() -> ModbusTcpUtil.WriteCoils(master, 3, 200, new boolean[]{true}))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("位地址：200")
            .hasMessageContaining("数量超限");
    }

    // ==================== 边界条件测试 ====================

    @Test
    @DisplayName("边界: slaveId为0时应正常处理")
    void testBoundary_SlaveIdZero() throws ModbusTransportException {
        // Given
        ReadHoldingRegistersResponse response = mock(ReadHoldingRegistersResponse.class);
        when(response.getShortData()).thenReturn(new short[]{0});
        when(master.send(any(ReadHoldingRegistersRequest.class))).thenReturn(response);

        // When
        short[] result = ModbusTcpUtil.ReadHR(master, 0, 0, 1);

        // Then
        assertThat(result).hasSize(1);
        verify(master).send(argThat((ReadHoldingRegistersRequest req) -> req.getSlaveId() == 0));
    }

    @Test
    @DisplayName("边界: 读取数量为1时应正常处理")
    void testBoundary_ReadSingleRegister() throws ModbusTransportException {
        // Given
        ReadHoldingRegistersResponse response = mock(ReadHoldingRegistersResponse.class);
        when(response.getShortData()).thenReturn(new short[]{42});
        when(master.send(any(ReadHoldingRegistersRequest.class))).thenReturn(response);

        // When
        short[] result = ModbusTcpUtil.ReadHR(master, 1, 0, 1);

        // Then
        assertThat(result).containsExactly((short) 42);
        verify(master).send(argThat((ReadHoldingRegistersRequest req) -> req.getSlaveId() == 1));
    }

    @Test
    @DisplayName("边界: 非默认slaveId应正确传递到请求中")
    void testBoundary_NonDefaultSlaveId() throws ModbusTransportException {
        // Given
        ReadHoldingRegistersResponse response = mock(ReadHoldingRegistersResponse.class);
        when(response.getShortData()).thenReturn(new short[]{1, 2});
        when(master.send(any(ReadHoldingRegistersRequest.class))).thenReturn(response);

        // When
        short[] result = ModbusTcpUtil.ReadHR(master, 7, 10, 2);

        // Then
        assertThat(result).hasSize(2);
        verify(master).send(argThat((ReadHoldingRegistersRequest req) -> req.getSlaveId() == 7));
    }
}
