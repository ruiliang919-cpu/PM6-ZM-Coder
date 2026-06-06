package com.ruoyi.zm.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.Key;
import com.ruoyi.cache.SceneControlCache;
import com.ruoyi.cache.WriteQueueCache;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqtt03.addr03.handler.addr0XB715.Addr0XB715;
import com.ruoyi.mqttwrite.module.ModuleSelect;
import com.ruoyi.zm.domain.*;
import com.ruoyi.zm.domain.vo.DevBaseDeviceTCPVo;
import com.ruoyi.zm.domain.vo.SceneControlReqVo;
import com.ruoyi.zm.domain.vo.SceneControlTable;
import com.ruoyi.zm.utils.IdGenerator;
import com.ruoyi.zm.utils.ScaleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WriteSceneService {

    private final MqttPublisher mqttPublisher;
    private final Key key;
    private final WriteQueueCache writeQueueCache;
    private final IDevBaseDeviceService deviceService;

    private static final String[] ADDR_SCENE = {"", "0XAAEE", "0XAB2E", "0XAB6E"};

    public R<?> workModule(Integer deviceId, Integer workModule) {
        Map<String, Object> m = new HashMap<>();
        m.put("msgId", IdGenerator.UUIDId());
        m.put("addr", "0xC001");
        m.put("data", workModule);
        mqttPublisher.publish(deviceId, PublishKey.工作模式, m);
        writeQueueCache.setWorkModule(deviceId, workModule);
        return R.ok("指令下发成功");
    }

    public R<?> selectHandModule(Integer deviceId, Integer handModule) {
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
        writeQueueCache.pushInstruction(reqVo.getDeviceId(), instruct);


        if (reqVo.getEnabled() == true) {
            data[1] = (short) ((int) reqVo.getControlId());
            writeQueueCache.setQueueCacheShortArr(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XABAF", new short[]{(short) data[1]});

            instruct.setId(IdGenerator.UUIDId());
            instruct.setAddr("0XABAE");
            instruct.setAddrNum(1);
            instruct.setWriteValue(new JSONObject().set("arr", new short[]{data[0]}).toString());
            writeQueueCache.pushInstruction(reqVo.getDeviceId(), instruct);

            instruct.setId(IdGenerator.UUIDId());
            instruct.setAddr("0XABAF");
            instruct.setAddrNum(1);
            instruct.setWriteValue(new JSONObject().set("arr", new short[]{data[1]}).toString());
            writeQueueCache.pushInstruction(reqVo.getDeviceId(), instruct);
        } else {
            short[] value = writeQueueCache.getQueueCacheShortArr(tcpVo.getIp(), Math.toIntExact(tcpVo.getId()), "0XABAF");

            if (value != null && value.length > 0) {
                instruct.setId(IdGenerator.UUIDId());
                instruct.setAddr("0XABAE");
                instruct.setAddrNum(1);
                instruct.setWriteValue(new JSONObject().set("arr", new short[]{(short) (reqVo.getControlId() - 1)}).toString());
                writeQueueCache.pushInstruction(reqVo.getDeviceId(), instruct);

                instruct.setId(IdGenerator.UUIDId());
                instruct.setAddr("0XABAF");
                instruct.setAddrNum(1);
                instruct.setWriteValue(new JSONObject().set("arr", new short[]{value[0]}).toString());
                writeQueueCache.pushInstruction(reqVo.getDeviceId(), instruct);
            }
        }

        short[] saveArr = {1};

        instruct.setCode(5);
        instruct.setId(IdGenerator.UUIDId());
        instruct.setAddr("0xC2CA");
        instruct.setAddrNum(1);
        instruct.setWriteValue(new JSONObject().set("arr", saveArr).toString());
        writeQueueCache.pushInstruction(reqVo.getDeviceId(), instruct);
    }

    public R<?> intoScene(Integer deviceId, Integer sceneId) {
        LambdaQueryWrapper<DevBaseDevice> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevBaseDevice::getDeviceNo, deviceId).last("LIMIT 1");
        DevBaseDevice device = deviceService.selectOne(lqw);
        device.setId(device.getId());
        if (key.getTelecommand(deviceId, 164) == 0)
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
        deviceService.updateById(device);
        return R.ok("操作成功");
    }

    public R<?> intoSceneNotRecord(Integer deviceId, Integer sceneId) {
        LambdaQueryWrapper<DevBaseDevice> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DevBaseDevice::getDeviceNo, deviceId).last("LIMIT 1");
        DevBaseDevice device = deviceService.selectOne(lqw);
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
        deviceService.updateById(device);
        return R.ok("操作成功");
    }
}
