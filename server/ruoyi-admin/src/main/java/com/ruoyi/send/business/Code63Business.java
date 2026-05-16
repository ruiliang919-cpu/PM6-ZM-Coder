package com.ruoyi.send.business;

import cn.hutool.json.JSONObject;
import com.ruoyi.schedule.util.WriteSaveUtil;
import com.ruoyi.zm.config.ModbusTCPManager;
import com.ruoyi.zm.domain.*;
import com.ruoyi.zm.service.impl.SceneLuxConfigService;
import com.ruoyi.zm.service.impl.SensorModuleQueueDataService;
import com.ruoyi.zm.utils.ScaleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.ruoyi.zm.utils.ScaleUtil.toAscII;

@Slf4j
@Component
@RequiredArgsConstructor
public class Code63Business {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String[] keySet1 = new String[]{"data1", "data2", "data3"};
    private final String[] keySet2 = new String[]{"data1", "data2", "data3", "data4", "data5"};
    private final RedisTemplate<String, short[]> shortArrayRedisTemplate;
    private final SceneLuxConfigService sceneLuxConfigService;
    private final SensorModuleQueueDataService sensorModuleQueueDataService;
    private final ModbusTCPManager masterTcp;
    private final WriteSaveUtil writeSaveUtil;

    // 遥调功能码06，其中的读取协议使用03功能码
    public void Business(DevInstruct instruct) {
        Integer salveId = instruct.getSalveId();
        // 处理更新总点
        if ("Update-the-total-points".equals(instruct.getAddr())) {
            DevInstruct gx = new DevInstruct();
            gx.setSalveId(salveId);
            gx.setCode(5);
            gx.setAddr("0xC2C8");
            gx.setAddrNum(1);
            gx.setWriteValue(new JSONObject().set("arr", new short[]{0}).toString());
            writeSaveUtil.SaveSimple(gx);
            return;
        }
        int addr = ScaleUtil.from16To10(instruct.getAddr());
        try {
//            short[] shorts = ModbusTcpUtil.ReadHR(masterTcp.getSlave(salveId), salveId, addr, instruct.getAddrNum());
//            response63(instruct, shorts);
        } catch (Exception e) {
            // log.error("06 Business", e);
        }
    }

    private void response63(DevInstruct instruct, short[] shorts) {
        Object response = dispose63Data(instruct, shorts);
        boolean result = eq63Cache(instruct, response);
        if (!result) update63Cache(instruct, response);
    }

    private boolean eq63Cache(DevInstruct instruct, Object data) {
        if (ObjectUtils.isEmpty(data) || (data + "").isEmpty()) return true;
        Object cache = null;
        try {
            String key = String.format("zm:queue:zm:cache:%d:%s:%d:%s", instruct.getCode(), instruct.getIp(), instruct.getSalveId(), instruct.getAddr());
            if (instruct.getAddr().equals("0xA000")
                || instruct.getAddr().equals("0xA5AE")
                || instruct.getAddr().equals("0xB63C")
                || instruct.getAddr().equals("0XB716")
                || instruct.getAddr().equals("0xA1F0")
                || instruct.getAddr().equals("0xA1FA")
                || instruct.getAddr().equals("0xA204")
                || instruct.getAddr().equals("0xA20E")
                || instruct.getAddr().equals("0xA218")
                || instruct.getAddr().equals("0xA222")
                || instruct.getAddr().equals("0xA22C")
                || instruct.getAddr().equals("0xA236")
                || instruct.getAddr().equals("0xA240")
                || instruct.getAddr().equals("0xA24A")
                || instruct.getAddr().equals("0xA254")
                || instruct.getAddr().equals("0xA25E")
                || instruct.getAddr().equals("0xA268")
                || instruct.getAddr().equals("0xA272")
                || instruct.getAddr().equals("0xA27C")
                || instruct.getAddr().equals("0xA286")
                || instruct.getAddr().equals("0xA483")
                || instruct.getAddr().equals("0xA48D")
                || instruct.getAddr().equals("0xA497")
                || instruct.getAddr().equals("0xA4A1")
                || instruct.getAddr().equals("0xA4AB")
                || instruct.getAddr().equals("0xA4B5")
                || instruct.getAddr().equals("0xA4BF")
                || instruct.getAddr().equals("0xA4C9")
                || instruct.getAddr().equals("0xA4D3")
                || instruct.getAddr().equals("0xA4DD")
                || instruct.getAddr().equals("0XA8E6")
                || instruct.getAddr().equals("0XABAF")
                || instruct.getAddr().equals("0xA007")
                || instruct.getAddr().equals("0XB715")
            ) {
                cache = shortArrayRedisTemplate.opsForValue().get(key);
                if (data instanceof short[] && cache != null)
                    return Arrays.equals((short[]) data, (short[]) cache);
                // 缓存中不存在数据，直接插入缓存，并返回 false，以便数据库更新数据
                if (ObjectUtils.isEmpty(cache)) {
                    shortArrayRedisTemplate.opsForValue().set(key, (short[]) data);
                    return false;
                }
            } else {
                cache = redisTemplate.opsForValue().get(key);
                if ("0XAE7A".equals(instruct.getAddr())) {
                    if (cache != null) {
                        Map<String, Object> cacheMap = (Map<String, Object>) cache;
                        Map<String, Object> dataMap = (Map<String, Object>) data;

                        // 比较 data1, data2, data3 数组
                        boolean isArrayEqual = true;
                        for (String key1 : keySet1) {
                            int[] cacheItem = (int[]) cacheMap.get(key1);
                            int[] dataItem = (int[]) dataMap.get(key1);

                            if (!Arrays.equals(cacheItem, dataItem)) {
                                isArrayEqual = false;
                            }
                        }

                        // 比较 data 列表
                        LinkedList<DevConfigInfraredSensor> cacheData = (LinkedList<DevConfigInfraredSensor>) cacheMap.get("data");
                        LinkedList<DevConfigInfraredSensor> dataData = (LinkedList<DevConfigInfraredSensor>) dataMap.get("data");

                        if (!cacheData.equals(dataData))  isArrayEqual = false;

                        return isArrayEqual;
                    }
                }

                if ("0XAE8F".equals(instruct.getAddr())) {
                    if (cache != null) {
                        Map<String, Object> cacheMap = (Map<String, Object>) cache;
                        Map<String, Object> dataMap = (Map<String, Object>) data;

                        boolean isArrayEqual = true;
                        for (String key1 : keySet2) {
                            int[] cacheItem = (int[]) cacheMap.get(key1);
                            int[] dataItem = (int[]) dataMap.get(key1);
                            if (!Arrays.equals(cacheItem, dataItem)) {
                                isArrayEqual = false;
                            }
                        }

                        // 比较 data 列表
                        LinkedList<DevConfigIlluminanceSensor> cacheData = (LinkedList<DevConfigIlluminanceSensor>) cacheMap.get("data");
                        LinkedList<DevConfigIlluminanceSensor> dataData = (LinkedList<DevConfigIlluminanceSensor>) dataMap.get("data");

                        if (!cacheData.equals(dataData))  isArrayEqual = false;

                        return isArrayEqual;
                    }
                }

                // 缓存中不存在数据，直接插入缓存，并返回 false，以便数据库更新数据
                if (ObjectUtils.isEmpty(cache)) {
                    redisTemplate.opsForValue().set(key, data);
                    return false;
                }
            }
        } catch (Exception e) {
            // log.error("~~~~~~eq63Cache~addr~{}~~~~~~eq63Cache~~~~~~~~~~~~", instruct.getAddr(), e);
        }
        if (cache != null)  return cache.equals(data);
        return false;
    }

    // 更新缓存的方法 遥调的读取协议
    private void update63Cache(DevInstruct instruct, Object data) {
        try {
            String key = String.format("zm:queue:zm:cache:%d:%s:%d:%s", instruct.getCode(), instruct.getIp(), instruct.getSalveId(), instruct.getAddr());
            if (instruct.getAddr().equals("0xA000")
                || instruct.getAddr().equals("0xA5AE")
                || instruct.getAddr().equals("0xB63C")
                || instruct.getAddr().equals("0XB716")
                || instruct.getAddr().equals("0xA1F0")
                || instruct.getAddr().equals("0xA1FA")
                || instruct.getAddr().equals("0xA204")
                || instruct.getAddr().equals("0xA20E")
                || instruct.getAddr().equals("0xA218")
                || instruct.getAddr().equals("0xA222")
                || instruct.getAddr().equals("0xA22C")
                || instruct.getAddr().equals("0xA236")
                || instruct.getAddr().equals("0xA240")
                || instruct.getAddr().equals("0xA24A")
                || instruct.getAddr().equals("0xA254")
                || instruct.getAddr().equals("0xA25E")
                || instruct.getAddr().equals("0xA268")
                || instruct.getAddr().equals("0xA272")
                || instruct.getAddr().equals("0xA27C")
                || instruct.getAddr().equals("0xA286")
                || instruct.getAddr().equals("0xA483")
                || instruct.getAddr().equals("0xA48D")
                || instruct.getAddr().equals("0xA497")
                || instruct.getAddr().equals("0xA4A1")
                || instruct.getAddr().equals("0xA4AB")
                || instruct.getAddr().equals("0xA4B5")
                || instruct.getAddr().equals("0xA4BF")
                || instruct.getAddr().equals("0xA4C9")
                || instruct.getAddr().equals("0xA4D3")
                || instruct.getAddr().equals("0xA4DD")
                || instruct.getAddr().equals("0XA8E6")
                || instruct.getAddr().equals("0XABAF")
                || instruct.getAddr().equals("0xA007")
                || instruct.getAddr().equals("0XB715")
            ) {
                short[] shorts = new short[0];
                if (data instanceof short[]) {
                    shorts = (short[]) data;
                } else if (data instanceof Short[]) {
                    shorts = new short[((Short[]) data).length];
                    for (int i = 0; i < shorts.length; i++) {
                        shorts[i] = ((Short[]) data)[i];
                    }
                } else if (data instanceof LinkedList) {
                    shorts = new short[((LinkedList<Short>) data).size()];
                    for (int i = 0; i < shorts.length; i++) {
                        shorts[i] = ((LinkedList<Short>) data).get(i);
                    }
                }
                if (!ObjectUtils.isEmpty(shorts)) {
                    shortArrayRedisTemplate.opsForValue().set(key, shorts);
                }
            } else {
                redisTemplate.opsForValue().set(key, data);
            }
        } catch (Exception e) {
            // log.error("~~~~~~~~~~~~~出错 出错 出错~~~~~~~~~~~~~~", e);
        }
    }

    private Object dispose63Data(DevInstruct instruct, short[] data) {
        Object result = data;
        if (instruct.getAddr().equals("0XA80A")
            || instruct.getAddr().equals("0XA853")
            || instruct.getAddr().equals("0XA89C")
        ) {
            result = sceneLuxConfigService.disposeSimpleControlData(instruct, data);
        } else if (instruct.getAddr().equals("0XAAEE")) {
            LinkedList<DevConfigTimeControlScene> list = new LinkedList<>();
            for (int i = 0; i < data.length; i += 8) {
                DevConfigTimeControlScene control = new DevConfigTimeControlScene();
                control.setId(Integer.valueOf(instruct.getSalveId() + "1" + (i / 8 + 1)));
                control.setDeviceId(instruct.getSalveId());
                control.setTimeControlId(1);
                control.setTimeFrameId(i / 8 + 1);
                control.setEnabledStatus((int) data[i]);
                //  场景选择
                control.setSceneSelect((int) data[i + 1]);
                control.setStime(toAscII(Arrays.copyOfRange(data, i + 2, i + 5)));
                control.setEtime(toAscII(Arrays.copyOfRange(data, i + 5, i + 8)));
                list.add(control);
            }
            result = list;
        } else if (instruct.getAddr().equals("0XAB2E")) {
            LinkedList<DevConfigTimeControlScene> list = new LinkedList<>();
            for (int i = 0; i < data.length; i += 8) {
                DevConfigTimeControlScene control = new DevConfigTimeControlScene();
                control.setId(Integer.valueOf(instruct.getSalveId() + "2" + (i / 8 + 1)));
                control.setDeviceId(instruct.getSalveId());
                control.setTimeControlId(2);
                control.setTimeFrameId(i / 8 + 1);
                control.setEnabledStatus((int) data[i]);
                //  场景选择
                control.setSceneSelect((int) data[i + 1]);
                control.setStime(toAscII(Arrays.copyOfRange(data, i + 2, i + 5)));
                control.setEtime(toAscII(Arrays.copyOfRange(data, i + 5, i + 8)));
                list.add(control);
            }
            result = list;
        } else if (instruct.getAddr().equals("0XAB6E")) {
            LinkedList<DevConfigTimeControlScene> list = new LinkedList<>();
            for (int i = 0; i < data.length; i += 8) {
                DevConfigTimeControlScene control = new DevConfigTimeControlScene();
                control.setId(Integer.valueOf(instruct.getSalveId() + "3" + (i / 8 + 1)));
                control.setDeviceId(instruct.getSalveId());
                control.setTimeControlId(3);
                control.setTimeFrameId(i / 8 + 1);
                control.setEnabledStatus((int) data[i]);
                //  场景选择
                control.setSceneSelect((int) data[i + 1]);
                control.setStime(toAscII(Arrays.copyOfRange(data, i + 2, i + 5)));
                control.setEtime(toAscII(Arrays.copyOfRange(data, i + 5, i + 8)));
                list.add(control);
            }
            result = list;
        } else if (instruct.getAddr().equals("0XAE7A")) {
            result = sensorModuleQueueDataService.infraredSensorMode(instruct, data);
        } else if (instruct.getAddr().equals("0XAE8F")) {
            result = sensorModuleQueueDataService.illuminanceSensorMode(instruct, data);
        } else if (instruct.getAddr().equals("0XAF1E")) {
            Map<String, Object> map = new HashMap<>();
            DevDeviceRemoteRead acNum = new DevDeviceRemoteRead();
            acNum.setId(instruct.getSalveId());
            acNum.setDeviceId(instruct.getSalveId());
            acNum.setAcSwitchNum((int) data[0]);
            map.put("acNum", acNum);

            data = Arrays.copyOfRange(data, 1, data.length);

            LinkedList<DevConfigTimeControlAc> list = new LinkedList<>();
            for (int i = 0; i < data.length; i += 6) {
                DevConfigTimeControlAc controlAc = new DevConfigTimeControlAc();
                controlAc.setId(Integer.valueOf(instruct.getSalveId() + "" + (i / 6 + 1)));
                controlAc.setDeviceId(instruct.getSalveId());
                controlAc.setFrameId(i / 6 + 1);
                controlAc.setSwitchStatus((int) data[i]);
                controlAc.setEnable((int) data[i + 1]);
                controlAc.setStimeHour((int) data[i + 2]);
                controlAc.setStimeMin((int) data[i + 3]);
                controlAc.setEtimeHour((int) data[i + 4]);
                controlAc.setEtimeMin((int) data[i + 5]);
                list.add(controlAc);
            }
            map.put("controlAc", list);

            result = map;
        } else if (instruct.getAddr().equals("0xAFC2")
            || instruct.getAddr().equals("0xAFE3")
            || instruct.getAddr().equals("0xB004")
            || instruct.getAddr().equals("0xB025")
            || instruct.getAddr().equals("0xB046")
            || instruct.getAddr().equals("0xB067")
            || instruct.getAddr().equals("0xB088")
            || instruct.getAddr().equals("0xB0A9")
            || instruct.getAddr().equals("0xB0CA")
            || instruct.getAddr().equals("0xB0EB")) {

            result = sceneLuxConfigService.disposeData(instruct, data);
        }
        return result;
    }
}
