package com.ruoyi.web.controller.zm;


import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ruoyi.cache.*;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.modbus.util.RecordPlus;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.mqtt03.addr03.handler.addr0XB715.Addr0XB715;
import com.ruoyi.mqtt03.addr03.handler.addr0xA483.Addr0xA483;
import com.ruoyi.mqtt03.addr03.handler.addr0xA483.Addr0xA483Handler;
import com.ruoyi.mqttwrite.accontrol.AcValue;
import com.ruoyi.mqttwrite.common.CommonDataInt;
import com.ruoyi.mqttwrite.common.CommonDataString;
import com.ruoyi.mqttwrite.ill.IllValue;
import com.ruoyi.mqttwrite.inf.InfValue;
import com.ruoyi.mqttwrite.loopcontrol.LoopControlVO;
import com.ruoyi.mqttwrite.module.ModuleSelect;
import com.ruoyi.mqttwrite.module.ModuleValue;
import com.ruoyi.mqttwrite.scene.SceneParams;
import com.ruoyi.mqttwrite.scene.SceneValue;
import com.ruoyi.mqttwrite.simple.SimpleValue;
import com.ruoyi.mqttwrite.time.TimeVO;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

import static com.ruoyi.cache.Key.REMOTE_KEY;
import static com.ruoyi.schedule.InstructionQueue.QUEUE_WRITE_KEY;
import static com.ruoyi.zm.utils.ScaleUtil.combineIDs;

/**
 * @deprecated 已拆分为 5 个独立 Controller，参见 write/ 子包:
 *   WriteGroupController, WriteSceneController, WriteDeviceController,
 *   WriteControlController, WriteModuleController
 *   旧端点保留兼容，新功能请使用拆分后的路径。
 */
@Deprecated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/write")
public class WriteController {
    private final DevBaseDeviceMapper deviceMapper;
    private final RedisTemplate<String, int[]> intArrayRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    public static final String[] simpleControlAddrArr = {"0XA80A", "0XA853", "0XA89C"};
    private final RedisTemplate<String, short[]> shortArrayRedisTemplate;
    public static final Integer[] DELETE_LOOP_NOS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33,
        34, 35, 36, 37, 38, 39, 40};
    private final Key key;
    private final DevBaseDistrictMapper districtMapper;
    private final static String[] ADDR_SIMPLE = {"", "0XA80A", "0XA853", "0XA89C"};
    private final ZoneUtilCache zoneUtilCache;
    private final RecordPlus record;
    private final DListUtil dListUtil;
    private final MqttPublisher mqttPublisher;
    private final StringRedisTemplate stringRedisTemplate;
    private final Key k;

    // 改分组名称 已保存缓存
    @PostMapping("/groupName")
    public R<?> groupName1(@RequestBody GroupNameVo groupNameVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return groupName(groupNameVo);
    }

    public R<?> groupName(@RequestBody GroupNameVo groupNameVo) {
        CommonDataString d = new CommonDataString();
        d.setAddr("0xA1F0");
        CommonDataString.D d1 = new CommonDataString.D();
        d1.setId(groupNameVo.getGroupId());
        d1.setValue(groupNameVo.getName());
        d.setData(d1);
        mqttPublisher.publish(groupNameVo.getDeviceId(), PublishKey.分组命名, d);

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(groupNameVo.getDeviceId());

        List<String> names = (List<String>) redisTemplate.opsForValue().get(AddrHandlerFactory.getKey(tcpVo.getIp(), "0xA1F0", groupNameVo.getDeviceId()));
        if (names == null || names.isEmpty()) {
            names = new ArrayList<>();
            for (int i = 0; i < 16; i++) names.add("分组" + (i + 1));
            if (groupNameVo.getGroupId() <= 16) {
                if (names.size() > groupNameVo.getGroupId() - 1) {
                    names.set(groupNameVo.getGroupId() - 1, groupNameVo.getName());
                } else names.add(groupNameVo.getName());
            }
        } else {
            if (groupNameVo.getGroupId() > 0 && groupNameVo.getGroupId() <= names.size()) {
                names.set(groupNameVo.getGroupId() - 1, groupNameVo.getName());
            } else {
                while (names.size() < groupNameVo.getGroupId())
                    names.add("分组" + (names.size() + 1));
                names.add(groupNameVo.getName());
            }
        }
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(tcpVo.getIp(), "0xA1F0", groupNameVo.getDeviceId()), names);

        DevBaseDistrict devBaseDistrict = districtMapper.selectById(groupNameVo.getId());
        short[] codes = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        try {
            short[] temp = key.getRemoteByArr(groupNameVo.getDeviceId(), "0xA5AE");
            if (temp[0] != -1) {
                codes = temp;
            }
            codes[groupNameVo.getGroupId() - 1] = (short) ((long) devBaseDistrict.getId());
        } catch (Exception e) {
            log.error("Error reading remote array for groupName", e);
        }
        shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0xA5AE", codes);

        return R.ok("指令已下发");
    }

    // 机柜设置-回路分组-保存接口 已保存缓存
    @PostMapping("/updateLoop")
    public synchronized R<?> updateLoop1(@RequestBody UpdateLoopReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateLoop(reqVo);
    }

    public R<?> updateLoop(@RequestBody UpdateLoopReqVo reqVo) {
        String groupId;
        Integer deviceId = reqVo.getDeviceId();
        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        if (reqVo.getGroupId() != null) {
            int[] ints = new int[40];
            Map<String, Object> m = new HashMap<>();
            m.put("groupId", reqVo.getGroupId());
            m.put("loopNo", ints);
            if (reqVo.getGroupId() < 10) groupId = "0" + reqVo.getGroupId();
            else groupId = "" + reqVo.getGroupId();
            if (reqVo.getLoopNo() != null && reqVo.getLoopNo().length > 0) {
                Integer[] loopNos = reqVo.getLoopNo();
                String groupStr = combineIDs(groupId, loopNos);
                StringBuilder resultGroupStr = new StringBuilder();
                for (String s : ScaleUtil.splitIntoPairs(groupStr.substring(2))) {
                    if (!"00".equals(s)) resultGroupStr.append(s);
                }
                ArrayList<Integer> integers = new ArrayList<>(java.util.Arrays.asList(loopNos));
                List<Integer> l = new ArrayList<>();
                for (int i = 1; i < 41; i++) {
                    if (integers.contains(i)) {
                        l.add(1);
                    } else l.add(0);
                }
                m.put("loopNo", l);
                redisTemplate.opsForValue().set("zm:queue:zm:cache:3:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":" + LoopByGroupCache.LOOP_NUMS_ADDR_ARR[reqVo.getGroupId() - 1], resultGroupStr.toString());
            } else
                redisTemplate.opsForValue().set("zm:queue:zm:cache:3:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":" + LoopByGroupCache.LOOP_NUMS_ADDR_ARR[reqVo.getGroupId() - 1], "");
            mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.保存回路, m);

            List<Long> loops = (List<Long>) redisTemplate.opsForValue().get(AddrHandlerFactory.getKey(tcpVo.getIp(), "0x23F5num", reqVo.getDeviceId()));
            if (loops == null || loops.isEmpty()) {
                loops = new ArrayList<>();
                for (int i = 0; i < 16; i++) loops.add(0L);
                if (reqVo.getGroupId() <= 16) {
                    if (loops.size() > reqVo.getGroupId() - 1) {
                        loops.set(reqVo.getGroupId() - 1, (long) reqVo.getLoopNo().length);
                    } else loops.add(0L);
                }
            } else {
                if (reqVo.getGroupId() > 0 && reqVo.getGroupId() <= loops.size()) {
                    loops.set(reqVo.getGroupId() - 1, (long) reqVo.getLoopNo().length);
                } else {
                    while (loops.size() < reqVo.getGroupId())
                        loops.add((long) reqVo.getLoopNo().length);
                    loops.add((long) reqVo.getLoopNo().length);
                }
            }
            redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(tcpVo.getIp(), "0x23F5num", reqVo.getDeviceId()), loops);

            if (reqVo.getZoneId() != null) {
                CommonDataInt data = new CommonDataInt();
                data.setAddr("0xA5AE");
                CommonDataInt.D d = new CommonDataInt.D();
                d.setId(reqVo.getGroupId());
                d.setValue(reqVo.getZoneId());
                data.setData(d);
                mqttPublisher.publish(deviceId, PublishKey.分区编号, data);
                short[] codes = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
                try {
                    short[] temp = key.getRemoteByArr(reqVo.getDeviceId(), "0xA5AE");
                    if (temp[0] != -1) codes = temp;
                    codes[reqVo.getGroupId() - 1] = reqVo.getZoneId();
                } catch (Exception e) {
                    log.error("Error reading remote array for updateLoop", e);
                }
                shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0xA5AE", codes);
            }
        }
        return R.ok("指令已下发");
    }

    private static final String[] SCENE_PARAMS_ARR = {"", "0xAFC2", "0xAFE3", "0xB004",
        "0xB025", "0xB046", "0xB067", "0xB088", "0xB0A9", "0xB0CA", "0xB0EB"
    };

    // 修改照明控制-分区控制 开关 已保存缓存
    @GetMapping("/updateZoneLightSwitch")
    public R<?> updateZoneLightSwitch1(Integer zoneId, Integer swStatus) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        swStatus = (swStatus == 1 ? 0 : 1);
        return updateZoneLightSwitch(zoneId, swStatus);
    }


    public R<?> updateZoneLightSwitch(Integer zoneId, Integer swStatus) {
        // 接收到指令前，主机应先记录所有机柜当前手、自动状态
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


                    // 把当前自动状态下的机柜，切换到手动状态，并手动控制该机柜进入分组控制
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

    // 修改照明控制-分区控制 调光 亮度值设定范围 0~100 已保存缓存
    @GetMapping("/updateZoneLightLux")
    public R<?> updateZoneLightLux1(Integer zoneId, Integer lux) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateZoneLightLux(zoneId, lux);
    }

    public R<?> updateZoneLightLux(Integer zoneId, Integer lux) {
        if (lux < 0 || lux > 100) {
            return R.fail("亮度值设定范围 0~100");
        }
        // 接收到指令前，主机应先记录所有机柜当前手、自动状态
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


                    // 把当前自动状态下的机柜，切换到手动状态，并手动控制该机柜进入分组控制
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

    // 照明状态&控制-照明控制-场景控制/进入场景（所有机柜） 已保存缓存
    @GetMapping("/intoScenes")
    public R<?> intoScenes1(Integer sceneId) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return intoScenes(sceneId);
    }

    public R<?> intoScenes(Integer sceneId) {
        // 接收到指令前，主机应先记录所有机柜当前手、自动状态
        record.runModule();
        List<DevBaseDevice> devBaseDevices = deviceMapper.selectList(new LambdaQueryWrapper<DevBaseDevice>().select(DevBaseDevice::getDeviceNo));
        if (devBaseDevices != null && !devBaseDevices.isEmpty()) {
            devBaseDevices.forEach(item -> intoScene(Math.toIntExact(item.getDeviceNo()), sceneId));
        }
        redisTemplate.opsForValue().set("zm:global:scene:select", sceneId + "");
        // redisTemplate.opsForHash().put(RecordUtil.RECOVER_KEY, "time", recordUtil.recordSceneModule());
        return R.ok("操作成功");
    }

    // 直流机柜-机柜控制-照明控制-场景控制/进入场景（单个机柜） 已保存缓存
    @GetMapping("/intoScene")
    public R<?> intoScene1(Integer deviceId, Integer sceneId) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return intoSceneNotRecord(deviceId, sceneId);
    }

    public R<?> intoSceneNotRecord(Integer deviceId, Integer sceneId) {
        LambdaQueryWrapper<DevBaseDevice> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevBaseDevice::getDeviceNo, deviceId).last("LIMIT 1");
        DevBaseDevice device = deviceMapper.selectOne(lqw);
        device.setId(device.getId());
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        m.put("addr", "0xC1B9");
        Map<String, Integer> d = new LinkedHashMap<>();
        d.put("no", sceneId);
        d.put("value", 1);
        m.put("data", d);
        mqttPublisher.publish(deviceId, PublishKey.场景控制, m);
        device.setSceneSelect(sceneId);
        deviceMapper.updateById(device);
        return R.ok("操作成功");
    }

    public R<?> intoScene(Integer deviceId, Integer sceneId) {
        LambdaQueryWrapper<DevBaseDevice> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevBaseDevice::getDeviceNo, deviceId).last("LIMIT 1");
        DevBaseDevice device = deviceMapper.selectOne(lqw);
        device.setId(device.getId());
        // 把当前自动状态下的机柜，切换到手动状态，并手动场景控制该机柜进入场景
        if (k.getTelecommand(deviceId, 164) == 0)
            workModule(deviceId, 1);
        selectHandModule(deviceId, 3);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        m.put("addr", "0xC1B9");
        Map<String, Integer> d = new LinkedHashMap<>();
        d.put("no", sceneId);
        d.put("value", 1);
        m.put("data", d);
        mqttPublisher.publish(deviceId, PublishKey.场景控制, m);
        device.setSceneSelect(sceneId);
        deviceMapper.updateById(device);
        return R.ok("操作成功");
    }

    // 机柜设置-场景设置-场景参数 保存接口 已保存缓存
    @PostMapping("/updateSceneParams")
    public R<?> updateSceneParams1(@RequestBody SceneParamsReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateSceneParams(reqVo);
    }

    public R<?> updateSceneParams(@RequestBody SceneParamsReqVo reqVo) {
        for (int i = 0; i < reqVo.getSelectLuxArr().length; i++) {
            if (reqVo.getSelectLuxArr()[i] < 0 || reqVo.getSelectLuxArr()[i] > 100)
                return R.warn("场景参数亮度上下限值 0-100");
        }
        SceneParams s = new SceneParams();
        SceneParams.D d = new SceneParams.D();

        Integer[] g = SceneParams.getG(reqVo.getSelectGroupIdArr());
        d.setGroupIds(g);
        d.setLuxArr(reqVo.getSelectLuxArr());
        d.setSceneId(reqVo.getSceneId());
        Boolean[] b = SceneParams.getB(reqVo.getSelectSwitchArr());
        d.setSwitchArr(b);
        s.setData(Collections.singletonList(d));

        Addr0xA483.Data d1 = new Addr0xA483.Data();
        d1.setSceneId(reqVo.getSceneId());
        d1.setSwitchArr(b);
        d1.setLuxArr(reqVo.getSelectLuxArr());
        d1.setGroupIds(g);
        redisTemplate.opsForHash().put(Addr0xA483Handler.writeKey + reqVo.getDeviceId(), reqVo.getSceneId() + "m", d1);

        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.场景设置, s);

        // System.out.println(java.util.Arrays.toString(reqVo.getSelectGroupIdArr()));
        Integer[] data = new Integer[33];
        // 亮度
        if (reqVo.getSelectLuxArr() != null && reqVo.getSelectLuxArr().length > 0) {
            System.arraycopy(reqVo.getSelectLuxArr(), 0, data, 0, 16);
        }
        // 开关
        if (reqVo.getSelectSwitchArr() != null && reqVo.getSelectSwitchArr().length > 0) {
            System.arraycopy(reqVo.getSelectSwitchArr(), 0, data, 16, 16);
        }
        // 选中分组情况
        if (reqVo.getSelectGroupIdArr().length > 0) {
            int lastValue = ScaleUtil.convertGroupsToRegisterValue(reqVo.getSelectGroupIdArr());
            data[data.length - 1] = lastValue;
        } else {
            data[data.length - 1] = 0;
        }

        // 存缓存
        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(reqVo.getDeviceId());
        List<DevConfigScene> scenes = (List<DevConfigScene>) key.getRemote(reqVo.getDeviceId(), SCENE_PARAMS_ARR[reqVo.getSceneId()]);
        if (scenes == null) {
            scenes = new LinkedList<>();
            for (int i = 0; i < 16; i++) {
                DevConfigScene configScene = new DevConfigScene();
                configScene.setSceneId(Long.valueOf(reqVo.getSceneId()));
                configScene.setGroupId((long) (i + 1));
                configScene.setDeviceId(Long.valueOf(reqVo.getDeviceId()));
                scenes.add(configScene);
            }
        }
        for (int i = 0; i < scenes.size(); i++) {
            scenes.get(i).setLux(Long.valueOf(reqVo.getSelectLuxArr()[i]));
        }
        for (int i = 0; i < scenes.size(); i++) {
            scenes.get(i).setBtnStatus(reqVo.getSelectSwitchArr()[i]);
        }
        for (int i = 0; i < 16; i++) {
            scenes.get(i).setSelectStatus(0);
        }
        for (int i = 0; i < scenes.size(); i++) {
            for (int j = 0; j < reqVo.getSelectGroupIdArr().length; j++) {
                if (i + 1 == reqVo.getSelectGroupIdArr()[j]) {
                    scenes.get(i).setSelectStatus(1);
                }
            }
        }
        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":" + SCENE_PARAMS_ARR[reqVo.getSceneId()], scenes);

        return R.ok("指令已下发");
    }

    // 机柜设置-时控模式-普通时控模式参数-保存接口 已保存缓存
    @PostMapping("/updateSimpleControl")
    public R<?> updateSimpleControl1(@RequestBody SimpleControlReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateSimpleControl(reqVo);
    }

    public R<?> updateSimpleControl(@RequestBody SimpleControlReqVo reqVo) {
        SimpleValue v = new SimpleValue();
        v.setControlId(reqVo.getControlId());
        v.setEnabled(reqVo.getEnabled());
        v.setData1(reqVo.getTable());
        v.setData2(SimpleValue.selectArr(reqVo.getGroupIds()));
        //        String json = new Gson().toJson(v);
        //        System.out.println(json);
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.普通时控, v);

        for (int i = 0; i < reqVo.getTable().size(); i++) {
            if (reqVo.getTable().get(i).getLux() < 0 || reqVo.getTable().get(i).getLux() > 100)
                return R.warn("普通时控亮度上下限值 0-100");
        }
        int[] controlArr = new int[73];

        Map<String, Object> map = (HashMap<String, Object>) key.getRemote(reqVo.getDeviceId(), simpleControlAddrArr[reqVo.getControlId() - 1]);
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
                control.setTimeControlId(reqVo.getControlId());
                control.setDeviceId(Long.valueOf(reqVo.getDeviceId()));
                control.setTimeFrameId(i + 1);
                data1.add(control);
            }
        }

        if (data2 == null) {
            data2 = new LinkedList<>();
            for (int i = 0; i < 16; i++) {
                DevConfigSimpleGroup group = new DevConfigSimpleGroup();
                group.setSimpleId(reqVo.getControlId());
                group.setDeviceId(reqVo.getDeviceId());
                group.setGroupId(i + 1);
                group.setSelectStatus(0);
                data2.add(group);
            }
        }

        // 处理时序表 06
        List<SimpleControlTable> table = reqVo.getTable();
        for (int i = 0; i < controlArr.length - 1; i += 9) {
            // 亮度
            controlArr[i] = table.get((i / 9)).getLux();
            data1.get(i / 9).setLux(table.get((i / 9)).getLux());
            // 开关
            controlArr[i + 1] = table.get((i / 9)).getSwitchStatus();
            data1.get(i / 9).setSwitchStatus(table.get((i / 9)).getSwitchStatus());
            // 使能
            controlArr[i + 2] = table.get((i / 9)).getEnabledStatus();
            data1.get(i / 9).setEnabledStatus(table.get((i / 9)).getEnabledStatus());
            // 开始时间01~03
            int[] sTime = ScaleUtil.gbkToArr(table.get((i / 9)).getStime());
            controlArr[i + 3] = sTime[0];
            controlArr[i + 4] = sTime[1];
            controlArr[i + 5] = sTime[2];
            data1.get(i / 9).setStime(table.get((i / 9)).getStime());
            // 结束时间01~03
            int[] eTime = ScaleUtil.gbkToArr(table.get((i / 9)).getEtime());
            controlArr[i + 6] = eTime[0];
            controlArr[i + 7] = eTime[1];
            controlArr[i + 8] = eTime[2];
            data1.get(i / 9).setEtime(table.get((i / 9)).getEtime());
        }
        // 处理分组情况 06
        if (!Arrays.isNullOrEmpty(reqVo.getGroupIds())) {
            controlArr[72] = ScaleUtil.convertGroupsToRegisterValue(reqVo.getGroupIds());
            for (int i = 0; i < 16; i++) data2.get(i).setSelectStatus(0);
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < reqVo.getGroupIds().length; j++) {
                    if (i + 1 == reqVo.getGroupIds()[j])
                        data2.get(i).setSelectStatus(1);
                }
            }
        } else {
            controlArr[72] = 0;
            for (int i = 0; i < 16; i++) data2.get(i).setSelectStatus(0);
        }
        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(reqVo.getDeviceId());

        // 处理启用 06
        short[] data = new short[2];
        data[0] = (short) (reqVo.getControlId() - 1);

        // 处理普通时控保存标志位 05
        short[] saveArr = {1};
        //        // send.writeToAddr(reqVo.getDeviceId(), 6, "系统设置-控制方式-普通模式-时控" + reqVo.getControlId() + "-时段信息1-亮度", 73, controlArr);
        // // send.writeToAddr(reqVo.getDeviceId(), 6, "系统设置-控制方式-普通模式-时控应用", 2, data);
        if (reqVo.getEnabled() == true) {
            data[1] = (short) ((int) reqVo.getControlId());
            shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XA8E6", new short[]{(short) data[1]});
            //            // send.writeToAddr(reqVo.getDeviceId(), 6, "系统设置-控制方式-普通模式-时控应用 ", 1, new short[]{data[0]});
            //            // send.writeToAddr(reqVo.getDeviceId(), 6, "系统设置-控制方式-普通模式-启用时控", 1, new short[]{data[1]});
        } else {
            short[] value = shortArrayRedisTemplate.opsForValue().get("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XA8E6");

            if (value != null && value.length > 0) {
                //                // send.writeToAddr(reqVo.getDeviceId(), 6, "系统设置-控制方式-普通模式-时控应用 ", 1, new short[]{(short) (reqVo.getControlId() - 1)});
                //                // send.writeToAddr(reqVo.getDeviceId(), 6, "系统设置-控制方式-普通模式-启用时控", 1, new short[]{value[0]});
            }
        }
        //        // send.writeToAddr(reqVo.getDeviceId(), 5, "普通时控保存标志位", 1, saveArr);

        //        log.info("{}", data1);
        map.put("data1", data1);
        map.put("data2", data2);
        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":" + simpleControlAddrArr[reqVo.getControlId() - 1], map);
        Map<String, Integer> m = new HashMap<>();
        m.put("data3", (reqVo.getEnabled() != null && reqVo.getEnabled()) ? reqVo.getControlId() : 0);
        if (Objects.equals(m.getOrDefault("data3", 0), reqVo.getControlId()))
            redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XA80AEnabled", m);
        return R.ok("指令已下发");
    }

    private final static String[] ADDR_SCENE = {"", "0XAAEE", "0XAB2E", "0XAB6E"};

    // 机柜设置-时控模式-场景时控模式参数-保存接口
    @PostMapping("/updateSceneControl")
    public R<?> updateSceneControl1(@RequestBody SceneControlReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateSceneControl(reqVo);
    }

    public R<?> updateSceneControl(@RequestBody SceneControlReqVo reqVo) {
        SceneValue v = new SceneValue();
        v.setControlId(reqVo.getControlId());
        v.setEnabled(reqVo.getEnabled());
        v.setData(reqVo.getTable());
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.场景时控, v);

        int[] controlArr = new int[64];

        List<DevConfigTimeControlScene> scenes = (List<DevConfigTimeControlScene>) key.getRemote(reqVo.getDeviceId(), SceneControlCache.SCENE_PARAMS_ADDR_ARR[reqVo.getControlId() - 1]);

        if (scenes == null) {
            scenes = new LinkedList<>();
            for (int i = 0; i < 8; i++) {
                DevConfigTimeControlScene scene = new DevConfigTimeControlScene();
                scene.setDeviceId(reqVo.getDeviceId());
                scene.setTimeControlId(reqVo.getControlId());
                scene.setTimeFrameId(i + 1);
                scenes.add(scene);
            }
        }

        // 处理时序表 06
        List<SceneControlTable> table = reqVo.getTable();
        for (int i = 0; i < controlArr.length - 1; i += 8) {
            // 使能
            controlArr[i] = table.get((i / 8)).getEnabledStatus();
            scenes.get(i / 8).setEnabledStatus(table.get((i / 8)).getEnabledStatus());
            // 场景选择
            controlArr[i + 1] = table.get((i / 8)).getSceneSelect();
            scenes.get(i / 8).setSceneSelect(table.get((i / 8)).getSceneSelect());
            // 开始时间01~03
            int[] sTime = ScaleUtil.gbkToArr(table.get((i / 8)).getStime());
            controlArr[i + 2] = sTime[0];
            controlArr[i + 3] = sTime[1];
            controlArr[i + 4] = sTime[2];
            scenes.get(i / 8).setStime(table.get((i / 8)).getStime());
            // 结束时间01~03
            int[] eTime = ScaleUtil.gbkToArr(table.get((i / 8)).getEtime());
            controlArr[i + 5] = eTime[0];
            controlArr[i + 6] = eTime[1];
            controlArr[i + 7] = eTime[2];
            scenes.get(i / 8).setEtime(table.get((i / 8)).getEtime());
        }

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(reqVo.getDeviceId());
        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":" + SceneControlCache.SCENE_PARAMS_ADDR_ARR[reqVo.getControlId() - 1], scenes);
        Map<String, Integer> m = new HashMap<>();
        m.put("data3", (reqVo.getEnabled() != null && reqVo.getEnabled()) ? reqVo.getControlId() : 0);
        if (Objects.equals(m.getOrDefault("data3", 0), reqVo.getControlId()))
            redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XAAEEEnabled", m);

        // 处理启用 06
        short[] data = new short[2];
        data[0] = (short) (reqVo.getControlId() - 1);

        // 处理场景时控保存标志位 05
        short[] saveArr = {1};

        // send.writeToAddr(reqVo.getDeviceId(), 6, "系统设置-控制方式-场景模式-时控" + reqVo.getControlId() + "-时段信息1-使能", 64, controlArr);
        if (reqVo.getEnabled() == true) {
            data[1] = (short) ((int) reqVo.getControlId());
            shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XABAF", new short[]{(short) data[1]});
            // send.writeToAddr(reqVo.getDeviceId(), 6, "系统设置-控制方式-场景模式-时控应用 ", 1, new short[]{data[0]});
            // send.writeToAddr(reqVo.getDeviceId(), 6, "系统设置-控制方式-场景模式-启用时控", 1, new short[]{data[1]});
        } else {
            short[] value = shortArrayRedisTemplate.opsForValue().get("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XABAF");
            if (value != null && value.length > 0) {
                // send.writeToAddr(reqVo.getDeviceId(), 6, "系统设置-控制方式-场景模式-时控应用 ", 1, new short[]{(short) (reqVo.getControlId() - 1)});
                // send.writeToAddr(reqVo.getDeviceId(), 6, "系统设置-控制方式-场景模式-启用时控", 1, new short[]{value[0]});
            }
        }
        // send.writeToAddr(reqVo.getDeviceId(), 5, "场景时控保存标志位", 1, saveArr);
        return R.ok("指令已下发");
    }

    public void updateSimpleControlCommon(SimpleControlReqVo reqVo) {
        int[] controlArr = new int[73];
        Map<String, Object> map = (HashMap<String, Object>) key.getRemote(reqVo.getDeviceId(), simpleControlAddrArr[reqVo.getControlId() - 1]);
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
                control.setTimeControlId(reqVo.getControlId());
                control.setDeviceId(Long.valueOf(reqVo.getDeviceId()));
                control.setTimeFrameId(i + 1);
                data1.add(control);
            }
        }

        if (data2 == null) {
            data2 = new LinkedList<>();
            for (int i = 0; i < 16; i++) {
                DevConfigSimpleGroup group = new DevConfigSimpleGroup();
                group.setSimpleId(reqVo.getControlId());
                group.setDeviceId(reqVo.getDeviceId());
                group.setGroupId(i + 1);
                group.setSelectStatus(0);
                data2.add(group);
            }
        }

        // 处理时序表 06
        List<SimpleControlTable> table = reqVo.getTable();
        for (int i = 0; i < controlArr.length - 1; i += 9) {
            // 亮度
            controlArr[i] = table.get((i / 9)).getLux();
            data1.get(i / 9).setLux(table.get((i / 9)).getLux());
            // 开关
            controlArr[i + 1] = table.get((i / 9)).getSwitchStatus();
            data1.get(i / 9).setSwitchStatus(table.get((i / 9)).getSwitchStatus());
            // 使能
            controlArr[i + 2] = table.get((i / 9)).getEnabledStatus();
            data1.get(i / 9).setEnabledStatus(table.get((i / 9)).getEnabledStatus());
            // 开始时间01~03
            int[] sTime = ScaleUtil.gbkToArr(table.get((i / 9)).getStime());
            controlArr[i + 3] = sTime[0];
            controlArr[i + 4] = sTime[1];
            controlArr[i + 5] = sTime[2];
            data1.get(i / 9).setStime(table.get((i / 9)).getStime());
            // 结束时间01~03
            int[] eTime = ScaleUtil.gbkToArr(table.get((i / 9)).getEtime());
            controlArr[i + 6] = eTime[0];
            controlArr[i + 7] = eTime[1];
            controlArr[i + 8] = eTime[2];
            data1.get(i / 9).setEtime(table.get((i / 9)).getEtime());
        }
        // 处理分组情况 06
        if (!Arrays.isNullOrEmpty(reqVo.getGroupIds())) {
            controlArr[72] = ScaleUtil.convertGroupsToRegisterValue(reqVo.getGroupIds());
            for (int i = 0; i < 16; i++) {
                data2.get(i).setSelectStatus(0);
            }
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < reqVo.getGroupIds().length; j++) {
                    if (i + 1 == reqVo.getGroupIds()[j]) {
                        data2.get(i).setSelectStatus(1);
                    }
                }
            }
        }
        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(reqVo.getDeviceId());

        // 处理启用 06
        short[] data = new short[2];
        data[0] = (short) (reqVo.getControlId() - 1);

        map.put("data1", data1);
        map.put("data2", data2);
        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":" + simpleControlAddrArr[reqVo.getControlId() - 1], map);

        DevInstruct instruct = new DevInstruct();
        instruct.setIp(tcpVo.getIp());
        instruct.setSalveId(reqVo.getDeviceId());
        instruct.setFeedback(2);

        instruct.setCode(6);
        instruct.setId(IdGenerator.UUIDId());
        instruct.setAddr(ADDR_SIMPLE[reqVo.getControlId()]);
        instruct.setAddrNum(73);
        instruct.setWriteValue(new JSONObject().set("arr", controlArr).toString());
        redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + reqVo.getDeviceId(), instruct);

        if (reqVo.getEnabled() == true) {
            data[1] = (short) ((int) reqVo.getControlId());
            shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XA8E6", new short[]{(short) data[1]});

            instruct.setId(IdGenerator.UUIDId());
            instruct.setAddr("0XA8E5");
            instruct.setAddrNum(1);
            instruct.setWriteValue(new JSONObject().set("arr", new short[]{data[0]}).toString());
            redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + reqVo.getDeviceId(), instruct);

            instruct.setId(IdGenerator.UUIDId());
            instruct.setAddr("0XA8E6");
            instruct.setAddrNum(1);
            instruct.setWriteValue(new JSONObject().set("arr", new short[]{data[1]}).toString());
            redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + reqVo.getDeviceId(), instruct);
        } else {
            short[] value = shortArrayRedisTemplate.opsForValue().get("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XA8E6");

            if (value != null && value.length > 0) {
                instruct.setId(IdGenerator.UUIDId());
                instruct.setAddr("0XA8E5");
                instruct.setAddrNum(1);
                instruct.setWriteValue(new JSONObject().set("arr", new short[]{(short) (reqVo.getControlId() - 1)}).toString());
                redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + reqVo.getDeviceId(), instruct);

                instruct.setId(IdGenerator.UUIDId());
                instruct.setAddr("0XA8E6");
                instruct.setAddrNum(1);
                instruct.setWriteValue(new JSONObject().set("arr", new short[]{value[0]}).toString());
                redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + reqVo.getDeviceId(), instruct);
            }


        }

        // 处理普通时控保存标志位 05
        short[] saveArr = {1};

        instruct.setCode(5);
        instruct.setId(IdGenerator.UUIDId());
        instruct.setAddr("0xC2C9");
        instruct.setAddrNum(1);
        instruct.setWriteValue(new JSONObject().set("arr", saveArr).toString());
        redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + reqVo.getDeviceId(), instruct);
    }

    public void updateSceneControlCommon(SceneControlReqVo reqVo) {
        int[] controlArr = new int[64];

        List<DevConfigTimeControlScene> scenes = (List<DevConfigTimeControlScene>) key.getRemote(reqVo.getDeviceId(), SceneControlCache.SCENE_PARAMS_ADDR_ARR[reqVo.getControlId() - 1]);

        if (scenes == null) {
            scenes = new LinkedList<>();
            for (int i = 0; i < 8; i++) {
                DevConfigTimeControlScene scene = new DevConfigTimeControlScene();
                scene.setDeviceId(reqVo.getDeviceId());
                scene.setTimeControlId(reqVo.getControlId());
                scene.setTimeFrameId(i + 1);
                scenes.add(scene);
            }
        }

        // 处理时序表 06
        List<SceneControlTable> table = reqVo.getTable();
        for (int i = 0; i < controlArr.length - 1; i += 8) {
            // 使能
            controlArr[i] = table.get((i / 8)).getEnabledStatus();
            scenes.get(i / 8).setEnabledStatus(table.get((i / 8)).getEnabledStatus());
            // 场景选择
            controlArr[i + 1] = table.get((i / 8)).getSceneSelect();
            scenes.get(i / 8).setSceneSelect(table.get((i / 8)).getSceneSelect());
            // 开始时间01~03
            int[] sTime = ScaleUtil.gbkToArr(table.get((i / 8)).getStime());
            controlArr[i + 2] = sTime[0];
            controlArr[i + 3] = sTime[1];
            controlArr[i + 4] = sTime[2];
            scenes.get(i / 8).setStime(table.get((i / 8)).getStime());
            // 结束时间01~03
            int[] eTime = ScaleUtil.gbkToArr(table.get((i / 8)).getEtime());
            controlArr[i + 5] = eTime[0];
            controlArr[i + 6] = eTime[1];
            controlArr[i + 7] = eTime[2];
            scenes.get(i / 8).setEtime(table.get((i / 8)).getEtime());
        }

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(reqVo.getDeviceId());
        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":" + SceneControlCache.SCENE_PARAMS_ADDR_ARR[reqVo.getControlId() - 1], scenes);

        // 处理启用 06
        short[] data = new short[2];
        data[0] = (short) (reqVo.getControlId() - 1);

        DevInstruct instruct = new DevInstruct();
        instruct.setIp(tcpVo.getIp());
        instruct.setSalveId(reqVo.getDeviceId());
        instruct.setFeedback(2);

        instruct.setCode(6);
        instruct.setId(IdGenerator.UUIDId());
        instruct.setAddr(ADDR_SCENE[reqVo.getControlId()]);
        instruct.setAddrNum(64);
        instruct.setWriteValue(new JSONObject().set("arr", controlArr).toString());
        redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + reqVo.getDeviceId(), instruct);


        if (reqVo.getEnabled() == true) {
            data[1] = (short) ((int) reqVo.getControlId());
            shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XABAF", new short[]{(short) data[1]});

            instruct.setId(IdGenerator.UUIDId());
            instruct.setAddr("0XABAE");
            instruct.setAddrNum(1);
            instruct.setWriteValue(new JSONObject().set("arr", new short[]{data[0]}).toString());
            redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + reqVo.getDeviceId(), instruct);

            instruct.setId(IdGenerator.UUIDId());
            instruct.setAddr("0XABAF");
            instruct.setAddrNum(1);
            instruct.setWriteValue(new JSONObject().set("arr", new short[]{data[1]}).toString());
            redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + reqVo.getDeviceId(), instruct);
        } else {
            short[] value = shortArrayRedisTemplate.opsForValue().get("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XABAF");

            if (value != null && value.length > 0) {
                instruct.setId(IdGenerator.UUIDId());
                instruct.setAddr("0XABAE");
                instruct.setAddrNum(1);
                instruct.setWriteValue(new JSONObject().set("arr", new short[]{(short) (reqVo.getControlId() - 1)}).toString());
                redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + reqVo.getDeviceId(), instruct);

                instruct.setId(IdGenerator.UUIDId());
                instruct.setAddr("0XABAF");
                instruct.setAddrNum(1);
                instruct.setWriteValue(new JSONObject().set("arr", new short[]{value[0]}).toString());
                redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + reqVo.getDeviceId(), instruct);
            }
        }

        // 处理场景时控保存标志位 05
        short[] saveArr = {1};

        instruct.setCode(5);
        instruct.setId(IdGenerator.UUIDId());
        instruct.setAddr("0xC2CA");
        instruct.setAddrNum(1);
        instruct.setWriteValue(new JSONObject().set("arr", saveArr).toString());
        redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + reqVo.getDeviceId(), instruct);
    }

    // 机柜设置-传感模式-红外传感器-照明参数 已保存缓存
    @PostMapping("/updateInfraredParams")
    public R<?> updateInfraredParams1(@RequestBody InfraredParamsReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateInfraredParams(reqVo);
    }

    public R<?> updateInfraredParams(@RequestBody InfraredParamsReqVo reqVo) {

        // 照明参数 06
        InfraredParamsTable table = reqVo.getTable();
        if (table.getInductiveLux() < 0 || table.getInductiveLux() > 100 || table.getUninductionLux() < 0 || table.getUninductionLux() > 100) {
            return R.warn("红外传感器亮度上下限值 0-100");
        }
        if (table.getDelayedTime() < 0 || table.getDelayedTime() > 120) return R.warn("红外传感器感应延迟上下限 0-120");
        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        int[] a = new int[3];
        a[reqVo.getSensorId() - 1] = 1;
        m.put("data", a);
        InfValue v = new InfValue();
        table.setSensorId(reqVo.getSensorId());
        table.setUninductionSwitchStatus(table.getUninductionSwitchStatus() == 0 ? 1 : 0);
        table.setEnabled(reqVo.getEnabled() ? 1 : 0);
        v.setData(Collections.singletonList(table));
        Integer[] groupIds = reqVo.getGroupIds();
        Integer[] sendGroups = new Integer[16];
        for (int i = 1; i < 17; i++) {
            sendGroups[i - 1] = 0;
            for (Integer groupId : groupIds) {
                if (groupId == i) {
                    sendGroups[i - 1] = 1;
                    break;
                }
            }
        }
        v.setData1(sendGroups);
        v.setEnabled(reqVo.getEnabled());
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.红外模式选择, m);
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.红外模式, v);

        Map<String, Object> sensors = (HashMap<String, Object>) key.getRemote(reqVo.getDeviceId(), "0XAE7A");
        if (sensors == null) {
            sensors = new HashMap<>();
            sensors.put("data1", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            sensors.put("data2", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            sensors.put("data3", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            List<DevConfigInfraredSensor> data = new LinkedList<>();
            for (int i = 0; i < 3; i++) {
                DevConfigInfraredSensor sensor = new DevConfigInfraredSensor();
                sensor.setDeviceId(reqVo.getDeviceId());
                sensor.setSensorId(i + 1);
                data.add(sensor);
            }
            sensors.put("data", data);
        }

        Integer[] groups = (Integer[]) sensors.get("data" + reqVo.getSensorId());
        List<DevConfigInfraredSensor> data = (List<DevConfigInfraredSensor>) sensors.get("data");

        int index = reqVo.getSensorId() - 1;
        data.get(index).setInductiveLux(table.getInductiveLux());
        data.get(index).setInductiveSwitchStatus(table.getInductiveSwitchStatus() == 0 ? 1 : 0);
        data.get(index).setUninductionLux(table.getUninductionLux());
        data.get(index).setUninductionSwitchStatus(table.getUninductionSwitchStatus() == 1 ? 0 : 1);
        data.get(index).setDelayedTime(table.getDelayedTime());
        data.get(index).setEnabled(reqVo.getEnabled() ? 1 : 0);

        java.util.Arrays.fill(groups, 0);

        for (int i = 0; i < 16; i++)
            for (int j = 0; j < reqVo.getGroupIds().length; j++)
                if (i + 1 == reqVo.getGroupIds()[j]) {
                    groups[i] = 1;
                    break;
                }

        sensors.put("data" + reqVo.getSensorId(), groups);
        sensors.put("data", data);

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(reqVo.getDeviceId());
        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XAE7A", sensors);

        return R.ok("指令已下发");
    }


    // 机柜设置-传感模式-照度传感器 已保存缓存
    // 外控通道与外控地址不需要写入
    @PostMapping("/updateIlluminanceParams")
    public R<?> updateIlluminanceParams1(@RequestBody IlluminanceParamsReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateIlluminanceParams(reqVo);
    }

    public R<?> updateIlluminanceParams(@RequestBody IlluminanceParamsReqVo reqVo) {
        // 照明参数 06
        IlluminanceParamsTable table = reqVo.getTable();

        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        int[] a = new int[5];
        a[reqVo.getSensorId() - 1] = 1;
        m.put("data", a);
        IllValue v = new IllValue();
        v.setEnabled(reqVo.getEnabled());
        ArrayList<Integer> ll = new ArrayList<>(java.util.Arrays.asList(reqVo.getGroupIds()));
        ArrayList<Integer> groupIds = new ArrayList<>();
        for (int i = 1; i < 17; i++) {
            if (ll.contains(i)) {
                groupIds.add(1);
            } else groupIds.add(0);
        }
        v.setData1(groupIds.toArray(new Integer[0]));
        table.setSensorId(reqVo.getSensorId());
        table.setEnabled(reqVo.getEnabled() ? 1 : 0);
        table.setOutControlStatus(table.getOutControlStatus() == 1 ? 0 : 1);
        v.setData(Collections.singletonList(table));
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.照度模式选择, m);
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.照度模式, v);

        Map<String, Object> sensors = (HashMap<String, Object>) key.getRemote(reqVo.getDeviceId(), "0XAE8F");

        if (sensors == null) {
            sensors = new HashMap<>();
            sensors.put("data1", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            sensors.put("data2", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            sensors.put("data3", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            sensors.put("data4", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            sensors.put("data5", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            List<DevConfigIlluminanceSensor> data = new LinkedList<>();
            for (int i = 0; i < 5; i++) {
                DevConfigIlluminanceSensor sensor = new DevConfigIlluminanceSensor();
                sensor.setDeviceId(reqVo.getDeviceId());
                sensor.setSensorId(i + 1);
                data.add(sensor);
            }
            sensors.put("data", data);
        }

        Integer[] groups = (Integer[]) sensors.get("data" + reqVo.getSensorId());
        List<DevConfigIlluminanceSensor> data = (List<DevConfigIlluminanceSensor>) sensors.get("data");

        int index = reqVo.getSensorId() - 1;
        data.get(index).setIlluminanceLux(table.getIlluminanceLux());
        data.get(index).setConstantIlluminanceLux(table.getConstantIlluminanceLux());
        data.get(index).setHysteresisLux(table.getHysteresisLux());
        data.get(index).setAdjustmentTimeSeconds(table.getAdjustmentTimeSeconds());
        data.get(index).setOutControlStatus(table.getOutControlStatus() == 1 ? 0 : 1);
        data.get(index).setEnabled(reqVo.getEnabled() ? 1 : 0);

        java.util.Arrays.fill(groups, 0);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < reqVo.getGroupIds().length; j++) {
                if (i + 1 == reqVo.getGroupIds()[j]) {
                    groups[i] = 1;
                    break;
                }
            }
        }

        sensors.put("data" + reqVo.getSensorId(), groups);
        sensors.put("data", data);

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(reqVo.getDeviceId());
        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XAE8F", sensors);

        return R.ok("指令已下发");
    }

    // 机柜设置-其他设置-机柜信息保存 已保存缓存
    @PostMapping("/deviceSave")
    public R<?> deviceSave(@RequestBody DeviceSaveReqVo reqVo) {
        DevBaseDevice device = new DevBaseDevice();
        device.setId(Long.valueOf(reqVo.getDeviceId()));
        device.setDeviceName(reqVo.getDeviceName());
        device.setRegionId(Long.valueOf(reqVo.getZoneId()));
        int index = deviceMapper.updateById(device);
        return index > 0 ? R.ok("保存成功") : R.fail("保存失败");
    }

    // 机柜设置-其他设置-通讯参数保存 已保存缓存
    @PostMapping("/communicateSave")
    public R<?> communicateSave(@RequestBody CommunicateSaveReqVo reqVo) {
        DevBaseDevice device = new DevBaseDevice();
        device.setId(Long.valueOf(reqVo.getDeviceNo()));
        device.setIp(reqVo.getIp());
        redisTemplate.delete("zm:create-tcp:" + device.getId());
        redisTemplate.delete("zm:dev_base_devices:list");
        int index = deviceMapper.updateById(device);
        return index > 0 ? R.ok("保存成功") : R.fail("保存失败");
    }

    // 机柜设置-其他设置-对时保存 已保存缓存
    @PostMapping("/timeSave")
    public R<?> timeSave1(@RequestBody TimeSaveReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return timeSave(reqVo);
    }

    public R<?> timeSave(@RequestBody TimeSaveReqVo reqVo) {
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.对时设置, new TimeVO());
        return R.ok("启动对时");
    }

    public R<?> timeSaveNow(int deviceNo) {
        mqttPublisher.publish(deviceNo, PublishKey.对时设置, new TimeVO());
        return R.ok("启动对时");
    }

    // 改场景名称 已保存缓存
    @GetMapping("/sceneName")
    public R<?> sceneName1(Integer deviceId, Integer sceneId, String name) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return sceneName(deviceId, sceneId, name);
    }

    public R<?> sceneName(Integer deviceId, Integer sceneId, String name) {
        CommonDataString d = new CommonDataString();
        CommonDataString.D dd = new CommonDataString.D();
        dd.setId(sceneId);
        dd.setValue(name);
        d.setAddr("0xA483");
        d.setData(dd);
        mqttPublisher.publish(deviceId, PublishKey.场景命名, d);
        String cacheAddr = "";
        if (10 > sceneId) {
            for (int i = 1; i < 10; i++) {
                if (i == sceneId) {
                    cacheAddr = SceneCache.SCENE_NAME_ADDR_ARR[i - 1];
                    break;
                }
            }
        }
        if (10 == sceneId) {
            //            addrName = "系统设置-场景设置-场景命名10_01";
            cacheAddr = SceneCache.SCENE_NAME_ADDR_ARR[9];
        }

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        stringRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + deviceId + ":" + cacheAddr, name);

        return R.ok("指令下发成功");
    }

    // 直流机柜-机柜设置-交流开关 已保存缓存
    @PostMapping("/updateAcControl")
    public R<?> updateAcControl1(@RequestBody List<TimeControlAcRespVo> reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateAcControl(reqVo);
    }

    public R<?> updateAcControl(@RequestBody List<TimeControlAcRespVo> reqVo) {
        HashMap<String, Object> map = (HashMap<String, Object>) key.getRemote(reqVo.get(0).getDeviceId(), "0XAF1E");
        if (map == null) {
            map = new HashMap<>();
            DevDeviceRemoteRead remoteRead = new DevDeviceRemoteRead();
            remoteRead.setId(reqVo.get(0).getDeviceId());
            remoteRead.setDeviceId(reqVo.get(0).getDeviceId());
            remoteRead.setAcSwitchNum(0);
            map.put("acNum", remoteRead);
        }

        // 存入缓存
        List<DevConfigTimeControlAc> data = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            DevConfigTimeControlAc controlAc = new DevConfigTimeControlAc();
            controlAc.setDeviceId(reqVo.get(i).getDeviceId());
            controlAc.setFrameId(i + 1);
            controlAc.setSwitchStatus(reqVo.get(i).getStatus() == 1 ? 0 : 1);
            controlAc.setEnable(reqVo.get(i).getEnabled() == 1 ? 0 : 1);
            String[] sTime = reqVo.get(i).getStime().split(":");
            controlAc.setStimeHour(Integer.parseInt(sTime[0]));
            controlAc.setStimeMin(Integer.parseInt(sTime[1]));
            String[] eTime = reqVo.get(i).getEtime().split(":");
            controlAc.setEtimeHour(Integer.parseInt(eTime[0]));
            controlAc.setEtimeMin(Integer.parseInt(eTime[1]));
            data.add(controlAc);
        }

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(reqVo.get(0).getDeviceId());
        map.put("controlAc", data);
        AcValue v = new AcValue();
        v.setAcSwitchNum(((DevDeviceRemoteRead) map.getOrDefault("acNum", new DevDeviceRemoteRead())).getAcSwitchNum());
        v.setData(data);
        mqttPublisher.publish(reqVo.get(0).getDeviceId(), PublishKey.交流开关, v);

        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0XAF1E", map);

        return R.ok("指令下发成功");
    }

    // 直流机柜-机柜控制-模块控制 AC/DC输出电压设置 已保存缓存
    @GetMapping("/updateAcDc")
    public R<?> updateAcDc1(Integer deviceId, BigDecimal value) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateAcDc(deviceId, value);
    }

    public R<?> updateAcDc(Integer deviceId, BigDecimal value) {
        int val;
        String valueStr = value.toString();
        try {
            String[] split = valueStr.split("\\.");
            val = Integer.parseInt(split[0] + split[1]);
        } catch (Exception e) {
            val = Integer.parseInt(valueStr) * 10;
        }
        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        short[] saveArr = key.getRemoteByArr(deviceId, "0xA007");
        if (saveArr[0] == -1) {
            saveArr = new short[]{(short) val, 360};
        } else {
            saveArr[0] = (short) val;
        }
        shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0xA007", saveArr);
        String dcDc = saveArr[1] + "";
        String dcDcValue = dcDc.substring(0, dcDc.length() - 1) + "." + dcDc.substring(dcDc.length() - 1);
        ModuleValue v = new ModuleValue();
        v.setAcdc(value.toString());
        v.setDcdc(dcDcValue);
        mqttPublisher.publish(deviceId, PublishKey.模块输出设置, v);
        return R.ok("指令下发成功");
    }

    // 直流机柜-机柜控制-模块控制 DC/DC输出电压设置 已保存缓存
    @GetMapping("/updateDcDc")
    public R<?> updateDcDc1(Integer deviceId, BigDecimal value) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateDcDc(deviceId, value);
    }

    public R<?> updateDcDc(Integer deviceId, BigDecimal value) {
        int val;
        String valueStr = value.toString();
        try {
            String[] split = valueStr.split("\\.");
            val = Integer.parseInt(split[0] + split[1]);
        } catch (Exception e) {
            val = Integer.parseInt(valueStr) * 10;
        }
        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        short[] saveArr = key.getRemoteByArr(deviceId, "0xA007");
        if (saveArr[0] == -1) {
            saveArr = new short[]{220, (short) val};
        } else {
            saveArr[1] = (short) val;
        }
        shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + tcpVo.getIp() + ":" + tcpVo.getId() + ":0xA007", saveArr);
        String acDc = saveArr[0] + "";
        String acDcValue = acDc.substring(0, acDc.length() - 1) + "." + acDc.substring(acDc.length() - 1);
        ModuleValue v = new ModuleValue();
        v.setAcdc(acDcValue);
        v.setDcdc(value.toString());
        mqttPublisher.publish(deviceId, PublishKey.模块输出设置, v);
        return R.ok("指令下发成功");
    }

    // 机柜控制-照明控制-总开关-开关控制 已保存缓存
    @GetMapping("/systemSwitch")
    public R<?> systemSwitch1(Integer deviceId, Integer systemSwitch) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return systemSwitch(deviceId, systemSwitch);
    }

    public R<?> systemSwitch(Integer deviceId, Integer systemSwitch) {
        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        m.put("addr", "0xC000");
        m.put("data", systemSwitch);
        mqttPublisher.publish(deviceId, PublishKey.系统总开关, m);
        return R.ok("指令下发成功");
    }

    // 机柜控制-照明控制-工作模式-开关控制 已保存缓存
    @GetMapping("/workModule")
    public R<?> workModule1(Integer deviceId, Integer workModule) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return workModule(deviceId, workModule);
    }

    public R<?> workModule(Integer deviceId, Integer workModule) {
        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        m.put("addr", "0xC001");
        m.put("data", workModule);
        mqttPublisher.publish(deviceId, PublishKey.工作模式, m);
        redisTemplate.opsForHash().put("zm:workModule", String.valueOf(deviceId), workModule);
        return R.ok("指令下发成功");
    }

    // 直流机柜-机柜控制-照明控制-回路控制-控制亮度 已保存缓存
    @PostMapping("/loopControlLux")
    public R<?> loopControlLux1(@RequestBody WriteLoopReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return loopControlLux(reqVo);
    }

    public R<?> loopControlLux(@RequestBody WriteLoopReqVo reqVo) {
        LoopControlVO v = new LoopControlVO();
        v.setModule(2);
        LoopControlVO.D d = new LoopControlVO.D();
        d.setLux(reqVo.getLux());

        // 处理回路选中数组
        int[] loopSelectArr = new int[40];
        for (int i = 0; i < reqVo.getLoopControl().size(); i++) {
            loopSelectArr[i] = reqVo.getLoopControl().get(i);
        }
        d.setLoopArr(loopSelectArr);
        v.setData(d);
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.回路总开关, v);

        intArrayRedisTemplate.opsForValue().set("zm:select:loop:" + reqVo.getDeviceId(), loopSelectArr);

        return R.ok("操作成功");
    }

    // 直流机柜-机柜控制-照明控制-回路控制-控制开关 已保存缓存
    @PostMapping("/loopControlSwitch")
    public R<?> loopControlSwitch1(@RequestBody WriteLoopReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return loopControlSwitch(reqVo);
    }

    public R<?> loopControlSwitch(@RequestBody WriteLoopReqVo reqVo) {
        LoopControlVO v = new LoopControlVO();
        v.setModule(1);
        LoopControlVO.D d = new LoopControlVO.D();
        d.setValue(reqVo.getSwitchStatus());

        // 处理回路选中数组
        int[] loopSelectArr = new int[40];
        for (int i = 0; i < reqVo.getLoopControl().size(); i++) {
            loopSelectArr[i] = reqVo.getLoopControl().get(i);
        }
        d.setLoopArr(loopSelectArr);
        v.setData(d);
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.回路总开关, v);

        intArrayRedisTemplate.opsForValue().set("zm:select:loop:" + reqVo.getDeviceId(), loopSelectArr);

        return R.ok("指令下发成功");
    }

    // 直流机柜-机柜控制-照明控制-分组控制-控制亮度 已保存缓存
    @PostMapping("/groupControlLux")
    public R<?> groupControlLux1(@RequestBody WriteGroupReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return groupControlLux(reqVo);
    }

    public R<?> groupControlLux(@RequestBody WriteGroupReqVo reqVo) {
        if (ObjectUtils.isEmpty(reqVo) || Arrays.isNullOrEmpty(reqVo.getGroupSelectArr())) {
            return R.warn("输入数据无效");
        }
        LoopControlVO v = new LoopControlVO();
        v.setModule(2);
        v.setAddr("0xC046");
        LoopControlVO.D d = new LoopControlVO.D();
        d.setLux(reqVo.getLux());

        // 处理分组选中数组
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

    // 直流机柜-机柜控制-照明控制-分组控制-控制开关 已保存缓存
    @PostMapping("/groupControlSwitch")
    public R<?> groupControlSwitch1(@RequestBody WriteGroupReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return groupControlSwitch(reqVo);
    }

    public R<?> groupControlSwitch(@RequestBody WriteGroupReqVo reqVo) {
        if (ObjectUtils.isEmpty(reqVo) || Arrays.isNullOrEmpty(reqVo.getGroupSelectArr())) {
            return R.warn("输入数据无效");
        }
        LoopControlVO v = new LoopControlVO();
        v.setModule(1);
        v.setAddr("0xC046");
        LoopControlVO.D d = new LoopControlVO.D();
        d.setValue(reqVo.getSwitchStatus());

        // 处理分组选中数组
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

    // 机柜设置-时控模式-普通时控模式参数-一键设置参数接口 已保存缓存
    @PostMapping("/updateSimpleControlToAll")
    public R<?> updateSimpleControlToAll1(@RequestBody SimpleControlReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateSimpleControlToAll(reqVo);
    }

    public R<?> updateSimpleControlToAll(@RequestBody SimpleControlReqVo reqVo) {
        for (int i = 0; i < reqVo.getTable().size(); i++) {
            if (reqVo.getTable().get(i).getLux() < 0 || reqVo.getTable().get(i).getLux() > 100) {
                return R.warn("普通时控亮度上下限值 0-100");
            }
        }
        List<DevBaseDevice> devices = deviceMapper.selectList();
        for (DevBaseDevice device : devices) {
            try {
                reqVo.setDeviceId(Math.toIntExact(device.getDeviceNo()));
                updateSimpleControlCommon(reqVo);
            } catch (Exception e) {
                log.error("Error in updateSimpleControlToAll for device: {}", device.getDeviceNo(), e);
            }
        }
        return R.ok("指令已下发");
    }

    // 机柜设置-时控模式-场景时控模式参数-一键设置参数接口 已保存缓存
    @PostMapping("/updateSceneControlToAll")
    public R<?> updateSceneControlToAll1(@RequestBody SceneControlReqVo reqVo) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateSceneControlToAll(reqVo);
    }

    public R<?> updateSceneControlToAll(@RequestBody SceneControlReqVo reqVo) {
        List<DevBaseDevice> devices = deviceMapper.selectList();
        for (DevBaseDevice device : devices) {
            try {
                reqVo.setDeviceId(Math.toIntExact(device.getDeviceNo()));
                updateSceneControlCommon(reqVo);
            } catch (Exception e) {
                log.error("Error in updateSceneControlToAll for device: {}", device.getDeviceNo(), e);
            }
        }
        return R.ok("指令已下发");
    }

    // 获取当前模式
    @GetMapping("/getModuleNow")
    public R<?> getModuleNow(@Param("deviceId") Integer deviceId) {
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        return R.ok(remote);
    }

    // 系统设置-控制方式-模式选择
    @GetMapping("/selectTimeModule")
    public R<?> selectTimeModule1(Integer deviceId, Integer timeModule) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return selectTimeModule(deviceId, timeModule);
    }

    public R<?> selectTimeModule(Integer deviceId, Integer timeModule) {
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        if (timeModule == 1) remote.setTimeModule("noEnabled");
        else if (timeModule == 2) remote.setTimeModule("simple");
        else if (timeModule == 3) remote.setTimeModule("scene");
        redisTemplate.opsForValue().set(REMOTE_KEY + key.getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + "0XB715", remote);
        ModuleSelect v = new ModuleSelect();
        v.setData(Collections.singletonList(remote));
        mqttPublisher.publish(deviceId, PublishKey.模式选择, v);
        return R.ok("指令已下发");
    }

    // 系统设置-控制方式-红外传感模式
    @GetMapping("/selectInfraredModule")
    public R<?> selectInfraredModule1(Integer deviceId, Boolean enabled) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return selectInfraredModule(deviceId, enabled);
    }

    public R<?> selectInfraredModule(Integer deviceId, Boolean enabled) {
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        remote.setInfraredSensorModule(enabled);
        redisTemplate.opsForValue().set(REMOTE_KEY + key.getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + "0XB715", remote);
        ModuleSelect v = new ModuleSelect();
        v.setData(Collections.singletonList(remote));
        mqttPublisher.publish(deviceId, PublishKey.模式选择, v);
        return R.ok("指令已下发");
    }

    // 系统设置-控制方式-照度传感模式
    @GetMapping("/selectIlluminanceModule")
    public R<?> selectIlluminanceModule1(Integer deviceId, Boolean enabled) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return selectIlluminanceModule(deviceId, enabled);
    }

    public R<?> selectIlluminanceModule(Integer deviceId, Boolean enabled) {
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        remote.setIlluminanceSensorModule(enabled);
        redisTemplate.opsForValue().set(REMOTE_KEY + key.getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + "0XB715", remote);
        ModuleSelect v = new ModuleSelect();
        v.setData(Collections.singletonList(remote));
        mqttPublisher.publish(deviceId, PublishKey.模式选择, v);
        return R.ok("指令已下发");
    }

    // 系统控制-照明控制-手动模式选择
    @GetMapping("/selectHandModule")
    public R<?> selectHandModule1(Integer deviceId, Integer handModule) {
        if (module()) return R.warn("设备处于远程控制模式，不能下发指令");
        return selectHandModule(deviceId, handModule);
    }

    public R<?> selectHandModule(Integer deviceId, Integer handModule) {

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

    // 当设备处于远程模式时以上接口不能使用
    private Boolean module() {
        String module = (String) redisTemplate.opsForValue().get("zm:global:module:select");
        return "on-the-line".equals(module);
    }

    public void moduleSetting(Integer no, Addr0XB715.Data remote) {
        if (remote != null) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("msgId", IdGenerator.UUIDId());
            m.put("addr", "0XB715");
            m.put("data", Collections.singletonList(remote));
            mqttPublisher.publish(no, PublishKey.模式选择, m);
        }
    }
}
