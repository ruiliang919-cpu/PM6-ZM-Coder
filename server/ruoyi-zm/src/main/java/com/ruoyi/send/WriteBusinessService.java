package com.ruoyi.send;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.ruoyi.flag.InstructFlag;
import com.ruoyi.zm.domain.DevInstruct;
import com.ruoyi.zm.domain.DevWriteInstruct;
import com.ruoyi.zm.mapper.DevWriteInstructMapper;
import com.ruoyi.zm.utils.IdGenerator;
import com.ruoyi.zm.utils.ScaleUtil;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.msg.WriteCoilsRequest;
import com.serotonin.modbus4j.msg.WriteCoilsResponse;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// 写入协议的队列中的数据处理接口
@Slf4j
@Service
@RequiredArgsConstructor
public class WriteBusinessService {
    private final DevWriteInstructMapper writeInstructMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    // 05 遥控 / 06 遥调
    public void Business(List<DevInstruct> instructs, ModbusMaster tcp, int slaveId) {
        List<DevInstruct> insertBatch = new ArrayList<>();
        List<DevInstruct> updateBatch = new ArrayList<>();
        instructs.forEach(in -> {
            int addr = ScaleUtil.from16To10(in.getAddr());
            Short[] writeValueArray = ((JSONArray) JSONUtil.parse(in.getWriteValue()).getByPath("arr")).toList(Short.class).toArray(new Short[0]);
            in.setFeedback(0);
            switch (in.getCode()) {
                case 5: {
                    boolean[] writeValue = new boolean[writeValueArray.length];
                    for (int i = 0; i < writeValueArray.length; i++) writeValue[i] = writeValueArray[i] != 0;
                    try {
                        if ("0xC2C8".equals(in.getAddr())) {
                            WriteCoilsResponse c = (WriteCoilsResponse) tcp.send(new WriteCoilsRequest(slaveId, addr, new boolean[]{false}));
                            if (c != null && !c.isException()) {
                                insertBatch.add(in);
                            } else in.setFeedback(1);
                            // 重置更新总点标识
                            InstructFlag.SetFlag(InstructFlag.TOTAL_POINT, slaveId, true);
                            redisTemplate.opsForValue().set("zm:update:" + slaveId + ":Update-the-total-points", "true");
                        } else {
                            WriteCoilsResponse c = (WriteCoilsResponse) tcp.send(new WriteCoilsRequest(slaveId, addr, writeValue));
                            if (c != null && !c.isException()) {
                                insertBatch.add(in);
                            } else in.setFeedback(1);
                        }
                    } catch (Exception e) {
                        log.error("FC05写入失败, slaveId={}, addr={}, addrNum={}, error={}", slaveId, in.getAddr(), in.getAddrNum(), e.getMessage());
                        in.setFeedback(1);
                    }
                    break;
                }
                case 6: {
                    short[] writeValue = new short[writeValueArray.length];
                    for (int i = 0; i < writeValueArray.length; i++) writeValue[i] = writeValueArray[i];
                    try {
                        WriteRegistersRequest request = new WriteRegistersRequest(slaveId, addr, writeValue);
                        WriteRegistersResponse registersResponse = (WriteRegistersResponse) tcp.send(request);

                        if (registersResponse != null && !registersResponse.isException()) insertBatch.add(in);
                        else in.setFeedback(1);
                    } catch (Exception e) {
                        log.error("FC06写入失败, slaveId={}, addr={}, addrNum={}, error={}", slaveId, in.getAddr(), in.getAddrNum(), e.getMessage());
                        in.setFeedback(1);
                    }
                    break;
                }
            }
            updateBatch.add(in);
        });
        insertWriteInstruct(insertBatch);
        updateWriteInstruct(updateBatch);
    }

    // 更新写入报文的信息
    @Async
    public void updateWriteInstruct(List<DevInstruct> write) {
        List<DevWriteInstruct> collect = write.stream()
            .map(in -> new DevWriteInstruct() {{
                setId(in.getId());
                setType(in.getType());
                setAddr(in.getAddr());
                setWriteValue(in.getWriteValue());
                setSalveId(in.getSalveId());
                setFeedback(in.getFeedback());
                setTimestamp(in.getTimestamp());
                setCode(in.getCode());
                setIp(in.getIp());
                setAddrNum(in.getAddrNum());
            }}).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(collect)) writeInstructMapper.updateBatchById(collect);
    }

    // 插入写入协议的接收指令的报文信息
    @Async
    public void insertWriteInstruct(List<DevInstruct> instruct) {
        List<DevWriteInstruct> collect = instruct.stream()
            .map(in -> new DevWriteInstruct() {{
                // 重新赋予接收指令报文的ID
                setId(IdGenerator.UUIDId());
                // 设置报文类型为接收指令
                setType(2);
                setAddr(in.getAddr());
                setWriteValue(in.getWriteValue());
                setSalveId(in.getSalveId());
                setFeedback(in.getFeedback());
                setTimestamp(in.getTimestamp());
                setCode(in.getCode());
                setIp(in.getIp());
                setAddrNum(in.getAddrNum());
            }}).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(collect)) writeInstructMapper.insertBatch(collect);
    }
}
