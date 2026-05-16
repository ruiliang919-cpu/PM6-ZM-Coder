package com.ruoyi.zm.service.impl;

import com.ruoyi.zm.domain.DevConfigScene;
import com.ruoyi.zm.domain.DevConfigSimpleGroup;
import com.ruoyi.zm.domain.DevConfigTimeControl;
import com.ruoyi.zm.domain.DevInstruct;
import com.ruoyi.zm.mapper.DevConfigGroupMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.ruoyi.zm.utils.ScaleUtil.toAscII;
import static com.ruoyi.zm.utils.ScaleUtil.toIntArray;

// 处理系统设置-场景设置-场景？-分组亮度？的业务逻辑
@Service
@RequiredArgsConstructor
@Slf4j
public class SceneLuxConfigService {
    private final DevConfigGroupMapper devConfigGroupMapper;

    public List<DevConfigScene> disposeData(DevInstruct instruct, short[] data) {
        // 处理场景ID
        int sceneId = getSceneId(instruct.getAddr());
        // 取得当前data数组的最后一个地址
        int groupSelect = data[data.length - 1];
        // 将十进制的groupSelect转成二进制字符串
        // String groupSelectStr = Integer.toBinaryString(groupSelect);
        String groupSelectStr = String.format("%16s", Integer.toBinaryString(groupSelect)).replace(' ', '0');
        // log.error("groupSelectsStr"+groupSelectStr);
        // 二进制字符串转为数组
        int[] groupSelectArray = toIntArray(groupSelectStr);
        // System.out.println("groupSelectArray："+ Arrays.toString(groupSelectArray));
        // 返回的对象集合
        List<DevConfigScene> configSceneList = new LinkedList<>();
        // 处理ID信息
        for (int i = 0; i < 16; i++) {
            DevConfigScene configScene = new DevConfigScene();
            // 假设是第一台设备，表示第一台设备的第一个场景的第一个分组
            configScene.setId(Long.valueOf("" + instruct.getSalveId() + sceneId + i));
            configScene.setDeviceId(Long.valueOf(instruct.getSalveId()));
            configScene.setSceneId((long) sceneId);
            configScene.setGroupId((long) i);
            configSceneList.add(configScene);
        }
        // 处理分组信息
        for (int i = 0; i < 16; i++) {
            // 如果当前位置为1，则表示选中，否则表示未选中
            if (groupSelectArray[i] == 1) {
                configSceneList.get(i).setSelectStatus(1);
            } else {
                configSceneList.get(i).setSelectStatus(0);
            }
        }
        // 处理亮度信息
        short[] luxArray = Arrays.copyOfRange(data, 0, 16);
        for (int i = 0; i < 16; i++) {
            configSceneList.get(i).setLux((long) luxArray[i]);
        }
        // 处理开关信息
        short[] btnArray = Arrays.copyOfRange(data, 16, 32);
        for (int i = 0; i < 16; i++) {
            configSceneList.get(i).setBtnStatus((int) btnArray[i]);
        }
        return configSceneList;
    }

    private int getSceneId(String addr) {
        int result = 0;
        switch (addr) {
            case "0xAFC2":
                result = 1;
                break;
            case "0xAFE3":
                result = 2;
                break;
            case "0xB004":
                result = 3;
                break;
            case "0xB025":
                result = 4;
                break;
            case "0xB046":
                result = 5;
                break;
            case "0xB067":
                result = 6;
                break;
            case "0xB088":
                result = 7;
                break;
            case "0xB0A9":
                result = 8;
                break;
            case "0xB0CA":
                result = 9;
                break;
            case "0xB0EB":
                result = 10;
                break;
        }
        return result;
    }

    // 普通时控模式数据处理方法
    public Map<String, Object> disposeSimpleControlData(DevInstruct instruct, short[] data) {
        Map<String, Object> result = new HashMap<>();
        LinkedList<DevConfigTimeControl> list1 = new LinkedList<>();
        // 得到当前的普通时控模式的ID
        int controlId = 0;
        if (instruct.getAddr().equals("0XA80A")) controlId = 1;
        if (instruct.getAddr().equals("0XA853")) controlId = 2;
        if (instruct.getAddr().equals("0XA89C")) controlId = 3;
        // System.out.println("~~~~~~~~~~~controlId~~~~~~~~~~" + controlId);
        for (int i = 0; i < data.length - 1; i += 9) {
            DevConfigTimeControl control = new DevConfigTimeControl();
            // 主键
            control.setId(Long.valueOf(instruct.getSalveId() + "" + controlId + (i / 9 + 1)));
            // 设备编号
            control.setDeviceId(Long.valueOf(instruct.getSalveId()));
            // 时控编号
            control.setTimeControlId(controlId);
            // 时段信息编号
            control.setTimeFrameId(i / 9 + 1);
            // 亮度
            control.setLux((int) data[i]);
            // 开关状态
            control.setSwitchStatus((int) data[i + 1]);
            // 使能状态
            control.setEnabledStatus((int) data[i + 2]);
            // 开始时间
            control.setStime(toAscII(Arrays.copyOfRange(data, i + 3, i + 6)));
            // 结束时间
            control.setEtime(toAscII(Arrays.copyOfRange(data, i + 6, i + 9)));
            list1.add(control);
        }
        result.put("data1", list1);
        // System.out.println("~~~~~~~~~~~data1~~~~~~~~~~" + list1);

        // 最后一个数据：分组清况
        short lastValue = data[data.length - 1];
        String binaryString = String.format("%16s", Integer.toBinaryString(lastValue)).replace(' ', '0');
        int[] intArray = toIntArray(binaryString);
        // System.out.println("~~~~~~~~~~~~~Arrays.toString(intArray)~~~~~~~~~~~~~~~~~~"+Arrays.toString(intArray));
        LinkedList<DevConfigSimpleGroup> list2 = new LinkedList<>();
        for (int i = 0; i < intArray.length; i++) {
            DevConfigSimpleGroup group = new DevConfigSimpleGroup();
            group.setId(Integer.valueOf("" + instruct.getSalveId() + controlId + (i + 1)));
            group.setDeviceId(instruct.getSalveId());
            group.setSimpleId(controlId);
            group.setGroupId(i + 1);
            group.setSelectStatus(intArray[i]);
            list2.add(group);
        }

        result.put("data2", list2);
        // System.out.println("~~~~~~~~~~~data2~~~~~~~~~~" + list2);
        return result;
    }
}
