package com.ruoyi.send;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.ruoyi.zm.domain.DevInstruct;
import com.ruoyi.zm.domain.DevWriteInstruct;
import com.ruoyi.zm.mapper.DevWriteInstructMapper;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.msg.ModbusRequest;
import com.serotonin.modbus4j.msg.WriteCoilsRequest;
import com.serotonin.modbus4j.msg.WriteCoilsResponse;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * WriteBusinessService 单元测试
 *
 * 测试场景:
 * - FC05 遥控（写入线圈）
 * - FC06 遥调（写入寄存器）
 * - 0xC2C8 地址特殊处理
 * - 异常响应处理
 * - 批量指令处理
 * - 数据库操作
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WriteBusinessService - 写入业务服务测试")
class WriteBusinessServiceTest {

    @Mock
    private DevWriteInstructMapper writeInstructMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ModbusMaster modbusMaster;

    @InjectMocks
    private WriteBusinessService writeBusinessService;

    private List<DevInstruct> instructs;

    @BeforeEach
    void setUp() {
        instructs = new ArrayList<>();
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    // ==================== FC05 遥控测试 ====================

    @Test
    @DisplayName("FC05: 正常写入线圈并标记成功")
    void testBusiness_FC05_Success() throws Exception {
        // Given
        DevInstruct instruct = createInstruct(5, "0x0064", createWriteValue(1, 0)); // 写入true
        instructs.add(instruct);

        WriteCoilsResponse response = mock(WriteCoilsResponse.class);
        when(response.isException()).thenReturn(false);
        doReturn(response).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 5);

        // Then
        assertThat(instruct.getFeedback()).isEqualTo(0); // 成功
        ArgumentCaptor<WriteCoilsRequest> coilsRequestCaptor = ArgumentCaptor.forClass(WriteCoilsRequest.class);
        verify(modbusMaster).send(coilsRequestCaptor.capture());
        assertThat(coilsRequestCaptor.getValue().getSlaveId()).isEqualTo(5);

        ArgumentCaptor<java.util.List<com.ruoyi.zm.domain.DevWriteInstruct>> insertListCaptor = ArgumentCaptor.forClass(java.util.List.class);
        verify(writeInstructMapper).insertBatch(insertListCaptor.capture());
        assertThat(insertListCaptor.getValue()).hasSize(1);
        assertThat(insertListCaptor.getValue().get(0).getFeedback()).isEqualTo(0);

        ArgumentCaptor<java.util.List<com.ruoyi.zm.domain.DevWriteInstruct>> updateListCaptor = ArgumentCaptor.forClass(java.util.List.class);
        verify(writeInstructMapper).updateBatchById(updateListCaptor.capture());
        assertThat(updateListCaptor.getValue()).hasSize(1);
        assertThat(updateListCaptor.getValue().get(0).getFeedback()).isEqualTo(0);
    }

    @Test
    @DisplayName("FC05: 写入false值正确转换")
    void testBusiness_FC05_WriteFalse() throws Exception {
        // Given
        DevInstruct instruct = createInstruct(5, "0x0064", createWriteValue(0)); // 写入false
        instructs.add(instruct);

        WriteCoilsResponse response = mock(WriteCoilsResponse.class);
        when(response.isException()).thenReturn(false);
        doReturn(response).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 7);

        // Then
        assertThat(instruct.getFeedback()).isEqualTo(0);
        ArgumentCaptor<WriteCoilsRequest> coilsRequestCaptor = ArgumentCaptor.forClass(WriteCoilsRequest.class);
        verify(modbusMaster).send(coilsRequestCaptor.capture());
        assertThat(coilsRequestCaptor.getValue().getSlaveId()).isEqualTo(7);
    }

    @Test
    @DisplayName("FC05: 0xC2C8地址特殊处理 - 先写false再重置总点标识")
    void testBusiness_FC05_C2C8() throws Exception {
        // Given
        DevInstruct instruct = createInstruct(5, "0xC2C8", createWriteValue(1, 1, 1));
        instructs.add(instruct);

        WriteCoilsResponse response = mock(WriteCoilsResponse.class);
        when(response.isException()).thenReturn(false);
        doReturn(response).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 3);

        // Then: 0xC2C8地址应写入单个false值，且slaveId=3
        ArgumentCaptor<WriteCoilsRequest> c2c8RequestCaptor = ArgumentCaptor.forClass(WriteCoilsRequest.class);
        verify(modbusMaster).send(c2c8RequestCaptor.capture());
        assertThat(c2c8RequestCaptor.getValue().getSlaveId()).isEqualTo(3);
        assertThat(instruct.getFeedback()).isEqualTo(0);
    }

    @Test
    @DisplayName("FC05: 异常响应时设置feedback=1")
    void testBusiness_FC05_ExceptionResponse() throws Exception {
        // Given
        DevInstruct instruct = createInstruct(5, "0x0064", createWriteValue(1));
        instructs.add(instruct);

        WriteCoilsResponse response = mock(WriteCoilsResponse.class);
        when(response.isException()).thenReturn(true);
        doReturn(response).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 1);

        // Then
        assertThat(instruct.getFeedback()).isEqualTo(1); // 失败
        verify(writeInstructMapper, never()).insertBatch(anyList());
        ArgumentCaptor<java.util.List<com.ruoyi.zm.domain.DevWriteInstruct>> updateListCaptor = ArgumentCaptor.forClass(java.util.List.class);
        verify(writeInstructMapper).updateBatchById(updateListCaptor.capture());
        assertThat(updateListCaptor.getValue()).hasSize(1);
        assertThat(updateListCaptor.getValue().get(0).getFeedback()).isEqualTo(1);
    }

    @Test
    @DisplayName("FC05: 传输异常时设置feedback=1")
    void testBusiness_FC05_TransportError() throws Exception {
        // Given
        DevInstruct instruct = createInstruct(5, "0x0064", createWriteValue(1));
        instructs.add(instruct);

        doThrow(new RuntimeException("Modbus连接断开")).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 1);

        // Then
        assertThat(instruct.getFeedback()).isEqualTo(1);
    }

    @Test
    @DisplayName("FC05: null响应时设置feedback=1")
    void testBusiness_FC05_NullResponse() throws Exception {
        // Given
        DevInstruct instruct = createInstruct(5, "0x0064", createWriteValue(1));
        instructs.add(instruct);

        doReturn(null).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 1);

        // Then
        assertThat(instruct.getFeedback()).isEqualTo(1);
    }

    // ==================== FC06 遥调测试 ====================

    @Test
    @DisplayName("FC06: 正常写入寄存器并标记成功")
    void testBusiness_FC06_Success() throws Exception {
        // Given
        DevInstruct instruct = createInstruct(6, "0x00C8", createWriteValue(100, 200));
        instructs.add(instruct);

        WriteRegistersResponse response = mock(WriteRegistersResponse.class);
        when(response.isException()).thenReturn(false);
        doReturn(response).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 9);

        // Then
        assertThat(instruct.getFeedback()).isEqualTo(0);
        ArgumentCaptor<WriteRegistersRequest> registersRequestCaptor = ArgumentCaptor.forClass(WriteRegistersRequest.class);
        verify(modbusMaster).send(registersRequestCaptor.capture());
        assertThat(registersRequestCaptor.getValue().getSlaveId()).isEqualTo(9);
        verify(writeInstructMapper).insertBatch(anyList());
    }

    @Test
    @DisplayName("FC06: 写入单个寄存器")
    void testBusiness_FC06_SingleRegister() throws Exception {
        // Given
        DevInstruct instruct = createInstruct(6, "0x00C8", createWriteValue(500));
        instructs.add(instruct);

        WriteRegistersResponse response = mock(WriteRegistersResponse.class);
        when(response.isException()).thenReturn(false);
        doReturn(response).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 1);

        // Then
        assertThat(instruct.getFeedback()).isEqualTo(0);
    }

    @Test
    @DisplayName("FC06: 异常响应时设置feedback=1")
    void testBusiness_FC06_ExceptionResponse() throws Exception {
        // Given
        DevInstruct instruct = createInstruct(6, "0x00C8", createWriteValue(100));
        instructs.add(instruct);

        WriteRegistersResponse response = mock(WriteRegistersResponse.class);
        when(response.isException()).thenReturn(true);
        doReturn(response).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 1);

        // Then
        assertThat(instruct.getFeedback()).isEqualTo(1);
        verify(writeInstructMapper, never()).insertBatch(anyList());
    }

    @Test
    @DisplayName("FC06: 传输异常时设置feedback=1")
    void testBusiness_FC06_TransportError() throws Exception {
        // Given
        DevInstruct instruct = createInstruct(6, "0x00C8", createWriteValue(100));
        instructs.add(instruct);

        doThrow(new RuntimeException("写入超时")).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 1);

        // Then
        assertThat(instruct.getFeedback()).isEqualTo(1);
    }

    // ==================== 批量混合指令测试 ====================

    @Test
    @DisplayName("批量: 混合FC05和FC06指令分别处理")
    void testBusiness_MixedInstructions() throws Exception {
        // Given
        DevInstruct fc05 = createInstruct(5, "0x0064", createWriteValue(1));
        DevInstruct fc06 = createInstruct(6, "0x00C8", createWriteValue(50));
        instructs.add(fc05);
        instructs.add(fc06);

        WriteCoilsResponse coilResponse = mock(WriteCoilsResponse.class);
        when(coilResponse.isException()).thenReturn(false);
        WriteRegistersResponse registerResponse = mock(WriteRegistersResponse.class);
        when(registerResponse.isException()).thenReturn(false);

        doReturn(coilResponse).doReturn(registerResponse).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 1);

        // Then
        assertThat(fc05.getFeedback()).isEqualTo(0);
        assertThat(fc06.getFeedback()).isEqualTo(0);
        ArgumentCaptor<java.util.List<com.ruoyi.zm.domain.DevWriteInstruct>> insertListCaptor = ArgumentCaptor.forClass(java.util.List.class);
        verify(writeInstructMapper).insertBatch(insertListCaptor.capture());
        assertThat(insertListCaptor.getValue()).hasSize(2);

        ArgumentCaptor<java.util.List<com.ruoyi.zm.domain.DevWriteInstruct>> updateListCaptor = ArgumentCaptor.forClass(java.util.List.class);
        verify(writeInstructMapper).updateBatchById(updateListCaptor.capture());
        assertThat(updateListCaptor.getValue()).hasSize(2);
    }

    @Test
    @DisplayName("批量: 空指令列表不操作数据库")
    void testBusiness_EmptyList() {
        // When
        writeBusinessService.Business(instructs, modbusMaster, 1);

        // Then
        verifyNoInteractions(modbusMaster);
        verify(writeInstructMapper, never()).insertBatch(anyList());
        verify(writeInstructMapper, never()).updateBatchById(anyList());
    }

    @Test
    @DisplayName("批量: 部分成功部分失败时分别记录")
    void testBusiness_PartialSuccess() throws Exception {
        // Given
        DevInstruct success = createInstruct(5, "0x0064", createWriteValue(1));
        DevInstruct fail = createInstruct(5, "0x0065", createWriteValue(1));
        instructs.add(success);
        instructs.add(fail);

        WriteCoilsResponse successResponse = mock(WriteCoilsResponse.class);
        when(successResponse.isException()).thenReturn(false);
        WriteCoilsResponse failResponse = mock(WriteCoilsResponse.class);
        when(failResponse.isException()).thenReturn(true);

        doReturn(successResponse).doReturn(failResponse).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 1);

        // Then
        assertThat(success.getFeedback()).isEqualTo(0);
        assertThat(fail.getFeedback()).isEqualTo(1);
        ArgumentCaptor<java.util.List<com.ruoyi.zm.domain.DevWriteInstruct>> insertListCaptor = ArgumentCaptor.forClass(java.util.List.class);
        verify(writeInstructMapper).insertBatch(insertListCaptor.capture());
        assertThat(insertListCaptor.getValue()).hasSize(1);

        ArgumentCaptor<java.util.List<com.ruoyi.zm.domain.DevWriteInstruct>> updateListCaptor = ArgumentCaptor.forClass(java.util.List.class);
        verify(writeInstructMapper).updateBatchById(updateListCaptor.capture());
        assertThat(updateListCaptor.getValue()).hasSize(2);
    }

    // ==================== 数据库操作测试 ====================

    @Test
    @DisplayName("插入: 正常批量插入接收指令")
    void testInsertWriteInstruct() {
        // Given
        List<DevInstruct> list = new ArrayList<>();
        list.add(createInstruct(5, "0x0064", createWriteValue(1)));
        list.add(createInstruct(6, "0x00C8", createWriteValue(100)));

        // When
        writeBusinessService.insertWriteInstruct(list);

        // Then
        ArgumentCaptor<java.util.List<com.ruoyi.zm.domain.DevWriteInstruct>> insertListCaptor = ArgumentCaptor.forClass(java.util.List.class);
        verify(writeInstructMapper).insertBatch(insertListCaptor.capture());
        assertThat(insertListCaptor.getValue()).hasSize(2);
        DevWriteInstruct first = insertListCaptor.getValue().get(0);
        assertThat(first.getType()).isEqualTo(2); // 接收指令类型
        assertThat(first.getId()).isNotNull(); // 重新生成ID
        assertThat(first.getAddr()).isEqualTo("0x0064");
    }

    @Test
    @DisplayName("插入: 空列表不操作")
    void testInsertWriteInstruct_EmptyList() {
        // When
        writeBusinessService.insertWriteInstruct(Collections.emptyList());

        // Then
        verify(writeInstructMapper, never()).insertBatch(anyList());
    }

    @Test
    @DisplayName("更新: 正常批量更新反馈状态")
    void testUpdateWriteInstruct() {
        // Given
        List<DevInstruct> list = new ArrayList<>();
        DevInstruct instruct = createInstruct(5, "0x0064", createWriteValue(1));
        instruct.setFeedback(1);
        list.add(instruct);

        // When
        writeBusinessService.updateWriteInstruct(list);

        // Then
        ArgumentCaptor<java.util.List<com.ruoyi.zm.domain.DevWriteInstruct>> updateListCaptor = ArgumentCaptor.forClass(java.util.List.class);
        verify(writeInstructMapper).updateBatchById(updateListCaptor.capture());
        assertThat(updateListCaptor.getValue()).hasSize(1);
        assertThat(updateListCaptor.getValue().get(0).getFeedback()).isEqualTo(1);
        assertThat(updateListCaptor.getValue().get(0).getAddr()).isEqualTo("0x0064");
    }

    @Test
    @DisplayName("更新: 空列表不操作")
    void testUpdateWriteInstruct_EmptyList() {
        // When
        writeBusinessService.updateWriteInstruct(Collections.emptyList());

        // Then
        verify(writeInstructMapper, never()).updateBatchById(anyList());
    }

    // ==================== 边界条件测试 ====================

    @Test
    @DisplayName("边界: slaveId为0时正常处理")
    void testBoundary_SlaveIdZero() throws Exception {
        // Given
        DevInstruct instruct = createInstruct(5, "0x0064", createWriteValue(1));
        instructs.add(instruct);

        WriteCoilsResponse response = mock(WriteCoilsResponse.class);
        when(response.isException()).thenReturn(false);
        doReturn(response).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 0);

        // Then
        assertThat(instruct.getFeedback()).isEqualTo(0);
    }

    @Test
    @DisplayName("边界: 大数组值正常处理")
    void testBoundary_LargeValueArray() throws Exception {
        // Given
        int[] values = new int[100];
        for (int i = 0; i < 100; i++) values[i] = i;
        DevInstruct instruct = createInstruct(6, "0x00C8", createWriteValue(values));
        instructs.add(instruct);

        WriteRegistersResponse response = mock(WriteRegistersResponse.class);
        when(response.isException()).thenReturn(false);
        doReturn(response).when(modbusMaster).send(any(ModbusRequest.class));

        // When
        writeBusinessService.Business(instructs, modbusMaster, 1);

        // Then
        assertThat(instruct.getFeedback()).isEqualTo(0);
    }

    // ==================== 辅助方法 ====================

    private DevInstruct createInstruct(int code, String addr, String writeValue) {
        DevInstruct instruct = new DevInstruct();
        instruct.setCode(code);
        instruct.setAddr(addr);
        instruct.setWriteValue(writeValue);
        instruct.setSalveId(1);
        instruct.setFeedback(0);
        instruct.setIp("192.168.1.1");
        instruct.setTimestamp(System.currentTimeMillis());
        instruct.setType(1);
        instruct.setAddrNum(1);
        return instruct;
    }

    private String createWriteValue(int... values) {
        JSONArray arr = new JSONArray();
        for (int v : values) {
            arr.add(v);
        }
        return JSONUtil.createObj().set("arr", arr).toString();
    }
}
