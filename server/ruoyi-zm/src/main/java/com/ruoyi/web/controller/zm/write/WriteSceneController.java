package com.ruoyi.web.controller.zm.write;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.Key;
import com.ruoyi.cache.ModuleGuard;
import com.ruoyi.cache.SceneCache;
import com.ruoyi.cache.SceneControlCache;
import com.ruoyi.cache.WriteQueueCache;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.modbus.util.RecordPlus;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqtt03.addr03.handler.addr0xA483.Addr0xA483;
import com.ruoyi.mqtt03.addr03.handler.addr0xA483.Addr0xA483Handler;
import com.ruoyi.mqttwrite.common.CommonDataString;
import com.ruoyi.mqttwrite.scene.SceneParams;
import com.ruoyi.mqttwrite.scene.SceneValue;
import com.ruoyi.zm.domain.*;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.domain.vo.SceneControlReqVo;
import com.ruoyi.zm.domain.vo.SceneControlTable;
import com.ruoyi.zm.domain.vo.SceneParamsReqVo;
import com.ruoyi.zm.service.IDevBaseDeviceService;
import com.ruoyi.zm.service.WriteSceneService;
import com.ruoyi.zm.utils.IdGenerator;
import com.ruoyi.zm.utils.ScaleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/write/scene")
public class WriteSceneController {

    private final IDevBaseDeviceService deviceService;
    private final WriteQueueCache writeQueueCache;
    private final Key key;
    private final Key k;
    private final RecordPlus record;
    private final MqttPublisher mqttPublisher;
    private final ModuleGuard moduleGuard;
    private final WriteSceneService writeSceneService;

    private static final String[] SCENE_PARAMS_ARR = {"", "0xAFC2", "0xAFE3", "0xB004",
        "0xB025", "0xB046", "0xB067", "0xB088", "0xB0A9", "0xB0CA", "0xB0EB"
    };

    @GetMapping("/intoScenes")
    public R<?> intoScenes1(Integer sceneId) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return intoScenes(sceneId);
    }

    public R<?> intoScenes(Integer sceneId) {
        record.runModule();
        List<DevBaseDevice> devBaseDevices = deviceService.listAll();
        if (devBaseDevices != null && !devBaseDevices.isEmpty()) {
            devBaseDevices.forEach(item -> writeSceneService.intoScene(Math.toIntExact(item.getDeviceNo()), sceneId));
        }
        writeQueueCache.setGlobalSceneSelect(sceneId + "");
        return R.ok("操作成功");
    }

    @GetMapping("/intoScene")
    public R<?> intoScene1(Integer deviceId, Integer sceneId) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeSceneService.intoSceneNotRecord(deviceId, sceneId);
    }

    public R<?> intoScene(Integer deviceId, Integer sceneId) {
        return writeSceneService.intoScene(deviceId, sceneId);
    }

    @GetMapping("/sceneName")
    public R<?> sceneName1(Integer deviceId, Integer sceneId, String name) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
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
            cacheAddr = SceneCache.SCENE_NAME_ADDR_ARR[9];
        }

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        writeQueueCache.setSceneName(tcpVo.getIp(), deviceId, cacheAddr, name);

        return R.ok("指令下发成功");
    }

    @PostMapping("/updateSceneParams")
    public R<?> updateSceneParams1(@RequestBody SceneParamsReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
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
        writeQueueCache.putHash(Addr0xA483Handler.writeKey + reqVo.getDeviceId(), reqVo.getSceneId() + "m", d1);

        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.场景设置, s);

        Integer[] data = new Integer[33];
        if (reqVo.getSelectLuxArr() != null && reqVo.getSelectLuxArr().length > 0) {
            System.arraycopy(reqVo.getSelectLuxArr(), 0, data, 0, 16);
        }
        if (reqVo.getSelectSwitchArr() != null && reqVo.getSelectSwitchArr().length > 0) {
            System.arraycopy(reqVo.getSelectSwitchArr(), 0, data, 16, 16);
        }
        if (reqVo.getSelectGroupIdArr().length > 0) {
            int lastValue = ScaleUtil.convertGroupsToRegisterValue(reqVo.getSelectGroupIdArr());
            data[data.length - 1] = lastValue;
        } else {
            data[data.length - 1] = 0;
        }

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
        writeQueueCache.setQueueCache(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), SCENE_PARAMS_ARR[reqVo.getSceneId()], scenes);

        return R.ok("指令已下发");
    }

    @PostMapping("/updateSceneControl")
    public R<?> updateSceneControl1(@RequestBody SceneControlReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
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

        List<SceneControlTable> table = reqVo.getTable();
        for (int i = 0; i < controlArr.length - 1; i += 8) {
            controlArr[i] = table.get((i / 8)).getEnabledStatus();
            scenes.get(i / 8).setEnabledStatus(table.get((i / 8)).getEnabledStatus());
            controlArr[i + 1] = table.get((i / 8)).getSceneSelect();
            scenes.get(i / 8).setSceneSelect(table.get((i / 8)).getSceneSelect());
            int[] sTime = ScaleUtil.gbkToArr(table.get((i / 8)).getStime());
            controlArr[i + 2] = sTime[0];
            controlArr[i + 3] = sTime[1];
            controlArr[i + 4] = sTime[2];
            scenes.get(i / 8).setStime(table.get((i / 8)).getStime());
            int[] eTime = ScaleUtil.gbkToArr(table.get((i / 8)).getEtime());
            controlArr[i + 5] = eTime[0];
            controlArr[i + 6] = eTime[1];
            controlArr[i + 7] = eTime[2];
            scenes.get(i / 8).setEtime(table.get((i / 8)).getEtime());
        }

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(reqVo.getDeviceId());
        writeQueueCache.setQueueCache(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), SceneControlCache.SCENE_PARAMS_ADDR_ARR[reqVo.getControlId() - 1], scenes);
        Map<String, Integer> m = new HashMap<>();
        m.put("data3", (reqVo.getEnabled() != null && reqVo.getEnabled()) ? reqVo.getControlId() : 0);
        if (Objects.equals(m.getOrDefault("data3", 0), reqVo.getControlId()))
            writeQueueCache.setQueueCache(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XAAEEEnabled", m);

        short[] data = new short[2];
        data[0] = (short) (reqVo.getControlId() - 1);

        short[] saveArr = {1};

        if (reqVo.getEnabled() == true) {
            data[1] = (short) ((int) reqVo.getControlId());
            writeQueueCache.setQueueCacheShortArr(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XABAF", new short[]{(short) data[1]});
        } else {
            short[] value = writeQueueCache.getQueueCacheShortArr(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XABAF");
            if (value != null && value.length > 0) {
            }
        }
        return R.ok("指令已下发");
    }

    @PostMapping("/updateSceneControlToAll")
    public R<?> updateSceneControlToAll1(@RequestBody SceneControlReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateSceneControlToAll(reqVo);
    }

    public R<?> updateSceneControlToAll(@RequestBody SceneControlReqVo reqVo) {
        List<DevBaseDevice> devices = deviceService.listAll();
        for (DevBaseDevice device : devices) {
            try {
                reqVo.setDeviceId(Math.toIntExact(device.getDeviceNo()));
                writeSceneService.updateSceneControlCommon(reqVo);
            } catch (Exception e) {
                log.error("Error in updateSceneControlToAll for device: {}", device.getDeviceNo(), e);
            }
        }
        return R.ok("指令已下发");
    }

}
