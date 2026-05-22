package com.ruoyi.schedule;

import com.ruoyi.cache.*;
import com.ruoyi.send.WriteSendService;
import com.ruoyi.web.controller.zm.WriteController;
import com.ruoyi.zm.domain.*;
import com.ruoyi.zm.domain.vo.*;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.utils.ScaleUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ruoyi.cache.GroupCache.GROUP_ADDR_ARR;
import static com.ruoyi.cache.GroupCache.GROUP_NO_ARR;
import static com.ruoyi.cache.SceneCache.SCENE_PARAMS_ADDR_ARR;

@Component
@RequiredArgsConstructor
public class Recover {
    private final Key key;
    private final WriteSendService send;
    private final WriteController write;
    private final DevBaseDeviceMapper deviceMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    // @Scheduled(fixedDelay = 500)
    public void init() {
        List<DevBaseDevice> devices = deviceMapper.selectList();
        if (devices != null) {
            for (DevBaseDevice base : devices) {
                DevBaseDeviceTCPVo device = (DevBaseDeviceTCPVo) redisTemplate.opsForValue().get("zm:create-tcp:" + base.getDeviceNo());
                if (device != null && device.getLastTime() != null) {
                    try {
                        device.setLastTime(null);
                        redisTemplate.opsForValue().set("zm:create-tcp:" + device.getId(), device, 1, TimeUnit.DAYS);
                        recover(device);
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
            }
        }
    }

    public void recover(DevBaseDeviceTCPVo device) {
        {
            // lock.lock();
            try {
                // 处理时间
                LocalDateTime now = LocalDateTime.now();
                int[] saveArr = {now.getYear(),
                    now.getMonth().getValue(),
                    now.getDayOfMonth(),
                    now.getHour(),
                    now.getMinute(),
                    now.getSecond(),
                    256};
                send.writeToAddr(Math.toIntExact(device.getId()), 6, "时间设置-年", 7, saveArr);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        {
            // lock.lock();
            try {
                // 处理电压
                short[] arr = key.getRemoteByArr(Math.toIntExact(device.getId()), "0xA007");
                if (arr[0] != -1) {
                    send.writeToAddr(Math.toIntExact(device.getId()), 6, "AC/DC模块输出电压", 1, arr);
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        {
            // lock.lock();
            try {
                // 处理分组名称
                for (int index = 0; index < GROUP_ADDR_ARR.length; index++) {
                    short[] arr = key.getRemoteByArr(Math.toIntExact(device.getId()), GROUP_ADDR_ARR[index]);
                    if (arr[0] != -1) {
                        send.writeToAddr(Math.toIntExact(device.getId()), 6, GroupCache.GROUP_NAME_ADDR[index], 10, arr);
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        {
            // lock.lock();
            try {

                // 处理分区码
                short[] arr = key.getRemoteByArr(Math.toIntExact(device.getId()), "0xA5AE");
                if (arr[0] != -1) {
                    send.writeToAddr(Math.toIntExact(device.getId()), 6, "系统设置-回路分组-分区编号01", 16, arr);
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        {
            // lock.lock();
            try {
                // 处理分组回路
                for (int i = 0; i < 16; i++) {
                    String loopNoStr = (String) key.getTelemeter(Math.toIntExact(device.getId()), GROUP_NO_ARR[i]);
                    if (loopNoStr != null && !loopNoStr.isEmpty()) {
                        Integer[] loopNos = ScaleUtil.splitIntoIntPairs(loopNoStr);
                        // System.err.println(Arrays.toString(loopNos));
                        UpdateLoopReqVo vo = new UpdateLoopReqVo();
                        vo.setGroupId(i + 1);
                        vo.setDeviceId(Math.toIntExact(device.getId()));
                        vo.setLoopNo(loopNos);
                        write.updateLoop(vo);
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        {
            // lock.lock();
            try {

                // 场景名称
                for (int index = 0; index < SceneCache.SCENE_NAME_ADDR_ARR.length; index++) {
                    short[] arr = key.getRemoteByArr(Math.toIntExact(device.getId()), SceneCache.SCENE_NAME_ADDR_ARR[index]);
                    if (arr[0] != -1) {
                        send.writeToAddr(Math.toIntExact(device.getId()), 6, SceneCache.SCENE_NAME_ARR[index], 10, arr);
                        send.writeToAddr(Math.toIntExact(device.getId()), 5, "场景时控保存标志位", 1, new short[]{1});
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        {
            // lock.lock();
            try {

                // 普通时控
                for (int i = 0; i < SimpleControlCache.SIMPLE_LIST_ADDR_ARR.length; i++) {
                    Map<String, Object> map = (Map<String, Object>) key.getRemote(Math.toIntExact(device.getId()), SimpleControlCache.SIMPLE_LIST_ADDR_ARR[i]);
                    if (map != null) {
                        try {
                            List<DevConfigTimeControl> data1 = (List<DevConfigTimeControl>) map.get("data1");
                            List<DevConfigSimpleGroup> data2 = (List<DevConfigSimpleGroup>) map.get("data2");
                            SimpleControlReqVo vo = new SimpleControlReqVo();
                            vo.setDeviceId(Math.toIntExact(device.getId()));
                            vo.setControlId(i + 1);
                            short[] enabled = key.getRemoteByArr(Math.toIntExact(1), "0XA8E6");
                            vo.setEnabled(enabled[0] == (i + 1));
                            Integer[] groups = data2.stream().filter(it -> it.getSelectStatus() == 1)
                                .map(DevConfigSimpleGroup::getGroupId).toArray(Integer[]::new);
                            vo.setGroupIds(groups);
                            int finalIndex = i;
                            List<SimpleControlTable> table = data1.stream().map(it -> new SimpleControlTable(
                                (finalIndex + 1), it.getTimeFrameId(), it.getLux(), it.getSwitchStatus(), it.getEnabledStatus(), it.getStime(), it.getEtime()
                            )).collect(Collectors.toList());
                            vo.setTable(table);
                            write.updateSimpleControl(vo);
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        {
            // lock.lock();
            try {
                // 场景时控
                for (int index = 0; index < SceneCache.ADDR_SCENE.length; index++) {
                    List<DevConfigTimeControlScene> scenes = (List<DevConfigTimeControlScene>) key.getRemote(Math.toIntExact(1), SceneCache.ADDR_SCENE[index]);
                    if (scenes != null) {
                        try {
                            SceneControlReqVo vo = new SceneControlReqVo();
                            vo.setDeviceId(Math.toIntExact(device.getId()));
                            vo.setControlId(index + 1);
                            short[] enabled = key.getRemoteByArr(Math.toIntExact(device.getId()), "0XABAF");
                            vo.setEnabled(enabled[0] == (index + 1));
                            List<SceneControlTable> table = scenes.stream().map(it -> new SceneControlTable(
                                it.getTimeFrameId(), it.getEnabledStatus(), it.getSceneSelect(), it.getStime(), it.getEtime()
                            )).collect(Collectors.toList());
                            vo.setTable(table);
                            // System.out.println(vo);
                            write.updateSceneControl(vo);
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        {
            // lock.lock();
            try {
                // 红外模式
                Map<String, Object> map = (Map<String, Object>) key.getRemote(Math.toIntExact(device.getId()), InfraredCache.ADDR[0]);
                if (map != null) {
                    try {
                        List<DevConfigInfraredSensor> data = (List<DevConfigInfraredSensor>) map.get("data");
                        for (int i = 0; i < 3; i++) {
                            InfraredParamsReqVo vo = new InfraredParamsReqVo();
                            vo.setDeviceId(Math.toIntExact(device.getId()));
                            vo.setEnabled(data.get(i).getEnabled() == 1);
                            vo.setSensorId(i + 1);
                            List<Integer> group = new ArrayList<>();
                            for (int index = 0; index < ((int[]) map.get("data" + (i + 1))).length; index++) {
                                if (((int[]) map.get("data" + (i + 1)))[index] == 1) {
                                    group.add(index + 1);
                                }
                            }
                            vo.setGroupIds(group.toArray(new Integer[0]));
                            List<InfraredParamsTable> table = data.stream().map(it -> new InfraredParamsTable(
                                null, it.getEnabled(), it.getInductiveLux(), it.getInductiveSwitchStatus(), it.getUninductionLux(),
                                it.getUninductionSwitchStatus(), it.getDelayedTime()
                            )).collect(Collectors.toList());
                            vo.setTable(table.get(i));
                            write.updateInfraredParams(vo);
                        }
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        {
            // lock.lock();
            try {
                // 照度传感器
                Map<String, Object> map = (Map<String, Object>) key.getRemote(Math.toIntExact(device.getId()), "0XAE8F");
                if (map != null) {
                    try {
                        List<DevConfigIlluminanceSensor> data = (List<DevConfigIlluminanceSensor>) map.get("data");
                        for (int i = 0; i < 5; i++) {
                            IlluminanceParamsReqVo vo = new IlluminanceParamsReqVo();
                            vo.setDeviceId(Math.toIntExact(device.getId()));
                            vo.setEnabled(data.get(i).getEnabled() == 1);
                            vo.setSensorId(i + 1);
                            List<Integer> group = new ArrayList<>();
                            for (int index = 0; index < ((int[]) map.get("data" + (i + 1))).length; index++) {
                                if (((int[]) map.get("data" + (i + 1)))[index] == 1) {
                                    group.add(index + 1);
                                }
                            }
                            vo.setGroupIds(group.toArray(new Integer[0]));
                            List<IlluminanceParamsTable> table = data.stream().map(it -> new IlluminanceParamsTable(
                                null, it.getIlluminanceLux(), it.getConstantIlluminanceLux(), it.getHysteresisLux(), it.getAdjustmentTimeSeconds(), it.getOutControlStatus(), null,
                                it.getOutControlStatus() == 1 ? 0 : 1, 0, 0
                            )).collect(Collectors.toList());
                            vo.setTable(table.get(i));
                            write.updateIlluminanceParams(vo);
                        }
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        {
            // lock.lock();
            try {
                // 交流开关
                Map<String, Object> map = (Map<String, Object>) key.getRemote(Math.toIntExact(device.getId()), "0XAF1E");
                if (map != null) {
                    List<DevConfigTimeControlAc> controlAc = (List<DevConfigTimeControlAc>) map.get("controlAc");
                    if (controlAc != null) {
                        for (int i = 0; i < controlAc.size(); i++) {
                            DevConfigTimeControlAc it = controlAc.get(i);
                            send.writeToAddr(Math.toIntExact(device.getId()), 6, AcSwitchCache.arr[i], 6, new short[]{
                                it.getSwitchStatus().shortValue(), it.getEnable().shortValue(), it.getStimeHour().shortValue(),
                                it.getStimeMin().shortValue(), it.getEtimeHour().shortValue(), it.getEtimeMin().shortValue()
                            });
                            send.writeToAddr(Math.toIntExact(device.getId()), 5, "调光信息-交流控制-手动开关标志0" + (i + 1), 1, new short[]{1});
                            send.writeToAddr(Math.toIntExact(device.getId()), 5, "交流时控保存标志位", 1, new short[]{1});
                        }
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        {
            // 场景参数
            try {
                for (int index = 0; index < SCENE_PARAMS_ADDR_ARR.length; index++) {
                    List<DevConfigScene> configS = (List<DevConfigScene>) key.getRemote(Math.toIntExact(device.getId()), SCENE_PARAMS_ADDR_ARR[index]);
                    SceneParamsReqVo vo = new SceneParamsReqVo();
                    vo.setDeviceId(Math.toIntExact(device.getId()));
                    vo.setSceneId(index + 1);
                    List<Integer> groupS = new ArrayList<>();
                    List<Integer> luxS = new ArrayList<>();
                    List<Integer> switchS = new ArrayList<>();
                    for (int i = 0; i < 16; i++) {
                        DevConfigScene configScene = configS.get(i);
                        if (1 == configScene.getSelectStatus()) {
                            groupS.add(i + 1);
                        }
                        luxS.add(Math.toIntExact(configScene.getLux()));
                        switchS.add(configScene.getBtnStatus());
                    }
                    vo.setSelectGroupIdArr(groupS.toArray(new Integer[0]));
                    // System.err.println(Arrays.toString(groupS.toArray(new Integer[0])));
                    vo.setSelectLuxArr(luxS.toArray(new Integer[0]));
                    vo.setSelectSwitchArr(switchS.toArray(new Integer[0]));
                    write.updateSceneParams(vo);
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        {
            // lock.lock();
            try {
                // 系统设置-控制方式-模式选择，系统设置-控制方式-红外传感模式，
                // 系统设置-控制方式-照度传感模式，系统控制-照明控制-手动模式选择
                short[] arr = key.getRemoteByArr(Math.toIntExact(1), "0XB715");
                // System.err.println(Arrays.toString(arr));
                if (arr[0] != -1) {
                    write.selectTimeModule(Math.toIntExact(device.getId()), (int) arr[0]);
                    write.selectInfraredModule(Math.toIntExact(device.getId()), (int) arr[1] == 1);
                    write.selectIlluminanceModule(Math.toIntExact(device.getId()), (int) arr[2] == 1);
                    write.selectHandModule(Math.toIntExact(device.getId()), (int) arr[3]);
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }


}
