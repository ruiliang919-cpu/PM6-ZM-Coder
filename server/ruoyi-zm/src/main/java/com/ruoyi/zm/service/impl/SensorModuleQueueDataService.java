package com.ruoyi.zm.service.impl;

import com.ruoyi.zm.domain.DevConfigIlluminanceSensor;
import com.ruoyi.zm.domain.DevConfigInfraredSensor;
import com.ruoyi.zm.domain.DevInstruct;
import com.ruoyi.zm.utils.ScaleUtil;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@Service
public class SensorModuleQueueDataService {
    // 红外传感模式数据处理
    public Map<String, Object> infraredSensorMode(DevInstruct instruct, short[] data) {
        Map<String, Object> result = new LinkedHashMap<>();
        LinkedList<DevConfigInfraredSensor> list = new LinkedList<>();
        for (int i = 0; i < data.length; i += 7) {
            DevConfigInfraredSensor sensor = new DevConfigInfraredSensor();
            sensor.setId(Integer.valueOf(instruct.getSalveId() + "" + (i / 7 + 1)));
            sensor.setDeviceId(instruct.getSalveId());
            sensor.setSensorId((i / 7 + 1));
            // 感应信息亮度
            sensor.setInductiveLux((int) data[i]);
            // 感应信息开关
            sensor.setInductiveSwitchStatus((int) data[i + 1]);
            // 未感应信息亮度
            sensor.setUninductionLux((int) data[i + 2]);
            // 未感应信息开关
            sensor.setUninductionSwitchStatus((int) data[i + 3]);
            // 延时
            sensor.setDelayedTime((int) data[i + 4]);
            // 分组信息，红外传感器有三个
            String key = "data" + (i / 7 + 1);
            result.put(key, processGroupData(data[i + 5]));
            // System.out.println("分组信息：" + key + ":" + data[i + 5]);
            // 启用
            sensor.setEnabled((int) data[i + 6]);
            list.add(sensor);
        }

        result.put("data", list);

        // System.out.println("红外传感模式数据处理："+ result);
        return result;
    }

    // 照度传感模式数据处理
    public Map<String, Object> illuminanceSensorMode(DevInstruct instruct, short[] data) {
        Map<String, Object> result = new LinkedHashMap<>();
        LinkedList<DevConfigIlluminanceSensor> list = new LinkedList<>();
        for (int i = 0; i < data.length; i += 7) {
            DevConfigIlluminanceSensor sensor = new DevConfigIlluminanceSensor();
            sensor.setId(Integer.valueOf(instruct.getSalveId() + "" + (i / 7 + 1)));
            sensor.setDeviceId(instruct.getSalveId());
            sensor.setSensorId((i / 7 + 1));
            // 照度值 单位Lux
            sensor.setIlluminanceLux((int) data[i]);
            // 恒照值 单位Lux
            sensor.setConstantIlluminanceLux((int) data[i + 1]);
            // 回差值 单位Lux
            sensor.setHysteresisLux((int) data[i + 2]);
            // 调节时间 单位S
            sensor.setAdjustmentTimeSeconds((int) data[i + 3]);
            // 外控使能 0-不启用外控/1-启用外控
            sensor.setOutControlStatus((int) data[i + 4]);
            // 分组信息，照度传感器有五个
            String key = "data" + (i / 7 + 1);
            result.put(key, processGroupData(data[i + 5]));
            // 启用 0-未启用/1-启用
            sensor.setEnabled((int) data[i + 6]);
            list.add(sensor);
        }

        result.put("data", list);
        // System.out.println("照度传感模式数据处理："+ result);
        return result;
    }

    // 处理分组信息数据
    public int[] processGroupData(int data) {
        String binaryString = String.format("%16s", Integer.toBinaryString(data)).replace(' ', '0');
        ;
        // System.out.println("~~~~~~~~~~~~~~~~binaryString~~~~~~~~~~~~~~~~~~"+binaryString);
        return ScaleUtil.toIntArray(binaryString);
    }
}
