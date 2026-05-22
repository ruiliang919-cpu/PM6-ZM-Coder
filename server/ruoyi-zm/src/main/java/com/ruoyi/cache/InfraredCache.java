package com.ruoyi.cache;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.zm.domain.DevConfigInfraredSensor;
import com.ruoyi.zm.domain.vo.InfraredSensorParamsRespVo;
import com.ruoyi.zm.domain.vo.SensorGroupSelectReqVo;
import com.ruoyi.zm.domain.vo.SimpleGroupRespVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InfraredCache {
    public static final String[] ADDR = {
        "0XAE7A",
        "0XAE81",
        "0XAE88"
    };
    private final Key key;
    private final GroupCache groupCache;

    // 红外传感器分组选择
    public TableDataInfo<SimpleGroupRespVo> infraredGroupSelect(SensorGroupSelectReqVo reqVo) {
        try {
            List<SimpleGroupRespVo> source = new ArrayList<>();
            List<String> groupNames = groupCache.groupNames(reqVo.getDeviceId());
            for (int i = 0; i < 16; i++) {
                SimpleGroupRespVo vo = new SimpleGroupRespVo();
                vo.setId(i + 1);
                vo.setGroupId(i + 1);
                // vo.setSelectStatus(groupSelectArr[i]);
                vo.setGroupName(groupNames.get(i));
                source.add(vo);
            }

            try {
                Integer[] groupSelectArr = (Integer[]) getSensorParams(reqVo.getDeviceId(), reqVo.getSensorId()).get("group");
                for (int i = 0; i < source.size(); i++) {
                    source.get(i).setSelectStatus(groupSelectArr[i]);
                }
            } catch (Exception e) {
                for (SimpleGroupRespVo simpleGroupRespVo : source) {
                    simpleGroupRespVo.setSelectStatus(0);
                }
            }

            return key.getPageTable(source, 1, 16);
        } catch (Exception e) {
            log.error("InfraredCache → infraredGroupSelect", e);
        }
        return null;
    }

    // 红外传感器照明参数
    public R<InfraredSensorParamsRespVo> infraredParams(Integer deviceId, Integer sensorId) {
        try {
            DevConfigInfraredSensor source = (DevConfigInfraredSensor) getSensorParams(deviceId, sensorId).get("sensor");
            InfraredSensorParamsRespVo vo = new InfraredSensorParamsRespVo();
            vo.setSensorId(source.getSensorId());
            vo.setEnabled(source.getEnabled());
            vo.setInductiveLux(source.getInductiveLux());
            vo.setInductiveSwitchStatus(source.getInductiveSwitchStatus() == 0 ? 1 : 0);
            vo.setUninductionLux(source.getUninductionLux());
            vo.setUninductionSwitchStatus(source.getUninductionSwitchStatus());
            vo.setDelayedTime(source.getDelayedTime());
            return R.ok(vo);
        } catch (Exception e) {
//             log.error("InfraredCache → infraredParams", e);
        }
        return null;
    }

    // 根据传感器ID获取传感器参数列表
    public Map<String, Object> getSensorParams(Integer deviceId, Integer sensorId) {
        Map<String, Object> source = (Map<String, Object>) key.getRemote(deviceId, "0XAE7A");
        Integer[] groupSelectArr = (Integer[]) source.get("data" + sensorId);
        DevConfigInfraredSensor sensor = ((List<DevConfigInfraredSensor>) source.get("data")).get(sensorId - 1);
        Map<String, Object> result = new HashMap<>();
        if (groupSelectArr == null) {
            groupSelectArr = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        }
        result.put("group", groupSelectArr);
        result.put("sensor", sensor);
        return result;
    }

//    public Map<String, Object> getSensorIntParams(Integer deviceId, Integer sensorId) {
//        Map<String, Object> source = (Map<String, Object>) key.getRemote(deviceId, "0XAE7A");
//        int[] groupSelectArr = (int[]) source.get("data" + sensorId);
//        DevConfigInfraredSensor sensor = ((List<DevConfigInfraredSensor>) source.get("data")).get(sensorId - 1);
//        Map<String, Object> result = new HashMap<>();
//        if (groupSelectArr == null) {
//            groupSelectArr = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//        }
//        result.put("group", groupSelectArr);
//        result.put("sensor", sensor);
//        return result;
//    }
}
