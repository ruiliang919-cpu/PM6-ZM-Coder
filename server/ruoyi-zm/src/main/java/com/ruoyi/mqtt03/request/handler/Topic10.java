package com.ruoyi.mqtt03.request.handler;

import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.mqttwrite.inf.InfValue;
import com.ruoyi.web.controller.zm.WriteController;
import com.ruoyi.zm.domain.DevConfigInfraredSensor;
import com.ruoyi.zm.domain.vo.InfraredParamsReqVo;
import com.ruoyi.zm.domain.vo.InfraredParamsTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("Topic10")
@RequiredArgsConstructor
public class Topic10 implements RequestHandler {
    private final WriteController w;
    private final Key k;

    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
        int moduleId = payload.getInt("id");
        Map<String, Object> hwMap = (Map<String, Object>) k.getRemote(deviceNo, "0XAE7A");
        if (hwMap != null && !hwMap.isEmpty()) {
            List<DevConfigInfraredSensor> data = (List<DevConfigInfraredSensor>) hwMap.getOrDefault("data", null);
            if (data != null && !data.isEmpty()) {
                DevConfigInfraredSensor d = data.get(moduleId - 1);
                InfraredParamsReqVo r = new InfraredParamsReqVo();
                r.setDeviceId(deviceNo);
                r.setEnabled(d.getEnabled() == 1);
                r.setSensorId(d.getSensorId());
                Integer[] orDefault = (Integer[]) hwMap.getOrDefault("data" + moduleId, new Integer[16]);
                r.setGroupIds(InfValue.selectIntegerArr(orDefault));
                InfraredParamsTable t = new InfraredParamsTable();
                t.setSensorId(d.getSensorId());
                t.setDelayedTime(d.getDelayedTime());
                t.setEnabled(d.getEnabled());
                t.setInductiveLux(d.getInductiveLux());
                t.setInductiveSwitchStatus(d.getInductiveSwitchStatus() == 0 ? 1 : 0);
                t.setUninductionLux(d.getUninductionLux());
                t.setUninductionSwitchStatus(d.getUninductionSwitchStatus());
                r.setTable(t);
                w.updateInfraredParams(r);
            }
        }
    }
}
