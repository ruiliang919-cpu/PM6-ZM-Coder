package com.ruoyi.web.controller.zm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.cache.DeviceProcessorCache;
import com.ruoyi.cache.GroupCache;
import com.ruoyi.cache.Key;
import com.ruoyi.cache.SceneCache;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.zm.domain.DevBaseDevice;
import com.ruoyi.zm.domain.DevDeviceRemoteRead;
import com.ruoyi.zm.domain.vo.*;
import com.ruoyi.zm.mapper.DevBaseDeviceMapper;
import com.ruoyi.zm.mapper.DevDeviceRemoteReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/process")
public class DeviceProcessorController {
    private final DevBaseDeviceMapper deviceMapper;
    private final DevDeviceRemoteReadMapper remoteReadMapper;
    private final DeviceProcessorCache cache;
    private final SceneCache sceneCache;
    private final GroupCache groupCache;
    private final Key key;
    private final RedisTemplate<String, int[]> intArrayRedisTemplate;

    // 机柜控制-照明控制 总开关、工作模式、场景控制列表、回路控制列表、分组控制列表
    @GetMapping("/lightControlList")
    public R<TreeMap<String, Object>> getLightControl(Integer deviceId) {
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            getSystemControl(deviceId),
            getSceneControl(deviceId),
            getLoopControl(deviceId),
            getGroupControl(deviceId),
            getLux(deviceId)
        );

        TreeMap<String, Object> result = new TreeMap<>();

        CompletableFuture<R<TreeMap<String, Object>>> combinedFuture =
            allFutures.thenCompose(unused -> CompletableFuture.supplyAsync(() -> {
                result.put("systemControl", getSystemControl(deviceId).join());
                result.put("sceneControl", getSceneControl(deviceId).join());
                result.put("loopControl", getLoopControl(deviceId).join());
                result.put("groupControl", getGroupControl(deviceId).join());
                List<Integer> luxArr = getLux(deviceId).join();
                result.put("loopLux", luxArr.get(0));
                result.put("groupLux", luxArr.get(1));
                return R.ok(result);
            }));

        return combinedFuture.join();
    }


    // 机柜控制-模块控制 ACDC/DCDC输出电压
    @GetMapping("/moduleControlList")
    public R<ModuleControlRespVo> moduleControlList(Integer deviceId) {
        short[] arr = key.getRemoteByArr(deviceId, "0xA007");
        if (arr[0] == -1) {
            arr = new short[]{0, 0};
        }
        ModuleControlRespVo vo = new ModuleControlRespVo();
        vo.setAcDcVoltage(new BigDecimal(arr[0]).divide(new BigDecimal(10)).setScale(1));
        vo.setDcDcVoltage(new BigDecimal(arr[1]).divide(new BigDecimal(10)).setScale(1));
        return R.ok(vo);
    }

    // 总开关、工作模式
    CompletableFuture<SystemControlRespVo> getSystemControl(Integer deviceId) {
        CompletableFuture<SystemControlRespVo> systemControl = cache.getSystemControl(deviceId);
        if (systemControl != null) return systemControl;
        return CompletableFuture.supplyAsync(() -> {
            SystemControlRespVo vo = new SystemControlRespVo();
//            LambdaQueryWrapper<DevBaseDevice> lqw = new LambdaQueryWrapper<>();
//            lqw.eq(DevBaseDevice::getId, deviceId);
//            DevBaseDevice source = deviceMapper.selectOne(lqw);
//            if (source != null && source.getRunStatus() != null) {
//                vo.setSystemSwitch(source.getRunStatus());
//            }
//            if (source != null && source.getRunMode() != null) {
//                vo.setWorkMode(source.getRunMode());
//            }
            return vo;
        });
    }

    // 场景控制列表
    CompletableFuture<List<SceneControlRespVo>> getSceneControl(Integer deviceId) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> sceneNamesList = sceneCache.getSceneNamesList(deviceId);
            List<SceneControlRespVo> result = new LinkedList<>();
            if (sceneNamesList != null && !sceneNamesList.isEmpty()) {
                for (int i = 0; i < 10; i++) {
                    SceneControlRespVo vo = new SceneControlRespVo();
                    vo.setSceneId(i + 1);
                    vo.setSceneName(sceneNamesList.get(i));
                    result.add(vo);
                }
            }
            LambdaQueryWrapper<DevBaseDevice> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DevBaseDevice::getDeviceNo, deviceId);
            DevBaseDevice devBaseDevice = deviceMapper.selectOne(lqw);
            if (devBaseDevice != null && devBaseDevice.getSceneSelect() != null) {
                for (SceneControlRespVo sceneControlRespVo : result) {
                    if (Objects.equals(sceneControlRespVo.getSceneId(), devBaseDevice.getSceneSelect())) {
                        sceneControlRespVo.setSceneSelect(1);
                    } else {
                        sceneControlRespVo.setSceneSelect(0);
                    }
                }
            } else {
                result.forEach(item -> {
                    item.setSceneSelect(0);
                });
            }
            return result;
        });
    }

    // 回路控制列表
    CompletableFuture<List<LoopControlRespVo>> getLoopControl(Integer deviceId) {
        return CompletableFuture.supplyAsync(() -> {
            // 查询所有回路
            List<LoopControlRespVo> result = new ArrayList<>();
            int[] selectArr = intArrayRedisTemplate.opsForValue().get("zm:select:loop:" + deviceId);
            boolean[] switchArr = key.getTelecommand(deviceId, 474, 513);
            List<BigDecimal> luxList = key.getTelemeterBigDecimalList(deviceId, "0X04C8");
            for (int i = 0; i < 40; i++) {
                LoopControlRespVo vo = new LoopControlRespVo();
                vo.setName("回路" + (i + 1));
                vo.setNo(i + 1);

                try {
                    if (selectArr != null) {
                        vo.setSelectStatus(selectArr[i] == 1);
                    }
                } catch (Exception e) {
                    vo.setSelectStatus(false);
                }

                try {
                    if (switchArr != null) {
                        vo.setSwitchStatus(switchArr[i] ? 1 : 0);
                    }
                } catch (Exception e) {
                    vo.setSwitchStatus(0);
                }

                try {
                    if (luxList != null) {
                        vo.setLux(luxList.get(i).intValue());
                    }
                } catch (Exception e) {
                    vo.setLux(0);
                }

                result.add(vo);
            }
            return result;
        });
    }

    // 分组控制列表
    CompletableFuture<List<GroupControlRespVo>> getGroupControl(Integer deviceId) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> groupedNames = groupCache.groupNames(deviceId);
            int[] selectArr = intArrayRedisTemplate.opsForValue().get("zm:select:group:" + deviceId);
            List<GroupControlRespVo> result = new ArrayList<>();
            for (int i = 0; i < 16; i++) {
                GroupControlRespVo vo = new GroupControlRespVo();
                vo.setId(i + 1);
                vo.setName(groupedNames.get(i) != null
                    && !"".equals(groupedNames.get(i)) ? groupedNames.get(i) : "分组" + (i + 1));
                try {
                    if (selectArr != null) {
                        vo.setSelectStatus(selectArr[i] == 1);
                    }
                } catch (Exception e) {
                    vo.setSelectStatus(false);
                }
                vo.setLux(100);
                result.add(vo);
            }
            return result;
        });
    }

    // 回路亮度与分组亮度
    CompletableFuture<List<Integer>> getLux(Integer deviceId) {
        return CompletableFuture.supplyAsync(() -> {
            LambdaQueryWrapper<DevDeviceRemoteRead> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DevDeviceRemoteRead::getId, deviceId);
            DevDeviceRemoteRead devDeviceRemoteRead = remoteReadMapper.selectOne(lqw);
            List<Integer> result = new LinkedList<>();
            if (devDeviceRemoteRead == null || devDeviceRemoteRead.getLoopLux() == null || devDeviceRemoteRead.getGroupLux() == null) {
                result.add(0);
                result.add(0);
                return result;
            }
            result.add(devDeviceRemoteRead.getLoopLux());
            result.add(devDeviceRemoteRead.getGroupLux());
            return result;
        });
    }

}
