package com.ruoyi.mqtt03.request.handler;

import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.mqttwrite.inf.InfValue;
import com.ruoyi.web.controller.zm.WriteController;
import com.ruoyi.zm.domain.DevConfigIlluminanceSensor;
import com.ruoyi.zm.domain.vo.IlluminanceParamsReqVo;
import com.ruoyi.zm.domain.vo.IlluminanceParamsTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("Topic11")
@RequiredArgsConstructor
public class Topic11 implements RequestHandler {
    private final WriteController w;
    private final Key k;

    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
        int moduleId = payload.get("id").getAsInt();
        Map<String, Object> zdMap = (Map<String, Object>) k.getRemote(deviceNo, "0XAE8F");
        if (zdMap != null && !zdMap.isEmpty()) {
            List<DevConfigIlluminanceSensor> data = (List<DevConfigIlluminanceSensor>) zdMap.getOrDefault("data", null);
            if (data != null && !data.isEmpty()) {
                DevConfigIlluminanceSensor d = data.get(moduleId - 1);
                IlluminanceParamsReqVo p = new IlluminanceParamsReqVo();
                p.setDeviceId(deviceNo);
                p.setEnabled(d.getEnabled() == 1);
                Integer[] orDefault = (Integer[]) zdMap.getOrDefault("data" + moduleId, new Integer[16]);
                p.setGroupIds(InfValue.selectIntegerArr(orDefault));
                p.setSensorId(d.getSensorId());
                IlluminanceParamsTable ill = new IlluminanceParamsTable();
                ill.setSensorId(d.getSensorId());
                ill.setConstantIlluminanceLux(d.getConstantIlluminanceLux());
                ill.setHysteresisLux(d.getHysteresisLux());
                ill.setAdjustmentTimeSeconds(d.getAdjustmentTimeSeconds());
                ill.setEnabled(d.getEnabled());
                ill.setOutControlStatus(d.getOutControlStatus());
                ill.setIlluminanceLux(d.getIlluminanceLux());
                p.setTable(ill);
                w.updateIlluminanceParams(p);
            }
        }
    }
}
