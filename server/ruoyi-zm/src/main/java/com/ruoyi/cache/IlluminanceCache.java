package com.ruoyi.cache;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevConfigIlluminanceSensor;
import com.ruoyi.zm.domain.vo.IlluminanceSensorParamsRespVo;
import com.ruoyi.zm.domain.vo.SensorGroupSelectReqVo;
import com.ruoyi.zm.domain.vo.SimpleGroupRespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class IlluminanceCache {
    private final Key key;
    private final GroupCache groupCache;

    // 照度传感器照明参数
    public R<IlluminanceSensorParamsRespVo> illuminanceParams(Integer deviceId, Integer sensorId, Integer lux, Map<String, Integer> address) {
        try {
            DevConfigIlluminanceSensor sensor = (DevConfigIlluminanceSensor) getSensorParamsByIll(deviceId, sensorId).get("sensor");
            IlluminanceSensorParamsRespVo vo = new IlluminanceSensorParamsRespVo();
            vo.setSensorId(sensorId);
            vo.setEnabled(sensor.getEnabled());
            vo.setIlluminanceLux(lux);
            vo.setConstantIlluminanceLux(sensor.getConstantIlluminanceLux());
            vo.setHysteresisLux(sensor.getHysteresisLux());
            vo.setAdjustmentTimeSeconds(sensor.getAdjustmentTimeSeconds());
            // 前端页面0是启用，1是禁用，设备相反
            // OutControlStatus 是启用外控/外部控制，ExternalControlEnabled 不是
//            vo.setExternalControlEnabled(sensor.getExternalControlEnabled());
            vo.setOutControlStatus(sensor.getOutControlStatus());
            vo.setOutControlChannel(BigDecimal.ZERO);
            vo.setOutControlAddr(BigDecimal.ZERO);
            return R.ok(vo);
        } catch (Exception e) {
//             log.error("IlluminanceCache → illuminanceParams", e);
        }
        return null;
    }

    // 照度传感器分组选择
    public TableDataInfo<SimpleGroupRespVo> illuminanceGroupSelect(SensorGroupSelectReqVo reqVo) {
        try {
            List<SimpleGroupRespVo> source = new ArrayList<>();
            List<String> groupNames = groupCache.groupNames(reqVo.getDeviceId());
            for (int i = 0; i < 16; i++) {
                SimpleGroupRespVo vo = new SimpleGroupRespVo();
                vo.setGroupId(i + 1);
                vo.setGroupName(groupNames.get(i));
                source.add(vo);
            }

            try {
                Integer[] groupSelectArr = (Integer[]) getSensorParamsByIll(reqVo.getDeviceId(), reqVo.getSensorId()).get("group");
                for (int i = 0; i < source.size(); i++) source.get(i).setSelectStatus(groupSelectArr[i]);
            } catch (Exception e) {
                for (SimpleGroupRespVo simp : source) simp.setSelectStatus(0);
            }

            return key.getPageTable(source, 1, 16);
        } catch (Exception e) {
//             log.error("IlluminanceCache → illuminanceGroupSelect", e);
        }
        return null;
    }

    // 根据传感器ID获取传感器参数列表
    public Map<String, Object> getSensorParamsByIll(Integer deviceId, Integer sensorId) {
        Map<String, Object> source = (Map<String, Object>) key.getRemote(deviceId, "0XAE8F");
        Integer[] groupSelectArr = (Integer[]) source.get("data" + sensorId);
        DevConfigIlluminanceSensor sensor = ((List<DevConfigIlluminanceSensor>) source.get("data")).get(sensorId - 1);
        Map<String, Object> result = new HashMap<>();
        if (groupSelectArr == null) groupSelectArr = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        result.put("group", groupSelectArr);
        result.put("sensor", sensor);
        return result;
    }
}
