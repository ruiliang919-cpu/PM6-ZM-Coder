package com.ruoyi.web.controller.zm;


import com.ruoyi.cache.IllUtilCache;
import com.ruoyi.cache.IlluminanceCache;
import com.ruoyi.cache.InfraredCache;
import com.ruoyi.cache.Key;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.vo.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

// 传感模式控制层接口
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/sensorModule")
public class SensorModuleController {
    private static final Logger log = LoggerFactory.getLogger(SensorModuleController.class);

    private final InfraredCache infraredCache;
    private final IlluminanceCache illuminanceCache;
    private final Key key;
    private final RedisTemplate<String, Object> redisTemplate;
    private final IllUtilCache illUtilCache;

    // 红外传感器下拉列表
    @RequestMapping("infraredSelectList")
    public R<List<SensorSelectRespVo>> iginfraredSelectList(Integer deviceId) {

        List<SensorSelectRespVo> result = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            SensorSelectRespVo vo = new SensorSelectRespVo();
            vo.setSensorId(i + 1);
            vo.setSensorName("红外传感器" + (i + 1));
            result.add(vo);
        }

        return R.ok(result);

    }

    // 照度传感器下拉列表
    @RequestMapping("illuminanceSelectList")
    public R<List<SensorSelectRespVo>> illuminanceSelectList(Integer deviceId) {

        List<SensorSelectRespVo> result = new LinkedList<>();

        for (int i = 0; i < 5; i++) {
            SensorSelectRespVo vo = new SensorSelectRespVo();
            vo.setSensorId(i + 1);
            vo.setSensorName("照度传感器" + (i + 1));
            result.add(vo);
        }

        return R.ok(result);
    }

    // 红外传感器照明参数
    @GetMapping("/infraredParams")
    public R<InfraredSensorParamsRespVo> infraredParams(Integer deviceId, Integer sensorId) {
        R<InfraredSensorParamsRespVo> infraredSensorParamsRespVoR = infraredCache.infraredParams(deviceId, sensorId);
        if (infraredSensorParamsRespVoR != null) return infraredSensorParamsRespVoR;

        InfraredSensorParamsRespVo vo = new InfraredSensorParamsRespVo();
        vo.setSensorId(sensorId);
        vo.setEnabled(0);
        vo.setInductiveLux(0);
        vo.setInductiveSwitchStatus(0);
        vo.setUninductionLux(0);
        vo.setUninductionSwitchStatus(1);
        vo.setDelayedTime(0);
        return R.ok(vo);
    }

    private static final int[] arr = new int[]{166, 167, 168, 169, 170};

    // 照度传感器启用外控
    @RequestMapping("/illuminanceEnabled")
    public R<Integer> illuminanceSelectList(Integer deviceId, Integer sensorId) {
        try {
            return R.ok(key.getTelecommand(deviceId, arr[sensorId - 1]) == 1 ? 0 : 1);
        } catch (Exception e) {
            log.warn("获取照度传感器外控状态异常, deviceId={}, sensorId={}", deviceId, sensorId, e);
        }
        return R.ok(1);
    }

    // 照度传感器照明参数
    @GetMapping("/illuminanceParams")
    public R<IlluminanceSensorParamsRespVo> illuminanceParams(Integer deviceId, Integer sensorId) {
        R<IlluminanceSensorParamsRespVo> resp = illuminanceCache.illuminanceParams(deviceId, sensorId, null, null);
        if (resp != null) return resp;

        IlluminanceSensorParamsRespVo vo = new IlluminanceSensorParamsRespVo();
        vo.setSensorId(sensorId);
        vo.setEnabled(0);
        vo.setIlluminanceLux(0);
        vo.setConstantIlluminanceLux(0);
        vo.setHysteresisLux(0);
        vo.setAdjustmentTimeSeconds(0);
        vo.setExternalControlEnabled(0);
        // 前端页面的照度传感器的外部控制状态是 OutControlStatus 字段
        vo.setOutControlStatus(0);
        vo.setOutControlChannel(BigDecimal.ZERO);
        vo.setOutControlAddr(BigDecimal.ZERO);
        return R.ok(vo);
    }

    // 红外传感器分组选择
    @PostMapping("infraredGroupSelect")
    public TableDataInfo<SimpleGroupRespVo> infraredGroupSelect(@RequestBody SensorGroupSelectReqVo reqVo) {
        TableDataInfo<SimpleGroupRespVo> simpleGroupRespVoTableDataInfo = infraredCache.infraredGroupSelect(reqVo);
        if (simpleGroupRespVoTableDataInfo != null) return simpleGroupRespVoTableDataInfo;
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(reqVo.getPageNum());
        pageQuery.setPageSize(16);
        List<SimpleGroupRespVo> result = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            SimpleGroupRespVo vo = new SimpleGroupRespVo();
            vo.setId(i + 1);
            vo.setGroupName("分组" + (i + 1));
            vo.setGroupId(i + 1);
            vo.setSelectStatus(0);
            result.add(vo);
        }
        return TableDataInfo.build(result);
    }

    // 照度传感器分组选择
    @PostMapping("illuminanceGroupSelect")
    public TableDataInfo<SimpleGroupRespVo> illuminanceGroupSelect(@RequestBody SensorGroupSelectReqVo reqVo) {
        TableDataInfo<SimpleGroupRespVo> simpleGroupRespVoTableDataInfo = illuminanceCache.illuminanceGroupSelect(reqVo);
        if (simpleGroupRespVoTableDataInfo != null) return simpleGroupRespVoTableDataInfo;
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(reqVo.getPageNum());
        pageQuery.setPageSize(16);
        List<SimpleGroupRespVo> result = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            SimpleGroupRespVo vo = new SimpleGroupRespVo();
            vo.setId(i + 1);
            vo.setGroupName("分组" + (i + 1));
            vo.setGroupId(i + 1);
            vo.setSelectStatus(0);
            result.add(vo);
        }
        return TableDataInfo.build(result);
    }

    // 根据照度传感器的外部通道与外部地址获取照度传感器当前照度值
    @GetMapping("/getIllLux")
    public R<Integer> getIllLux(Integer deviceId, Integer moduleId, Integer sourceId) {
        if (deviceId == null || moduleId == null) return R.ok(0);
        return R.ok(lux(illUtilCache.getDeviceId(deviceId), moduleId));
    }

    // 根据照度传感器模式获取当前照度值，不启用外控下，需要一直读取
    @GetMapping("/getIllBaseLux")
    public R<Integer> getIllBaseLux(Integer deviceId, Integer moduleId) {
        return R.ok(baseLux(deviceId, moduleId));
    }

    private static final Map<String, Integer> map = new HashMap<String, Integer>() {{
        put("outChannel", 0);
        put("outAddress", 0);
    }};

    // 根据设备ID和照度模式ID获取照度传感器的外部通道和外部地址
    @GetMapping("/getAddress")
    public R<?> getAddress(Integer deviceId, Integer sensorId) {
        return R.ok(address(deviceId, sensorId));
    }

    public int lux(int deviceId, Integer sensorId) {
        Object lux = redisTemplate.opsForHash().get("zm:power:ill:" + deviceId + ":" + sensorId, "lux");
        if (lux != null) return Integer.parseInt(lux + "");
        return 0;
    }

    public int baseLux(int deviceId, Integer sensorId) {
        Object lux = redisTemplate.opsForHash().get("zm:power:ill:" + deviceId + ":" + sensorId, "lux");
        if (lux != null) return Integer.parseInt(lux + "");
        return 0;
    }

    public Map<String, Integer> address(int deviceId, Integer sensorId) {
        Object address = redisTemplate.opsForHash().get("zm:power:ill:" + deviceId + ":" + sensorId, "address");
        if (address != null) return (Map<String, Integer>) address;
        return map;
    }
}
