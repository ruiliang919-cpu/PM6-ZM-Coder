package com.ruoyi.zm.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ruoyi.cache.Key;
import com.ruoyi.cache.WriteQueueCache;
import com.ruoyi.cache.ZoneUtilCache;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.modbus.util.RecordPlus;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqtt03.addr03.handler.addr0XB715.Addr0XB715;
import com.ruoyi.mqttwrite.accontrol.AcValue;
import com.ruoyi.mqttwrite.loopcontrol.LoopControlVO;
import com.ruoyi.mqttwrite.module.ModuleSelect;
import com.ruoyi.mqttwrite.module.ModuleValue;
import com.ruoyi.mqttwrite.simple.SimpleValue;
import com.ruoyi.utils.device.DListUtil;
import com.ruoyi.zm.domain.*;
import com.ruoyi.zm.domain.vo.*;
import com.ruoyi.zm.utils.IdGenerator;
import com.ruoyi.zm.utils.ScaleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class WriteControlService {

    private final MqttPublisher mqttPublisher;
    private final Key key;
    private final WriteQueueCache writeQueueCache;
    private final RecordPlus record;
    private final DListUtil dListUtil;
    private final ZoneUtilCache zoneUtilCache;
    private final IDevBaseDistrictService districtService;
    private final IDevBaseDeviceService deviceService;

    public static final String[] simpleControlAddrArr = {"0XA80A", "0XA853", "0XA89C"};
    private static final String[] ADDR_SIMPLE = {"", "0XA80A", "0XA853", "0XA89C"};

    public R<?> workModule(Integer deviceId, Integer workModule) {
        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        m.put("addr", "0xC001");
        m.put("data", workModule);
        mqttPublisher.publish(deviceId, PublishKey.工作模式, m);
        writeQueueCache.setWorkModule(deviceId, workModule);
        return R.ok("指令下发成功");
    }

    public R<?> selectHandModule(Integer deviceId, Integer handModule) {
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        if (handModule == 1) remote.setHandModule("loop");
        else if (handModule == 2) remote.setHandModule("group");
        else if (handModule == 3) remote.setHandModule("scene");
        writeQueueCache.setRemoteKey(key.getCreateTCP(deviceId).getIp(), deviceId, "0XB715", remote);
        ModuleSelect v = new ModuleSelect();
        v.setData(Collections.singletonList(remote));
        mqttPublisher.publish(deviceId, PublishKey.模式选择, v);
        return R.ok("指令已下发");
    }

    public R<?> groupControlSwitch(WriteGroupReqVo reqVo) {
        if (ObjectUtils.isEmpty(reqVo) || Arrays.isNullOrEmpty(reqVo.getGroupSelectArr())) {
            return R.warn("输入数据无效");
        }
        LoopControlVO v = new LoopControlVO();
        v.setModule(1);
        v.setAddr("0xC046");
        LoopControlVO.D d = new LoopControlVO.D();
        d.setValue(reqVo.getSwitchStatus());

        int[] groupSelectArr = new int[16];
        for (int i = 0; i < reqVo.getGroupSelectArr().length; i++) {
            groupSelectArr[i] = reqVo.getGroupSelectArr()[i];
        }

        d.setGroupArr(groupSelectArr);
        v.setData(d);
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.分组控制总开关, v);
        writeQueueCache.setGroupSelect(reqVo.getDeviceId(), groupSelectArr);
        return R.ok("指令下发成功");
    }

    public R<?> groupControlLux(WriteGroupReqVo reqVo) {
        if (ObjectUtils.isEmpty(reqVo) || Arrays.isNullOrEmpty(reqVo.getGroupSelectArr())) {
            return R.warn("输入数据无效");
        }
        LoopControlVO v = new LoopControlVO();
        v.setModule(2);
        v.setAddr("0xC046");
        LoopControlVO.D d = new LoopControlVO.D();
        d.setLux(reqVo.getLux());

        int[] groupSelectArr = new int[16];
        for (int i = 0; i < reqVo.getGroupSelectArr().length; i++) {
            groupSelectArr[i] = reqVo.getGroupSelectArr()[i];
        }

        d.setGroupArr(groupSelectArr);
        v.setData(d);
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.分组控制总开关, v);
        writeQueueCache.setGroupSelect(reqVo.getDeviceId(), groupSelectArr);
        return R.ok("操作成功");
    }

    public void updateSimpleControlCommon(SimpleValue simpleValue, Integer deviceId) {
        int[] controlArr = new int[73];
        Map<String, Object> map = (HashMap<String, Object>) key.getRemote(deviceId, simpleControlAddrArr[simpleValue.getControlId() - 1]);
        if (map == null) {
            map = new HashMap<>();
            map.put("data1", null);
            map.put("data2", null);
        }
        List<DevConfigTimeControl> data1 = (List<DevConfigTimeControl>) map.get("data1");
        List<DevConfigSimpleGroup> data2 = (List<DevConfigSimpleGroup>) map.get("data2");

        if (data1 == null) {
            data1 = new LinkedList<>();
            for (int i = 0; i < 8; i++) {
                DevConfigTimeControl control = new DevConfigTimeControl();
                control.setTimeControlId(simpleValue.getControlId());
                control.setDeviceId(Long.valueOf(deviceId));
                control.setTimeFrameId(i + 1);
                data1.add(control);
            }
        }

        if (data2 == null) {
            data2 = new LinkedList<>();
            for (int i = 0; i < 16; i++) {
                DevConfigSimpleGroup group = new DevConfigSimpleGroup();
                group.setSimpleId(simpleValue.getControlId());
                group.setDeviceId(deviceId);
                group.setGroupId(i + 1);
                group.setSelectStatus(0);
                data2.add(group);
            }
        }

        List<SimpleControlTable> table = simpleValue.getData1();
        for (int i = 0; i < controlArr.length - 1; i += 9) {
            controlArr[i] = table.get((i / 9)).getLux();
            data1.get(i / 9).setLux(table.get((i / 9)).getLux());
            controlArr[i + 1] = table.get((i / 9)).getSwitchStatus();
            data1.get(i / 9).setSwitchStatus(table.get((i / 9)).getSwitchStatus());
            controlArr[i + 2] = table.get((i / 9)).getEnabledStatus();
            data1.get(i / 9).setEnabledStatus(table.get((i / 9)).getEnabledStatus());
            int[] sTime = ScaleUtil.gbkToArr(table.get((i / 9)).getStime());
            controlArr[i + 3] = sTime[0];
            controlArr[i + 4] = sTime[1];
            controlArr[i + 5] = sTime[2];
            data1.get(i / 9).setStime(table.get((i / 9)).getStime());
            int[] eTime = ScaleUtil.gbkToArr(table.get((i / 9)).getEtime());
            controlArr[i + 6] = eTime[0];
            controlArr[i + 7] = eTime[1];
            controlArr[i + 8] = eTime[2];
            data1.get(i / 9).setEtime(table.get((i / 9)).getEtime());
        }

        if (!Arrays.isNullOrEmpty(simpleValue.getData2())) {
            controlArr[72] = ScaleUtil.convertGroupsToRegisterValue(convertFromData2(simpleValue.getData2()));
            for (int i = 0; i < 16; i++) {
                data2.get(i).setSelectStatus(0);
            }
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < simpleValue.getData2().length; j++) {
                    if (simpleValue.getData2()[j] == 1 && i + 1 == j + 1) {
                        data2.get(i).setSelectStatus(1);
                    }
                }
            }
        }

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);

        short[] data = new short[2];
        data[0] = (short) (simpleValue.getControlId() - 1);

        map.put("data1", data1);
        map.put("data2", data2);
        writeQueueCache.setQueueCache(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), simpleControlAddrArr[simpleValue.getControlId() - 1], map);

        DevInstruct instruct = new DevInstruct();
        instruct.setIp(tcpVo.getIp());
        instruct.setSalveId(deviceId);
        instruct.setFeedback(2);

        instruct.setCode(6);
        instruct.setId(IdGenerator.UUIDId());
        instruct.setAddr(ADDR_SIMPLE[simpleValue.getControlId()]);
        instruct.setAddrNum(73);
        instruct.setWriteValue(new JSONObject().set("arr", controlArr).toString());
        writeQueueCache.pushInstruction(deviceId, instruct);

        if (simpleValue.isEnabled()) {
            data[1] = (short) (simpleValue.getControlId());
            writeQueueCache.setQueueCacheShortArr(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XA8E6", new short[]{(short) data[1]});

            instruct.setId(IdGenerator.UUIDId());
            instruct.setAddr("0XA8E5");
            instruct.setAddrNum(1);
            instruct.setWriteValue(new JSONObject().set("arr", new short[]{data[0]}).toString());
            writeQueueCache.pushInstruction(deviceId, instruct);

            instruct.setId(IdGenerator.UUIDId());
            instruct.setAddr("0XA8E6");
            instruct.setAddrNum(1);
            instruct.setWriteValue(new JSONObject().set("arr", new short[]{data[1]}).toString());
            writeQueueCache.pushInstruction(deviceId, instruct);
        } else {
            short[] value = writeQueueCache.getQueueCacheShortArr(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XA8E6");

            if (value != null && value.length > 0) {
                instruct.setId(IdGenerator.UUIDId());
                instruct.setAddr("0XA8E5");
                instruct.setAddrNum(1);
                instruct.setWriteValue(new JSONObject().set("arr", new short[]{(short) (simpleValue.getControlId() - 1)}).toString());
                writeQueueCache.pushInstruction(deviceId, instruct);

                instruct.setId(IdGenerator.UUIDId());
                instruct.setAddr("0XA8E6");
                instruct.setAddrNum(1);
                instruct.setWriteValue(new JSONObject().set("arr", new short[]{value[0]}).toString());
                writeQueueCache.pushInstruction(deviceId, instruct);
            }
        }

        short[] saveArr = {1};

        instruct.setCode(5);
        instruct.setId(IdGenerator.UUIDId());
        instruct.setAddr("0xC2C9");
        instruct.setAddrNum(1);
        instruct.setWriteValue(new JSONObject().set("arr", saveArr).toString());
        writeQueueCache.pushInstruction(deviceId, instruct);
    }

    public Integer[] convertFromData2(Integer[] data2) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < data2.length; i++) {
            if (data2[i] == 1) result.add(i + 1);
        }
        return result.toArray(new Integer[0]);
    }

    public R<?> updateZoneLightSwitchInternal(Integer zoneId, Integer swStatus) {
        record.runModule();

        DevBaseDistrict assist = new DevBaseDistrict();
        assist.setOrderNo(zoneId);
        assist.setSwitchStatus(swStatus == 1 ? 1 : 0);
        districtService.insertOrUpdate(assist);

        assist = districtService.selectById(zoneId.longValue());
        zoneId = Math.toIntExact(assist.getId());

        List<Long> devBaseDevices = dListUtil.Nos();
        if (devBaseDevices != null && !devBaseDevices.isEmpty()) {
            for (Long no : devBaseDevices) {
                try {
                    List<Integer> groups = zoneUtilCache.getGroupByZone(Math.toIntExact(no), java.util.Arrays.asList(zoneId));

                    if (key.getTelecommand(Math.toIntExact(no), 164) == 0)
                        workModule(Math.toIntExact(no), 1);
                    selectHandModule(Math.toIntExact(no), 2);

                    WriteGroupReqVo vo = new WriteGroupReqVo();
                    vo.setSwitchStatus(swStatus);
                    vo.setGroupSelectArr(groups.toArray(new Integer[0]));
                    vo.setDeviceId(Math.toIntExact(no));
                    groupControlSwitch(vo);

                } catch (Exception e) {
                    log.error("Error in updateZoneLightSwitch for device: {}", no, e);
                }
            }
        }
        return R.ok("指令已下发");
    }

    public R<?> updateZoneLightLuxInternal(Integer zoneId, Integer lux) {
        if (lux < 0 || lux > 100) {
            return R.fail("亮度值设定范围 0~100");
        }
        record.runModule();

        DevBaseDistrict assist = new DevBaseDistrict();
        assist.setOrderNo(zoneId);
        assist.setLux(lux);
        districtService.insertOrUpdate(assist);

        assist = districtService.selectById(zoneId.longValue());
        zoneId = Math.toIntExact(assist.getId());

        List<Long> devBaseDevices = dListUtil.Nos();
        if (devBaseDevices != null && !devBaseDevices.isEmpty()) {
            for (Long no : devBaseDevices) {
                try {
                    List<Integer> groups = zoneUtilCache.getGroupByZone(Math.toIntExact(no), java.util.Arrays.asList(zoneId));

                    if (key.getTelecommand(Math.toIntExact(no), 164) == 0)
                        workModule(Math.toIntExact(no), 1);
                    selectHandModule(Math.toIntExact(no), 2);

                    WriteGroupReqVo vo = new WriteGroupReqVo();
                    vo.setLux(lux);
                    vo.setGroupSelectArr(groups.toArray(new Integer[0]));
                    vo.setDeviceId(Math.toIntExact(no));
                    groupControlLux(vo);
                } catch (Exception e) {
                    log.error("Error in updateZoneLightLux for device: {}", no, e);
                }
            }
        }
        return R.ok("指令已下发");
    }

    public R<?> updateSimpleControl(SimpleValue simpleValue, Integer deviceId) {
        mqttPublisher.publish(deviceId, PublishKey.普通时控, simpleValue);

        for (int i = 0; i < simpleValue.getData1().size(); i++) {
            if (simpleValue.getData1().get(i).getLux() < 0 || simpleValue.getData1().get(i).getLux() > 100)
                return R.warn("普通时控亮度上下限值 0-100");
        }

        int[] controlArr = new int[73];

        Map<String, Object> map = (HashMap<String, Object>) key.getRemote(deviceId, simpleControlAddrArr[simpleValue.getControlId() - 1]);
        if (map == null) {
            map = new HashMap<>();
            map.put("data1", null);
            map.put("data2", null);
        }

        List<DevConfigTimeControl> data1 = (List<DevConfigTimeControl>) map.get("data1");
        List<DevConfigSimpleGroup> data2 = (List<DevConfigSimpleGroup>) map.get("data2");

        if (data1 == null) {
            data1 = new LinkedList<>();
            for (int i = 0; i < 8; i++) {
                DevConfigTimeControl control = new DevConfigTimeControl();
                control.setTimeControlId(simpleValue.getControlId());
                control.setDeviceId(Long.valueOf(deviceId));
                control.setTimeFrameId(i + 1);
                data1.add(control);
            }
        }

        if (data2 == null) {
            data2 = new LinkedList<>();
            for (int i = 0; i < 16; i++) {
                DevConfigSimpleGroup group = new DevConfigSimpleGroup();
                group.setSimpleId(simpleValue.getControlId());
                group.setDeviceId(deviceId);
                group.setGroupId(i + 1);
                group.setSelectStatus(0);
                data2.add(group);
            }
        }

        List<SimpleControlTable> table = simpleValue.getData1();
        for (int i = 0; i < controlArr.length - 1; i += 9) {
            controlArr[i] = table.get((i / 9)).getLux();
            data1.get(i / 9).setLux(table.get((i / 9)).getLux());
            controlArr[i + 1] = table.get((i / 9)).getSwitchStatus();
            data1.get(i / 9).setSwitchStatus(table.get((i / 9)).getSwitchStatus());
            controlArr[i + 2] = table.get((i / 9)).getEnabledStatus();
            data1.get(i / 9).setEnabledStatus(table.get((i / 9)).getEnabledStatus());
            int[] sTime = ScaleUtil.gbkToArr(table.get((i / 9)).getStime());
            controlArr[i + 3] = sTime[0];
            controlArr[i + 4] = sTime[1];
            controlArr[i + 5] = sTime[2];
            data1.get(i / 9).setStime(table.get((i / 9)).getStime());
            int[] eTime = ScaleUtil.gbkToArr(table.get((i / 9)).getEtime());
            controlArr[i + 6] = eTime[0];
            controlArr[i + 7] = eTime[1];
            controlArr[i + 8] = eTime[2];
            data1.get(i / 9).setEtime(table.get((i / 9)).getEtime());
        }

        if (!Arrays.isNullOrEmpty(simpleValue.getData2())) {
            controlArr[72] = ScaleUtil.convertGroupsToRegisterValue(convertFromData2(simpleValue.getData2()));
            for (int i = 0; i < 16; i++) data2.get(i).setSelectStatus(0);
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < simpleValue.getData2().length; j++) {
                    if (simpleValue.getData2()[j] == 1 && i + 1 == j + 1)
                        data2.get(i).setSelectStatus(1);
                }
            }
        } else {
            controlArr[72] = 0;
            for (int i = 0; i < 16; i++) data2.get(i).setSelectStatus(0);
        }

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);

        short[] data = new short[2];
        data[0] = (short) (simpleValue.getControlId() - 1);

        if (simpleValue.isEnabled()) {
            data[1] = (short) (simpleValue.getControlId());
            writeQueueCache.setQueueCacheShortArr(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XA8E6", new short[]{(short) data[1]});
        } else {
            short[] value = writeQueueCache.getQueueCacheShortArr(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XA8E6");
            if (value != null && value.length > 0) {
            }
        }

        map.put("data1", data1);
        map.put("data2", data2);
        writeQueueCache.setQueueCache(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), simpleControlAddrArr[simpleValue.getControlId() - 1], map);

        Map<String, Integer> m = new HashMap<>();
        m.put("data3", simpleValue.isEnabled() ? simpleValue.getControlId() : 0);
        if (Objects.equals(m.getOrDefault("data3", 0), simpleValue.getControlId()))
            writeQueueCache.setQueueCache(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XA80AEnabled", m);

        return R.ok("指令已下发");
    }

    public R<?> updateAcControl(AcValue acValue) {
        Integer deviceId = acValue.getData().get(0).getDeviceId();

        HashMap<String, Object> map = (HashMap<String, Object>) key.getRemote(deviceId, "0XAF1E");
        if (map == null) {
            map = new HashMap<>();
            DevDeviceRemoteRead remoteRead = new DevDeviceRemoteRead();
            remoteRead.setId(deviceId);
            remoteRead.setDeviceId(deviceId);
            remoteRead.setAcSwitchNum(0);
            map.put("acNum", remoteRead);
        }

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        map.put("controlAc", acValue.getData());

        mqttPublisher.publish(deviceId, PublishKey.交流开关, acValue);

        writeQueueCache.setQueueCache(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XAF1E", map);

        return R.ok("指令下发成功");
    }

    public R<?> updateAcDc(Integer deviceId, java.math.BigDecimal value) {
        int val;
        String valueStr = value.toString();
        String[] split = valueStr.split("\\.");
        val = Integer.parseInt(split[0] + split[1]);

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        short[] saveArr = key.getRemoteByArr(deviceId, "0xA007");
        if (saveArr[0] == -1) {
            saveArr = new short[]{(short) val, 360};
        } else {
            saveArr[0] = (short) val;
        }
        writeQueueCache.setQueueCacheShortArr(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0xA007", saveArr);

        String dcDc = saveArr[1] + "";
        String dcDcValue = dcDc.substring(0, dcDc.length() - 1) + "." + dcDc.substring(dcDc.length() - 1);
        String acDcValue = val + "";
        if (acDcValue.length() > 1) {
            acDcValue = acDcValue.substring(0, acDcValue.length() - 1) + "." + acDcValue.substring(acDcValue.length() - 1);
        }

        ModuleValue v = new ModuleValue();
        v.setAcdc(acDcValue);
        v.setDcdc(dcDcValue);
        mqttPublisher.publish(deviceId, PublishKey.模块输出设置, v);
        return R.ok("指令下发成功");
    }

    public R<?> updateDcDc(Integer deviceId, java.math.BigDecimal value) {
        int val;
        String valueStr = value.toString();
        String[] split = valueStr.split("\\.");
        val = Integer.parseInt(split[0] + split[1]);

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        short[] saveArr = key.getRemoteByArr(deviceId, "0xA007");
        if (saveArr[0] == -1) {
            saveArr = new short[]{220, (short) val};
        } else {
            saveArr[1] = (short) val;
        }
        writeQueueCache.setQueueCacheShortArr(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0xA007", saveArr);

        String acDc = saveArr[0] + "";
        String acDcValue = acDc.substring(0, acDc.length() - 1) + "." + acDc.substring(acDc.length() - 1);
        String dcDcValue = val + "";
        if (dcDcValue.length() > 1) {
            dcDcValue = dcDcValue.substring(0, dcDcValue.length() - 1) + "." + dcDcValue.substring(dcDcValue.length() - 1);
        }

        ModuleValue v = new ModuleValue();
        v.setAcdc(acDcValue);
        v.setDcdc(dcDcValue);
        mqttPublisher.publish(deviceId, PublishKey.模块输出设置, v);
        return R.ok("指令下发成功");
    }

    public R<?> loopControlLux(Integer deviceId, LoopControlVO loopControlVO) {
        mqttPublisher.publish(deviceId, PublishKey.回路总开关, loopControlVO);
        writeQueueCache.setLoopSelect(deviceId, loopControlVO.getData().getLoopArr());
        return R.ok("操作成功");
    }

    public R<?> loopControlSwitch(Integer deviceId, LoopControlVO loopControlVO) {
        mqttPublisher.publish(deviceId, PublishKey.回路总开关, loopControlVO);
        writeQueueCache.setLoopSelect(deviceId, loopControlVO.getData().getLoopArr());
        return R.ok("指令下发成功");
    }
}
