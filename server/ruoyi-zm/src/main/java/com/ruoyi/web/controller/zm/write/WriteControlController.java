package com.ruoyi.web.controller.zm.write;

import com.ruoyi.cache.ModuleGuard;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.mqttwrite.accontrol.AcValue;
import com.ruoyi.mqttwrite.loopcontrol.LoopControlVO;
import com.ruoyi.mqttwrite.simple.SimpleValue;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.service.IDevBaseDeviceService;
import com.ruoyi.zm.service.WriteControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/write/control")
public class WriteControlController {

    private final WriteControlService writeControlService;
    private final IDevBaseDeviceService deviceService;
    private final ModuleGuard moduleGuard;

    @PostMapping("/updateSimpleControl")
    public R<?> updateSimpleControl(@RequestParam Integer deviceId, @RequestBody SimpleValue simpleValue) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeControlService.updateSimpleControl(simpleValue, deviceId);
    }

    @PostMapping("/updateSimpleControlToAll")
    public R<?> updateSimpleControlToAll(@RequestBody SimpleValue simpleValue) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");

        for (int i = 0; i < simpleValue.getData1().size(); i++) {
            if (simpleValue.getData1().get(i).getLux() < 0 || simpleValue.getData1().get(i).getLux() > 100) {
                return R.warn("普通时控亮度上下限值 0-100");
            }
        }

        java.util.List<DevBaseDevice> devices = deviceService.listAll();
        for (DevBaseDevice device : devices) {
            try {
                writeControlService.updateSimpleControlCommon(simpleValue, Math.toIntExact(device.getDeviceNo()));
            } catch (Exception e) {
                log.error("Error in updateSimpleControlToAll for device: {}", device.getDeviceNo(), e);
            }
        }
        return R.ok("指令已下发");
    }

    @GetMapping("/updateZoneLightSwitch")
    public R<?> updateZoneLightSwitch(@RequestParam String type, @RequestParam Integer value) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        Integer zoneId = Integer.parseInt(type);
        Integer swStatus = (value == 1 ? 0 : 1);
        return writeControlService.updateZoneLightSwitchInternal(zoneId, swStatus);
    }

    @GetMapping("/updateZoneLightLux")
    public R<?> updateZoneLightLux(@RequestParam String type, @RequestParam Integer value) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        Integer zoneId = Integer.parseInt(type);
        return writeControlService.updateZoneLightLuxInternal(zoneId, value);
    }

    @PostMapping("/updateAcControl")
    public R<?> updateAcControl(@RequestBody AcValue acValue) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeControlService.updateAcControl(acValue);
    }

    @GetMapping("/updateAcDc")
    public R<?> updateAcDc(@RequestParam Integer deviceId, @RequestParam BigDecimal value) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeControlService.updateAcDc(deviceId, value);
    }

    @GetMapping("/updateDcDc")
    public R<?> updateDcDc(@RequestParam Integer deviceId, @RequestParam BigDecimal value) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeControlService.updateDcDc(deviceId, value);
    }

    @PostMapping("/loopControlLux")
    public R<?> loopControlLux(@RequestParam Integer deviceId, @RequestBody LoopControlVO loopControlVO) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeControlService.loopControlLux(deviceId, loopControlVO);
    }

    @PostMapping("/loopControlSwitch")
    public R<?> loopControlSwitch(@RequestParam Integer deviceId, @RequestBody LoopControlVO loopControlVO) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        return writeControlService.loopControlSwitch(deviceId, loopControlVO);
    }
}
