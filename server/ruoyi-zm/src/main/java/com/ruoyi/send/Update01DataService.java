package com.ruoyi.send;

import cn.hutool.json.JSONObject;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.schedule.util.WriteSaveUtil;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.domain.DevFaultRecord;
import com.ruoyi.zm.domain.DevInstruct;
import com.ruoyi.zm.domain.vo.DevFaultRecordVo;
import com.ruoyi.zm.mapper.DevFaultRecordMapper;
import com.ruoyi.zm.utils.Addr01Util;
import java.util.function.IntPredicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

// 更新功能码01 数据到数据库中的业务层
@Slf4j
@Service
@RequiredArgsConstructor
public class Update01DataService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final DevFaultRecordMapper faultRecordMapper;
    private final WriteSaveUtil writeSaveUtil;

    private static final String[] RECORD_05_ADDR = new String[]{"0XC300", "0XC301", "0XC302", "0XC303", "0XC304", "0XC305", "0XC306", "0XC307", "0XC308", "0XC309", "0XC30A", "0XC30B", "0XC30C", "0XC30D", "0XC30E",
        "0XC30F", "0XC310", "0XC311", "0XC312", "0XC313", "0XC314", "0XC315", "0XC316", "0XC317", "0XC318", "0XC319", "0XC31A", "0XC31B", "0XC31C", "0XC31D", "0XC31E", "0XC31F", "0XC320", "0XC321",
        "0XC322", "0XC323", "0XC324", "0XC325", "0XC326", "0XC327", "0XC328", "0XC329", "0XC32A", "0XC32B", "0XC32C", "0XC32D", "0XC32E", "0XC32F", "0XC330", "0XC331", "0XC332", "0XC333", "0XC334",
        "0XC335", "0XC336", "0XC337", "0XC338", "0XC339", "0XC33A", "0XC33B", "0XC33C", "0XC33D", "0XC33E", "0XC33F", "0XC340", "0XC341", "0XC342", "0XC343", "0XC344", "0XC345", "0XC346", "0XC347",
        "0XC348", "0XC349", "0XC34A", "0XC34B", "0XC34C", "0XC34D", "0XC34E", "0XC34F", "0XC350", "0XC351", "0XC352", "0XC353", "0XC354", "0XC355", "0XC356", "0XC357",
        "0XC358", "0XC359", "0XC35A", "0XC35B", "0XC35C", "0XC35D", "0XC35E", "0XC35F", "0XC360", "0XC361", "0XC362", "0XC363"};
    private final DListUtil dListUtil;
    private final MqttPublisher mqttPublisher;

    /**
     * 记录告警信息
     * type：类型 1告警记录 2事件记录
     * show：0为正常 1为故障
     */
    private void RecordFault(int deviceNo, int addrIndex, int type, int show) {
        String addr = String.format("0x%04X", Addr01Util.AddressTotalArr[addrIndex]);
        try {
            // 出现故障
            if (type == 1) {
                if (show == 1) {
                    String key = "zm:fault:" + deviceNo + ":" + addr;
                    if (!redisTemplate.hasKey(key)) {
                        DevFaultRecordVo fault = new DevFaultRecordVo();
                        fault.setDeviceId(deviceNo);
                        fault.setName(dListUtil.NameMap().getOrDefault((long) deviceNo, "未知机柜"));
                        fault.setMessage(addr);
                        fault.setStime(System.currentTimeMillis());
                        redisTemplate.opsForValue().set(key, fault);
                        // 记录故障到数据库
                        faultRecordMapper.insert(new DevFaultRecord() {{
                            setDeviceId(deviceNo);
                            setMessage(addr);
                            setStime(System.currentTimeMillis() / 1000);
                            setShowType(1);
                            setType(type);
                        }});
                    }
                }
                // 故障恢复
                else if (show == 0) {
                    String key = "zm:fault:" + deviceNo + ":" + addr;
                    if (redisTemplate.hasKey(key)) redisTemplate.delete(key);
                }
            }
            // 出现事件
            if (type == 2) {
                String key = "zm:dev_fault_record:" + deviceNo + ":" + addr;
                // 发生事件
                if (show == 1) {
                    // 记录事件信息
                    if (!redisTemplate.hasKey(key)) {
                        faultRecordMapper.insert(new DevFaultRecord() {{
                            setDeviceId(deviceNo);
                            setMessage(addr);
                            setStime(System.currentTimeMillis() / 1000);
                            setShowType(1);
                            setType(type);
                        }});
                        redisTemplate.opsForValue().set(key, addr);
                        // 发送遥控协议置 0 功能码 05 此处自定义功能码
                        DevInstruct i = new DevInstruct();
                        i.setSalveId(deviceNo);
                        i.setCode(5);
                        i.setAddr(RECORD_05_ADDR[addrIndex - 530]);
                        i.setAddrNum(1);
                        i.setWriteValue(new JSONObject().set("arr", new Short[]{0}).toString());
//                        writeSaveUtil.SaveSimple(i);
                    }
                }
                // 事件恢复，消除
                if (show == 0 && redisTemplate.hasKey(key)) redisTemplate.delete(key);
            }
        } catch (Exception e) {
            log.error("recordFault 方法出错，设备号：{},处理地址：{}，功能码 01",
                deviceNo, addr, e);
        }
    }

        final List<IntPredicate> FaultRanges = Arrays.asList(
        inRange(0, 140),    // 0x0000 ~ 0x0135
        inRange(143, 162),  // 0x0138 ~ 0x014B
        inRange(171, 172),  // 0x0154 ~ 0x0155
        inRange(194, 433),  // 0x0200 ~ 0x0425
        inRange(694, 821),  // 0x0800 ~ 0x0A1C
        inRange(827, 844)   // 0x0B36 ~ 0x0B47
    );
    final IntPredicate EventRange = inRange(530, 629); // 0x0600 ~ 0x0663

    private static IntPredicate inRange(int from, int to) {
        return i -> i >= from && i <= to;
    }
    private static final Map<String, int[]> mArr = new HashMap<String, int[]>() {{
        put("data", new int[100]);
    }};

    public void UpdateBoolData(int deviceNo, boolean[] data) {
        try {
            if (data[823]) {
                Map<String, Object> m = new HashMap<>();
                m.put("data", 0);
                mqttPublisher.publish(deviceNo, PublishKey.设置更新标志位, m);
            }
        } catch (Exception e) {
            log.warn("发布设置更新标志位异常, deviceNo={}", deviceNo, e);
        }
        try {
            mqttPublisher.publish(deviceNo, PublishKey.事件记录, mArr);
        } catch (Exception e) {
            log.warn("发布事件记录异常, deviceNo={}", deviceNo, e);
        }
        IntStream.range(0, data.length)
            .forEach(i -> {
                try {
                    boolean isFault = FaultRanges.stream().anyMatch(range -> range.test(i));
                    boolean isEvent = EventRange.test(i);

                    if (isFault || isEvent) {
                        // 1-故障，2-事件
                        int type = isFault ? 1 : 2;
                        int value = data[i] ? 1 : 0;
                        RecordFault(deviceNo, i, type, value);
                    }
                } catch (Exception e) {
                    log.error("UpdateBoolData 处理索引 {} 时出错", i, e);
                }
            });
    }
}
