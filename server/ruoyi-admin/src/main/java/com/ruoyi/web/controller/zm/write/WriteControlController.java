package com.ruoyi.web.controller.zm.write;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ruoyi.cache.Key;
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
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevBaseDistrictMapper;
import com.ruoyi.zm.utils.IdGenerator;
import com.ruoyi.zm.utils.ScaleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.ruoyi.cache.Key.REMOTE_KEY;
import static com.ruoyi.schedule.InstructionQueue.QUEUE_WRITE_KEY;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/write/control")
public class WriteControlController {

    private final DevBaseDeviceMapper deviceMapper;
    private final DevBaseDistrictMapper districtMapper;
    private final MqttPublisher mqttPublisher;
    private final Key key;
    private final Key k;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, int[]> intArrayRedisTemplate;
    private final RedisTemplate<String, short[]> shortArrayRedisTemplate;
    private final RecordPlus record;
    private final DListUtil dListUtil;
    private final ZoneUtilCache zoneUtilCache;

    public static final String[] simpleControlAddrArr = {"0XA80A", "0XA853", "0XA89C"};
    private final static String[] ADDR_SIMPLE = {"", "0XA80A", "0XA853", "0XA89C"};

    @PostMapping("/updateSimpleControl")
    public R<?> updateSimpleControl(@RequestParam Integer deviceId, @RequestBody SimpleValue simpleValue) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");

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
            shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XA8E6", new short[]{(short) data[1]});
        } else {
            short[] value = shortArrayRedisTemplate.opsForValue().get("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XA8E6");
            if (value != null && value.length > 0) {
            }
        }

        map.put("data1", data1);
        map.put("data2", data2);
        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":" + simpleControlAddrArr[simpleValue.getControlId() - 1], map);

        Map<String, Integer> m = new HashMap<>();
        m.put("data3", simpleValue.isEnabled() ? simpleValue.getControlId() : 0);
        if (Objects.equals(m.getOrDefault("data3", 0), simpleValue.getControlId()))
            redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XA80AEnabled", m);

        return R.ok("指令已下发");
    }

    @PostMapping("/updateSimpleControlToAll")
    public R<?> updateSimpleControlToAll(@RequestBody SimpleValue simpleValue) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");

        for (int i = 0; i < simpleValue.getData1().size(); i++) {
            if (simpleValue.getData1().get(i).getLux() < 0 || simpleValue.getData1().get(i).getLux() > 100) {
                return R.warn("普通时控亮度上下限值 0-100");
            }
        }

        List<DevBaseDevice> devices = deviceMapper.selectList();
        for (DevBaseDevice device : devices) {
            try {
                updateSimpleControlCommon(simpleValue, Math.toIntExact(device.getDeviceNo()));
            } catch (Exception e) {
                log.error("Error in updateSimpleControlToAll for device: {}", device.getDeviceNo(), e);
            }
        }
        return R.ok("指令已下发");
    }

    @GetMapping("/updateZoneLightSwitch")
    public R<?> updateZoneLightSwitch(@RequestParam String type, @RequestParam Integer value) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        Integer zoneId = Integer.parseInt(type);
        Integer swStatus = (value == 1 ? 0 : 1);
        return updateZoneLightSwitchInternal(zoneId, swStatus);
    }

    private R<?> updateZoneLightSwitchInternal(Integer zoneId, Integer swStatus) {
        record.runModule();

        DevBaseDistrict assist = new DevBaseDistrict();
        assist.setOrderNo(zoneId);
        assist.setSwitchStatus(swStatus == 1 ? 1 : 0);
        districtMapper.insertOrUpdate(assist);

        assist = districtMapper.selectById(zoneId);
        zoneId = Math.toIntExact(assist.getId());

        List<Long> devBaseDevices = dListUtil.Nos();
        if (devBaseDevices != null && !devBaseDevices.isEmpty()) {
            for (Long no : devBaseDevices) {
                try {
                    List<Integer> groups = zoneUtilCache.getGroupByZone(Math.toIntExact(no), java.util.Arrays.asList(zoneId));

                    if (k.getTelecommand(Math.toIntExact(no), 164) == 0)
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

    @GetMapping("/updateZoneLightLux")
    public R<?> updateZoneLightLux(@RequestParam String type, @RequestParam Integer value) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        Integer zoneId = Integer.parseInt(type);
        return updateZoneLightLuxInternal(zoneId, value);
    }

    private R<?> updateZoneLightLuxInternal(Integer zoneId, Integer lux) {
        if (lux < 0 || lux > 100) {
            return R.fail("亮度值设定范围 0~100");
        }
        record.runModule();

        DevBaseDistrict assist = new DevBaseDistrict();
        assist.setOrderNo(zoneId);
        assist.setLux(lux);
        districtMapper.insertOrUpdate(assist);

        assist = districtMapper.selectById(zoneId);
        zoneId = Math.toIntExact(assist.getId());

        List<Long> devBaseDevices = dListUtil.Nos();
        if (devBaseDevices != null && !devBaseDevices.isEmpty()) {
            for (Long no : devBaseDevices) {
                try {
                    List<Integer> groups = zoneUtilCache.getGroupByZone(Math.toIntExact(no), java.util.Arrays.asList(zoneId));

                    if (k.getTelecommand(Math.toIntExact(no), 164) == 0)
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

    @PostMapping("/updateAcControl")
    public R<?> updateAcControl(@RequestBody AcValue acValue) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");

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

        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XAF1E", map);

        return R.ok("指令下发成功");
    }

    @GetMapping("/updateAcDc")
    public R<?> updateAcDc(@RequestParam Integer deviceId, @RequestParam int acdc) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        short[] saveArr = key.getRemoteByArr(deviceId, "0xA007");
        if (saveArr[0] == -1) {
            saveArr = new short[]{(short) acdc, 360};
        } else {
            saveArr[0] = (short) acdc;
        }
        shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0xA007", saveArr);

        String dcDc = saveArr[1] + "";
        String dcDcValue = dcDc.substring(0, dcDc.length() - 1) + "." + dcDc.substring(dcDc.length() - 1);
        String acDcValue = acdc + "";
        if (acDcValue.length() > 1) {
            acDcValue = acDcValue.substring(0, acDcValue.length() - 1) + "." + acDcValue.substring(acDcValue.length() - 1);
        }

        ModuleValue v = new ModuleValue();
        v.setAcdc(acDcValue);
        v.setDcdc(dcDcValue);
        mqttPublisher.publish(deviceId, PublishKey.模块输出设置, v);
        return R.ok("指令下发成功");
    }

    @GetMapping("/updateDcDc")
    public R<?> updateDcDc(@RequestParam Integer deviceId, @RequestParam int acdc) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        short[] saveArr = key.getRemoteByArr(deviceId, "0xA007");
        if (saveArr[0] == -1) {
            saveArr = new short[]{220, (short) acdc};
        } else {
            saveArr[1] = (short) acdc;
        }
        shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0xA007", saveArr);

        String acDc = saveArr[0] + "";
        String acDcValue = acDc.substring(0, acDc.length() - 1) + "." + acDc.substring(acDc.length() - 1);
        String dcDcValue = acdc + "";
        if (dcDcValue.length() > 1) {
            dcDcValue = dcDcValue.substring(0, dcDcValue.length() - 1) + "." + dcDcValue.substring(dcDcValue.length() - 1);
        }

        ModuleValue v = new ModuleValue();
        v.setAcdc(acDcValue);
        v.setDcdc(dcDcValue);
        mqttPublisher.publish(deviceId, PublishKey.模块输出设置, v);
        return R.ok("指令下发成功");
    }

    @PostMapping("/loopControlLux")
    public R<?> loopControlLux(@RequestParam Integer deviceId, @RequestBody LoopControlVO loopControlVO) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");

        mqttPublisher.publish(deviceId, PublishKey.回路总开关, loopControlVO);

        intArrayRedisTemplate.opsForValue().set("zm:select:loop:" + deviceId, loopControlVO.getData().getLoopArr());

        return R.ok("操作成功");
    }

    @PostMapping("/loopControlSwitch")
    public R<?> loopControlSwitch(@RequestParam Integer deviceId, @RequestBody LoopControlVO loopControlVO) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");

        mqttPublisher.publish(deviceId, PublishKey.回路总开关, loopControlVO);

        intArrayRedisTemplate.opsForValue().set("zm:select:loop:" + deviceId, loopControlVO.getData().getLoopArr());

        return R.ok("指令下发成功");
    }

    private R<?> workModule(Integer deviceId, Integer workModule) {
        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        m.put("addr", "0xC001");
        m.put("data", workModule);
        mqttPublisher.publish(deviceId, PublishKey.工作模式, m);
        redisTemplate.opsForHash().put("zm:workModule", String.valueOf(deviceId), workModule);
        return R.ok("指令下发成功");
    }

    private R<?> selectHandModule(Integer deviceId, Integer handModule) {
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        if (handModule == 1) remote.setHandModule("loop");
        else if (handModule == 2) remote.setHandModule("group");
        else if (handModule == 3) remote.setHandModule("scene");
        redisTemplate.opsForValue().set(REMOTE_KEY + key.getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + "0XB715", remote);
        ModuleSelect v = new ModuleSelect();
        v.setData(Collections.singletonList(remote));
        mqttPublisher.publish(deviceId, PublishKey.模式选择, v);

        return R.ok("指令已下发");
    }

    private R<?> groupControlSwitch(WriteGroupReqVo reqVo) {
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

        intArrayRedisTemplate.opsForValue().set("zm:select:group:" + reqVo.getDeviceId(), groupSelectArr);

        return R.ok("指令下发成功");
    }

    private R<?> groupControlLux(WriteGroupReqVo reqVo) {
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

        intArrayRedisTemplate.opsForValue().set("zm:select:group:" + reqVo.getDeviceId(), groupSelectArr);

        return R.ok("操作成功");
    }

    private void updateSimpleControlCommon(SimpleValue simpleValue, Integer deviceId) {
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
        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":" + simpleControlAddrArr[simpleValue.getControlId() - 1], map);

        DevInstruct instruct = new DevInstruct();
        instruct.setIp(tcpVo.getIp());
        instruct.setSalveId(deviceId);
        instruct.setFeedback(2);

        instruct.setCode(6);
        instruct.setId(IdGenerator.UUIDId());
        instruct.setAddr(ADDR_SIMPLE[simpleValue.getControlId()]);
        instruct.setAddrNum(73);
        instruct.setWriteValue(new JSONObject().set("arr", controlArr).toString());
        redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + deviceId, instruct);

        if (simpleValue.isEnabled()) {
            data[1] = (short) (simpleValue.getControlId());
            shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XA8E6", new short[]{(short) data[1]});

            instruct.setId(IdGenerator.UUIDId());
            instruct.setAddr("0XA8E5");
            instruct.setAddrNum(1);
            instruct.setWriteValue(new JSONObject().set("arr", new short[]{data[0]}).toString());
            redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + deviceId, instruct);

            instruct.setId(IdGenerator.UUIDId());
            instruct.setAddr("0XA8E6");
            instruct.setAddrNum(1);
            instruct.setWriteValue(new JSONObject().set("arr", new short[]{data[1]}).toString());
            redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + deviceId, instruct);
        } else {
            short[] value = shortArrayRedisTemplate.opsForValue().get("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XA8E6");

            if (value != null && value.length > 0) {
                instruct.setId(IdGenerator.UUIDId());
                instruct.setAddr("0XA8E5");
                instruct.setAddrNum(1);
                instruct.setWriteValue(new JSONObject().set("arr", new short[]{(short) (simpleValue.getControlId() - 1)}).toString());
                redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + deviceId, instruct);

                instruct.setId(IdGenerator.UUIDId());
                instruct.setAddr("0XA8E6");
                instruct.setAddrNum(1);
                instruct.setWriteValue(new JSONObject().set("arr", new short[]{value[0]}).toString());
                redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + deviceId, instruct);
            }
        }

        short[] saveArr = {1};

        instruct.setCode(5);
        instruct.setId(IdGenerator.UUIDId());
        instruct.setAddr("0xC2C9");
        instruct.setAddrNum(1);
        instruct.setWriteValue(new JSONObject().set("arr", saveArr).toString());
        redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + deviceId, instruct);
    }

    private Boolean module() {
        String module = (String) redisTemplate.opsForValue().get("zm:global:module:select");
        return "on-the-line".equals(module);
    }

    private Integer[] convertFromData2(Integer[] data2) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < data2.length; i++) {
            if (data2[i] == 1) result.add(i + 1);
        }
        return result.toArray(new Integer[0]);
    }
}
