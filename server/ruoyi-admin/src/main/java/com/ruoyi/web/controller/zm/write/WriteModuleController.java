package com.ruoyi.web.controller.zm.write;

import com.ruoyi.cache.Key;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqtt03.addr03.handler.addr0XB715.Addr0XB715;
import com.ruoyi.mqttwrite.module.ModuleSelect;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.ruoyi.cache.Key.REMOTE_KEY;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/write/module")
public class WriteModuleController {

    private final Key key;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MqttPublisher mqttPublisher;

    @GetMapping("/getModuleNow")
    public R<?> getModuleNow(@RequestParam(required = false) Integer deviceId) {
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        return R.ok(remote);
    }

    @GetMapping("/selectTimeModule")
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

    @GetMapping("/selectInfraredModule")
    public R<?> selectInfraredModule(Integer deviceId, Boolean enabled) {
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        remote.setInfraredSensorModule(enabled);
        redisTemplate.opsForValue().set(REMOTE_KEY + key.getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + "0XB715", remote);
        ModuleSelect v = new ModuleSelect();
        v.setData(Collections.singletonList(remote));
        mqttPublisher.publish(deviceId, PublishKey.模式选择, v);
        return R.ok("指令已下发");
    }

    @GetMapping("/selectIlluminanceModule")
    public R<?> selectIlluminanceModule(Integer deviceId, Boolean enabled) {
        Addr0XB715.Data remote = (Addr0XB715.Data) key.getRemote(deviceId, "0XB715");
        remote.setIlluminanceSensorModule(enabled);
        redisTemplate.opsForValue().set(REMOTE_KEY + key.getCreateTCP(deviceId).getIp() + ":" + deviceId + ":" + "0XB715", remote);
        ModuleSelect v = new ModuleSelect();
        v.setData(Collections.singletonList(remote));
        mqttPublisher.publish(deviceId, PublishKey.模式选择, v);
        return R.ok("指令已下发");
    }

    @GetMapping("/selectHandModule")
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

    @GetMapping("/systemSwitch")
    public R<?> systemSwitch(Integer deviceId, Integer systemSwitch) {
        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        m.put("addr", "0xC000");
        m.put("data", systemSwitch);
        mqttPublisher.publish(deviceId, PublishKey.系统总开关, m);
        return R.ok("指令下发成功");
    }

    @GetMapping("/workModule")
    public R<?> workModule(Integer deviceId, Integer workModule) {
        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        m.put("addr", "0xC001");
        m.put("data", workModule);
        mqttPublisher.publish(deviceId, PublishKey.工作模式, m);
        redisTemplate.opsForHash().put("zm:workModule", String.valueOf(deviceId), workModule);
        return R.ok("指令下发成功");
    }
}
