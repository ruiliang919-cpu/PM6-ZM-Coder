package com.ruoyi.web.controller.zm;


import com.ruoyi.cache.ModuleGuard;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt03.addr03.handler.addr0XB715.Addr0XB715;
import com.ruoyi.mqttwrite.accontrol.AcValue;
import com.ruoyi.mqttwrite.ill.IllValue;
import com.ruoyi.mqttwrite.inf.InfValue;
import com.ruoyi.mqttwrite.loopcontrol.LoopControlVO;
import com.ruoyi.mqttwrite.simple.SimpleValue;
import com.ruoyi.mqttwrite.time.TimeVO;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.web.controller.zm.write.*;
import com.ruoyi.zm.domain.*;
import com.ruoyi.zm.domain.vo.*;
import com.ruoyi.zm.service.WriteSceneService;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * @deprecated 已拆分为 5 个独立 Controller，参见 write/ 子包:
 *   WriteGroupController, WriteSceneController, WriteDeviceController,
 *   WriteControlController, WriteModuleController
 *   旧端点保留兼容，新功能请使用拆分后的路径。
 *
 * 本类现为薄委托层，所有业务逻辑已迁移到上述 5 个 Controller。
 * 外部服务（Topic handlers、Recover、TaskInit 等）通过注入本类调用，
 * 方法签名保持不变，内部委托到对应的拆分 Controller。
 */
@Deprecated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/write")
public class WriteController {

    /** @deprecated 使用 WriteGroupController.DELETE_LOOP_NOS */
    public static final Integer[] DELETE_LOOP_NOS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33,
        34, 35, 36, 37, 38, 39, 40};

    // ── 委托目标 Controller ──────────────────────────────────
    private final WriteGroupController writeGroupController;
    private final WriteSceneController writeSceneController;
    private final WriteDeviceController writeDeviceController;
    private final WriteControlController writeControlController;
    private final WriteModuleController writeModuleController;
    private final ModuleGuard moduleGuard;
    private final MqttPublisher mqttPublisher;
    private final WriteSceneService writeSceneService;

    // ══════════════════════════════════════════════════════════
    // 分组操作 → WriteGroupController
    // ══════════════════════════════════════════════════════════

    @PostMapping("/groupName")
    public R<?> groupName1(@RequestBody GroupNameVo groupNameVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeGroupController.groupName(groupNameVo);
    }

    public R<?> groupName(@RequestBody GroupNameVo groupNameVo) {
        return writeGroupController.groupName(groupNameVo);
    }

    @PostMapping("/updateLoop")
    public synchronized R<?> updateLoop1(@RequestBody UpdateLoopReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeGroupController.updateLoop(reqVo);
    }

    public R<?> updateLoop(@RequestBody UpdateLoopReqVo reqVo) {
        return writeGroupController.updateLoop(reqVo);
    }

    // ══════════════════════════════════════════════════════════
    // 分区控制 → WriteControlController
    // ══════════════════════════════════════════════════════════

    @GetMapping("/updateZoneLightSwitch")
    public R<?> updateZoneLightSwitch1(Integer zoneId, Integer swStatus) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        swStatus = (swStatus == 1 ? 0 : 1);
        return writeControlController.updateZoneLightSwitch(String.valueOf(zoneId), swStatus);
    }

    public R<?> updateZoneLightSwitch(Integer zoneId, Integer swStatus) {
        return writeControlController.updateZoneLightSwitch(String.valueOf(zoneId), swStatus);
    }

    @GetMapping("/updateZoneLightLux")
    public R<?> updateZoneLightLux1(Integer zoneId, Integer lux) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeControlController.updateZoneLightLux(String.valueOf(zoneId), lux);
    }

    public R<?> updateZoneLightLux(Integer zoneId, Integer lux) {
        return writeControlController.updateZoneLightLux(String.valueOf(zoneId), lux);
    }

    // ══════════════════════════════════════════════════════════
    // 场景操作 → WriteSceneController
    // ══════════════════════════════════════════════════════════

    @GetMapping("/intoScenes")
    public R<?> intoScenes1(Integer sceneId) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeSceneController.intoScenes(sceneId);
    }

    public R<?> intoScenes(Integer sceneId) {
        return writeSceneController.intoScenes(sceneId);
    }

    @GetMapping("/intoScene")
    public R<?> intoScene1(Integer deviceId, Integer sceneId) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeSceneService.intoSceneNotRecord(deviceId, sceneId);
    }

    public R<?> intoSceneNotRecord(Integer deviceId, Integer sceneId) {
        return writeSceneService.intoSceneNotRecord(deviceId, sceneId);
    }

    public R<?> intoScene(Integer deviceId, Integer sceneId) {
        return writeSceneController.intoScene(deviceId, sceneId);
    }

    @PostMapping("/updateSceneParams")
    public R<?> updateSceneParams1(@RequestBody SceneParamsReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeSceneController.updateSceneParams(reqVo);
    }

    public R<?> updateSceneParams(@RequestBody SceneParamsReqVo reqVo) {
        return writeSceneController.updateSceneParams(reqVo);
    }

    @GetMapping("/sceneName")
    public R<?> sceneName1(Integer deviceId, Integer sceneId, String name) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeSceneController.sceneName(deviceId, sceneId, name);
    }

    public R<?> sceneName(Integer deviceId, Integer sceneId, String name) {
        return writeSceneController.sceneName(deviceId, sceneId, name);
    }

    // ══════════════════════════════════════════════════════════
    // 普通时控 → WriteControlController
    // ══════════════════════════════════════════════════════════

    @PostMapping("/updateSimpleControl")
    public R<?> updateSimpleControl1(@RequestBody SimpleControlReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateSimpleControl(reqVo);
    }

    public R<?> updateSimpleControl(@RequestBody SimpleControlReqVo reqVo) {
        // 简单委托 — 旧签名(SimpleControlReqVo)与新签名(SimpleValue)不同，
        // 保留此方法作为旧调用者的入口，内部构建 SimpleValue 后委托
        SimpleValue v = new SimpleValue();
        v.setControlId(reqVo.getControlId());
        v.setEnabled(reqVo.getEnabled());
        v.setData1(reqVo.getTable());
        v.setData2(SimpleValue.selectArr(reqVo.getGroupIds()));
        return writeControlController.updateSimpleControl(reqVo.getDeviceId(), v);
    }

    public void updateSimpleControlCommon(SimpleControlReqVo reqVo) {
        SimpleValue v = new SimpleValue();
        v.setControlId(reqVo.getControlId());
        v.setEnabled(reqVo.getEnabled());
        v.setData1(reqVo.getTable());
        v.setData2(SimpleValue.selectArr(reqVo.getGroupIds()));
        writeControlController.updateSimpleControlToAll(v);
    }

    @PostMapping("/updateSimpleControlToAll")
    public R<?> updateSimpleControlToAll1(@RequestBody SimpleControlReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateSimpleControlToAll(reqVo);
    }

    public R<?> updateSimpleControlToAll(@RequestBody SimpleControlReqVo reqVo) {
        SimpleValue v = new SimpleValue();
        v.setControlId(reqVo.getControlId());
        v.setEnabled(reqVo.getEnabled());
        v.setData1(reqVo.getTable());
        v.setData2(SimpleValue.selectArr(reqVo.getGroupIds()));
        return writeControlController.updateSimpleControlToAll(v);
    }

    // ══════════════════════════════════════════════════════════
    // 场景时控 → WriteSceneController
    // ══════════════════════════════════════════════════════════

    @PostMapping("/updateSceneControl")
    public R<?> updateSceneControl1(@RequestBody SceneControlReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeSceneController.updateSceneControl(reqVo);
    }

    public R<?> updateSceneControl(@RequestBody SceneControlReqVo reqVo) {
        return writeSceneController.updateSceneControl(reqVo);
    }

    public void updateSceneControlCommon(SceneControlReqVo reqVo) {
        writeSceneService.updateSceneControlCommon(reqVo);
    }

    @PostMapping("/updateSceneControlToAll")
    public R<?> updateSceneControlToAll1(@RequestBody SceneControlReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeSceneController.updateSceneControlToAll(reqVo);
    }

    public R<?> updateSceneControlToAll(@RequestBody SceneControlReqVo reqVo) {
        return writeSceneController.updateSceneControlToAll(reqVo);
    }

    // ══════════════════════════════════════════════════════════
    // 传感器操作 → WriteDeviceController
    // ══════════════════════════════════════════════════════════

    @PostMapping("/updateInfraredParams")
    public R<?> updateInfraredParams1(@RequestBody InfraredParamsReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateInfraredParams(reqVo);
    }

    public R<?> updateInfraredParams(@RequestBody InfraredParamsReqVo reqVo) {
        // 旧签名接受 InfraredParamsReqVo，新签名接受 InfValue + deviceId
        // 构建 InfValue 后委托
        InfraredParamsTable table = reqVo.getTable();
        if (table.getInductiveLux() < 0 || table.getInductiveLux() > 100 || table.getUninductionLux() < 0 || table.getUninductionLux() > 100) {
            return R.warn("红外传感器亮度上下限值 0-100");
        }
        if (table.getDelayedTime() < 0 || table.getDelayedTime() > 120) return R.warn("红外传感器感应延迟上下限 0-120");

        InfValue v = new InfValue();
        table.setSensorId(reqVo.getSensorId());
        table.setUninductionSwitchStatus(table.getUninductionSwitchStatus() == 0 ? 1 : 0);
        table.setEnabled(reqVo.getEnabled() ? 1 : 0);
        v.setData(Collections.singletonList(table));
        Integer[] sendGroups = new Integer[16];
        for (int i = 1; i < 17; i++) {
            sendGroups[i - 1] = 0;
            for (Integer groupId : reqVo.getGroupIds()) {
                if (groupId == i) {
                    sendGroups[i - 1] = 1;
                    break;
                }
            }
        }
        v.setData1(sendGroups);
        v.setEnabled(reqVo.getEnabled());
        return writeDeviceController.updateInfraredParams(reqVo.getDeviceId(), v);
    }

    @PostMapping("/updateIlluminanceParams")
    public R<?> updateIlluminanceParams1(@RequestBody IlluminanceParamsReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateIlluminanceParams(reqVo);
    }

    public R<?> updateIlluminanceParams(@RequestBody IlluminanceParamsReqVo reqVo) {
        IlluminanceParamsTable table = reqVo.getTable();

        IllValue v = new IllValue();
        v.setEnabled(reqVo.getEnabled());
        ArrayList<Integer> ll = new ArrayList<>(Arrays.asList(reqVo.getGroupIds()));
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
        return writeDeviceController.updateIlluminanceParams(reqVo.getDeviceId(), v);
    }

    // ══════════════════════════════════════════════════════════
    // 设备设置 → WriteDeviceController
    // ══════════════════════════════════════════════════════════

    @PostMapping("/deviceSave")
    public R<?> deviceSave(@RequestBody DeviceSaveReqVo reqVo) {
        DevBaseDevice device = new DevBaseDevice();
        device.setId(Long.valueOf(reqVo.getDeviceId()));
        device.setDeviceName(reqVo.getDeviceName());
        device.setRegionId(Long.valueOf(reqVo.getZoneId()));
        return writeDeviceController.deviceSave(device);
    }

    @PostMapping("/communicateSave")
    public R<?> communicateSave(@RequestBody CommunicateSaveReqVo reqVo) {
        DevBaseDevice device = new DevBaseDevice();
        device.setId(Long.valueOf(reqVo.getDeviceNo()));
        device.setIp(reqVo.getIp());
        return writeDeviceController.communicateSave(device);
    }

    @PostMapping("/timeSave")
    public R<?> timeSave1(@RequestBody TimeSaveReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return timeSave(reqVo);
    }

    public R<?> timeSave(@RequestBody TimeSaveReqVo reqVo) {
        mqttPublisher.publish(reqVo.getDeviceId(), PublishKey.对时设置, new com.ruoyi.mqttwrite.time.TimeVO());
        return R.ok("启动对时");
    }

    public R<?> timeSaveNow(int deviceNo) {
        mqttPublisher.publish(deviceNo, PublishKey.对时设置, new com.ruoyi.mqttwrite.time.TimeVO());
        return R.ok("启动对时");
    }

    // ══════════════════════════════════════════════════════════
    // AC 控制 → WriteControlController
    // ══════════════════════════════════════════════════════════

    @PostMapping("/updateAcControl")
    public R<?> updateAcControl1(@RequestBody List<TimeControlAcRespVo> reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return updateAcControl(reqVo);
    }

    public R<?> updateAcControl(@RequestBody List<TimeControlAcRespVo> reqVo) {
        // 旧签名接受 List<TimeControlAcRespVo>，新签名接受 AcValue
        // 构建 AcValue 后委托
        AcValue v = new AcValue();
        List<DevConfigTimeControlAc> data = new java.util.LinkedList<>();
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
        v.setData(data);
        return writeControlController.updateAcControl(v);
    }

    // ══════════════════════════════════════════════════════════
    // 模块电压 → WriteControlController
    // ══════════════════════════════════════════════════════════

    @GetMapping("/updateAcDc")
    public R<?> updateAcDc1(Integer deviceId, BigDecimal value) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeControlController.updateAcDc(deviceId, value);
    }

    public R<?> updateAcDc(Integer deviceId, BigDecimal value) {
        return writeControlController.updateAcDc(deviceId, value);
    }

    @GetMapping("/updateDcDc")
    public R<?> updateDcDc1(Integer deviceId, BigDecimal value) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeControlController.updateDcDc(deviceId, value);
    }

    public R<?> updateDcDc(Integer deviceId, BigDecimal value) {
        return writeControlController.updateDcDc(deviceId, value);
    }

    // ══════════════════════════════════════════════════════════
    // 系统控制 → WriteModuleController
    // ══════════════════════════════════════════════════════════

    @GetMapping("/systemSwitch")
    public R<?> systemSwitch1(Integer deviceId, Integer systemSwitch) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeModuleController.systemSwitch(deviceId, systemSwitch);
    }

    public R<?> systemSwitch(Integer deviceId, Integer systemSwitch) {
        return writeModuleController.systemSwitch(deviceId, systemSwitch);
    }

    @GetMapping("/workModule")
    public R<?> workModule1(Integer deviceId, Integer workModule) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeModuleController.workModule(deviceId, workModule);
    }

    public R<?> workModule(Integer deviceId, Integer workModule) {
        return writeModuleController.workModule(deviceId, workModule);
    }

    // ══════════════════════════════════════════════════════════
    // 回路控制 → WriteControlController
    // ══════════════════════════════════════════════════════════

    @PostMapping("/loopControlLux")
    public R<?> loopControlLux1(@RequestBody WriteLoopReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return loopControlLux(reqVo);
    }

    public R<?> loopControlLux(@RequestBody WriteLoopReqVo reqVo) {
        LoopControlVO v = new LoopControlVO();
        v.setModule(2);
        LoopControlVO.D d = new LoopControlVO.D();
        d.setLux(reqVo.getLux());
        int[] loopSelectArr = new int[40];
        for (int i = 0; i < reqVo.getLoopControl().size(); i++) {
            loopSelectArr[i] = reqVo.getLoopControl().get(i);
        }
        d.setLoopArr(loopSelectArr);
        v.setData(d);
        return writeControlController.loopControlLux(reqVo.getDeviceId(), v);
    }

    @PostMapping("/loopControlSwitch")
    public R<?> loopControlSwitch1(@RequestBody WriteLoopReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return loopControlSwitch(reqVo);
    }

    public R<?> loopControlSwitch(@RequestBody WriteLoopReqVo reqVo) {
        LoopControlVO v = new LoopControlVO();
        v.setModule(1);
        LoopControlVO.D d = new LoopControlVO.D();
        d.setValue(reqVo.getSwitchStatus());
        int[] loopSelectArr = new int[40];
        for (int i = 0; i < reqVo.getLoopControl().size(); i++) {
            loopSelectArr[i] = reqVo.getLoopControl().get(i);
        }
        d.setLoopArr(loopSelectArr);
        v.setData(d);
        return writeControlController.loopControlSwitch(reqVo.getDeviceId(), v);
    }

    // ══════════════════════════════════════════════════════════
    // 分组控制 → WriteGroupController
    // ══════════════════════════════════════════════════════════

    @PostMapping("/groupControlLux")
    public R<?> groupControlLux1(@RequestBody WriteGroupReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeGroupController.groupControlLux(reqVo);
    }

    public R<?> groupControlLux(@RequestBody WriteGroupReqVo reqVo) {
        return writeGroupController.groupControlLux(reqVo);
    }

    @PostMapping("/groupControlSwitch")
    public R<?> groupControlSwitch1(@RequestBody WriteGroupReqVo reqVo) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeGroupController.groupControlSwitch(reqVo);
    }

    public R<?> groupControlSwitch(@RequestBody WriteGroupReqVo reqVo) {
        return writeGroupController.groupControlSwitch(reqVo);
    }

    // ══════════════════════════════════════════════════════════
    // 模式选择 → WriteModuleController
    // ══════════════════════════════════════════════════════════

    @GetMapping("/getModuleNow")
    public R<?> getModuleNow(@RequestParam(required = false) Integer deviceId) {
        return writeModuleController.getModuleNow(deviceId);
    }

    @GetMapping("/selectTimeModule")
    public R<?> selectTimeModule1(Integer deviceId, Integer timeModule) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeModuleController.selectTimeModule(deviceId, timeModule);
    }

    public R<?> selectTimeModule(Integer deviceId, Integer timeModule) {
        return writeModuleController.selectTimeModule(deviceId, timeModule);
    }

    @GetMapping("/selectInfraredModule")
    public R<?> selectInfraredModule1(Integer deviceId, Boolean enabled) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeModuleController.selectInfraredModule(deviceId, enabled);
    }

    public R<?> selectInfraredModule(Integer deviceId, Boolean enabled) {
        return writeModuleController.selectInfraredModule(deviceId, enabled);
    }

    @GetMapping("/selectIlluminanceModule")
    public R<?> selectIlluminanceModule1(Integer deviceId, Boolean enabled) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeModuleController.selectIlluminanceModule(deviceId, enabled);
    }

    public R<?> selectIlluminanceModule(Integer deviceId, Boolean enabled) {
        return writeModuleController.selectIlluminanceModule(deviceId, enabled);
    }

    @GetMapping("/selectHandModule")
    public R<?> selectHandModule1(Integer deviceId, Integer handModule) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeModuleController.selectHandModule(deviceId, handModule);
    }

    public R<?> selectHandModule(Integer deviceId, Integer handModule) {
        return writeModuleController.selectHandModule(deviceId, handModule);
    }

    // ══════════════════════════════════════════════════════════
    // 辅助方法
    // ══════════════════════════════════════════════════════════

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
