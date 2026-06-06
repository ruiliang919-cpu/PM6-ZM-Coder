package com.ruoyi.web.controller.zm.write;

import com.ruoyi.cache.Key;
import com.ruoyi.cache.ModuleGuard;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqttwrite.ill.IllValue;
import com.ruoyi.mqttwrite.inf.InfValue;
import com.ruoyi.mqttwrite.time.TimeVO;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevConfigIlluminanceSensor;
import com.ruoyi.zm.domain.DevConfigInfraredSensor;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.domain.vo.IlluminanceParamsTable;
import com.ruoyi.zm.domain.vo.InfraredParamsTable;
import com.ruoyi.zm.service.IDevBaseDeviceService;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.cache.WriteQueueCache;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/write/device")
public class WriteDeviceController {

    private final IDevBaseDeviceService deviceService;
    private final WriteQueueCache writeQueueCache;
    private final MqttPublisher mqttPublisher;
    private final Key key;

    private final ModuleGuard moduleGuard;

    @PostMapping("/deviceSave")
    public R<?> deviceSave(@RequestBody DevBaseDevice device) {
        int index = deviceService.updateById(device);
        return index > 0 ? R.ok("保存成功") : R.fail("保存失败");
    }

    @PostMapping("/communicateSave")
    public R<?> communicateSave(@RequestBody DevBaseDevice device) {
        writeQueueCache.deleteCreateTcp(device.getId());
        writeQueueCache.deleteDeviceList();
        int index = deviceService.updateById(device);
        return index > 0 ? R.ok("保存成功") : R.fail("保存失败");
    }

    @PostMapping("/timeSave")
    public R<?> timeSave(@RequestBody DevBaseDevice device) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");
        mqttPublisher.publish(Math.toIntExact(device.getDeviceNo()), PublishKey.对时设置, new TimeVO());
        return R.ok("启动对时");
    }

    @PostMapping("/updateInfraredParams")
    public R<?> updateInfraredParams(@RequestParam Integer deviceId, @RequestBody InfValue infValue) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");

        InfraredParamsTable table = infValue.getData().get(0);
        if (table.getInductiveLux() < 0 || table.getInductiveLux() > 100
                || table.getUninductionLux() < 0 || table.getUninductionLux() > 100) {
            return R.warn("红外传感器亮度上下限值 0-100");
        }
        if (table.getDelayedTime() < 0 || table.getDelayedTime() > 120)
            return R.warn("红外传感器感应延迟上下限 0-120");

        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        int[] a = new int[3];
        a[table.getSensorId() - 1] = 1;
        m.put("data", a);

        mqttPublisher.publish(deviceId, PublishKey.红外模式选择, m);
        mqttPublisher.publish(deviceId, PublishKey.红外模式, infValue);

        Map<String, Object> sensors = (HashMap<String, Object>) key.getRemote(deviceId, "0XAE7A");
        if (sensors == null) {
            sensors = new HashMap<>();
            sensors.put("data1", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            sensors.put("data2", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            sensors.put("data3", new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            List<DevConfigInfraredSensor> data = new LinkedList<>();
            for (int i = 0; i < 3; i++) {
                DevConfigInfraredSensor sensor = new DevConfigInfraredSensor();
                sensor.setDeviceId(deviceId);
                sensor.setSensorId(i + 1);
                data.add(sensor);
            }
            sensors.put("data", data);
        }

        Integer[] groups = (Integer[]) sensors.get("data" + table.getSensorId());
        List<DevConfigInfraredSensor> data = (List<DevConfigInfraredSensor>) sensors.get("data");

        int index = table.getSensorId() - 1;
        data.get(index).setInductiveLux(table.getInductiveLux());
        data.get(index).setInductiveSwitchStatus(table.getInductiveSwitchStatus() == 0 ? 1 : 0);
        data.get(index).setUninductionLux(table.getUninductionLux());
        data.get(index).setUninductionSwitchStatus(table.getUninductionSwitchStatus() == 1 ? 0 : 1);
        data.get(index).setDelayedTime(table.getDelayedTime());
        data.get(index).setEnabled(infValue.isEnabled() ? 1 : 0);

        java.util.Arrays.fill(groups, 0);

        for (int i = 0; i < 16; i++)
            for (int j = 0; j < infValue.getData1().length; j++)
                if (infValue.getData1()[j] == 1 && i + 1 == j + 1) {
                    groups[i] = 1;
                    break;
                }

        sensors.put("data" + table.getSensorId(), groups);
        sensors.put("data", data);

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        writeQueueCache.setQueueCache(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XAE7A", sensors);

        return R.ok("指令已下发");
    }

    @PostMapping("/updateIlluminanceParams")
    public R<?> updateIlluminanceParams(@RequestParam Integer deviceId, @RequestBody IllValue illValue) {
        if (moduleGuard.isInRemoteMode()) return R.warn("设备处于远程控制模式，不能下发指令");

        IlluminanceParamsTable table = illValue.getData().get(0);

        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        int[] a = new int[5];
        a[table.getSensorId() - 1] = 1;
        m.put("data", a);

        mqttPublisher.publish(deviceId, PublishKey.照度模式选择, m);
        mqttPublisher.publish(deviceId, PublishKey.照度模式, illValue);

        Map<String, Object> sensors = (HashMap<String, Object>) key.getRemote(deviceId, "0XAE8F");

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
                sensor.setDeviceId(deviceId);
                sensor.setSensorId(i + 1);
                data.add(sensor);
            }
            sensors.put("data", data);
        }

        Integer[] groups = (Integer[]) sensors.get("data" + table.getSensorId());
        List<DevConfigIlluminanceSensor> data = (List<DevConfigIlluminanceSensor>) sensors.get("data");

        int index = table.getSensorId() - 1;
        data.get(index).setIlluminanceLux(table.getIlluminanceLux());
        data.get(index).setConstantIlluminanceLux(table.getConstantIlluminanceLux());
        data.get(index).setHysteresisLux(table.getHysteresisLux());
        data.get(index).setAdjustmentTimeSeconds(table.getAdjustmentTimeSeconds());
        data.get(index).setOutControlStatus(table.getOutControlStatus() == 1 ? 0 : 1);
        data.get(index).setEnabled(illValue.isEnabled() ? 1 : 0);

        java.util.Arrays.fill(groups, 0);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < illValue.getData1().length; j++) {
                if (illValue.getData1()[j] == 1 && i + 1 == j + 1) {
                    groups[i] = 1;
                    break;
                }
            }
        }

        sensors.put("data" + table.getSensorId(), groups);
        sensors.put("data", data);

        DevBaseDeviceTCPVo tcpVo = key.getCreateTCP(deviceId);
        writeQueueCache.setQueueCache(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XAE8F", sensors);

        return R.ok("指令已下发");
    }

}
