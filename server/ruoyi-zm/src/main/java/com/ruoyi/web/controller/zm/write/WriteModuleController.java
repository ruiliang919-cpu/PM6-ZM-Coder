package com.ruoyi.web.controller.zm.write;

import com.ruoyi.cache.Key;
import com.ruoyi.cache.ModuleGuard;
import com.ruoyi.cache.WriteQueueCache;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqtt03.addr03.handler.addr0XB715.Addr0XB715;
import com.ruoyi.mqttwrite.module.ModuleSelect;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/write/module")
public class WriteModuleController {

    private final Key key;
    private final WriteQueueCache writeQueueCache;
    private final MqttPublisher mqttPublisher;


    private final ModuleGuard moduleGuard;

    @GetMapping("/getModuleNow")
    public R<?> getModuleNow(@RequestParam(required = false) Integer deviceId) {
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        return R.ok(remote);
    }

    @GetMapping("/selectTimeModule")
    public R<?> selectTimeModule(Integer deviceId, Integer timeModule) {
        if (moduleGuard.isInRemoteMode()) return R.ok("遥控状态,请先取消");
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        if (timeModule == 1) remote.setTimeModule("noEnabled");
        else if (timeModule == 2) remote.setTimeModule("simple");
        else if (timeModule == 3) remote.setTimeModule("scene");
        writeQueueCache.setRemoteKey(key.getCreateTCP(deviceId).getIp(), deviceId, "0XB715", remote);
        ModuleSelect v = new ModuleSelect();
        v.setData(Collections.singletonList(remote));
        mqttPublisher.publish(deviceId, PublishKey.模式选择, v);
        return R.ok("指令已下发");
    }

    @GetMapping("/selectInfraredModule")
    public R<?> selectInfraredModule(Integer deviceId, Boolean enabled) {
        if (moduleGuard.isInRemoteMode()) return R.ok("遥控状态,请先取消");
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        remote.setInfraredSensorModule(enabled);
        writeQueueCache.setRemoteKey(key.getCreateTCP(deviceId).getIp(), deviceId, "0XB715", remote);
        ModuleSelect v = new ModuleSelect();
        v.setData(Collections.singletonList(remote));
        mqttPublisher.publish(deviceId, PublishKey.模式选择, v);
        return R.ok("指令已下发");
    }

    @GetMapping("/selectIlluminanceModule")
    public R<?> selectIlluminanceModule(Integer deviceId, Boolean enabled) {
        if (moduleGuard.isInRemoteMode()) return R.ok("遥控状态,请先取消");
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        remote.setIlluminanceSensorModule(enabled);
        writeQueueCache.setRemoteKey(key.getCreateTCP(deviceId).getIp(), deviceId, "0XB715", remote);
        ModuleSelect v = new ModuleSelect();
        v.setData(Collections.singletonList(remote));
        mqttPublisher.publish(deviceId, PublishKey.模式选择, v);
        return R.ok("指令已下发");
    }

    @GetMapping("/selectHandModule")
    public R<?> selectHandModule(Integer deviceId, Integer handModule) {
        if (moduleGuard.isInRemoteMode()) return R.ok("遥控状态,请先取消");
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

    @GetMapping("/systemSwitch")
    public R<?> systemSwitch(Integer deviceId, Integer systemSwitch) {
        if (moduleGuard.isInRemoteMode()) return R.ok("遥控状态,请先取消");
        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        m.put("addr", "0xC000");
        m.put("data", systemSwitch);
        mqttPublisher.publish(deviceId, PublishKey.系统总开关, m);
        return R.ok("指令下发成功");
    }

    @GetMapping("/workModule")
    public R<?> workModule(Integer deviceId, Integer workModule) {
        if (moduleGuard.isInRemoteMode()) return R.ok("遥控状态,请先取消");
        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        m.put("addr", "0xC001");
        m.put("data", workModule);
        mqttPublisher.publish(deviceId, PublishKey.工作模式, m);
        writeQueueCache.setWorkModule(deviceId, workModule);
        return R.ok("指令下发成功");
    }
}
