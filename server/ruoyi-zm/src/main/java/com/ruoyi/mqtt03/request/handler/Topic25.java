package com.ruoyi.mqtt03.request.handler;

import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.zm.domain.DevConfigIlluminanceSensor;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("Topic25")
@RequiredArgsConstructor
public class Topic25 implements RequestHandler {
    private final Key k;
    private final MqttPublisher mqttPublisher;

    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
//        int moduleId = payload.get("id").getAsInt();
        Map<String, Object> zdMap = (Map<String, Object>) k.getRemote(deviceNo, "0XAE8F");
        if (zdMap != null && !zdMap.isEmpty()) {
            List<DevConfigIlluminanceSensor> data = (List<DevConfigIlluminanceSensor>) zdMap.getOrDefault("data", null);
            if (data != null && !data.isEmpty()) {
                Map<String, Object> m = new HashMap<>();
                m.put("msgId", IdGenerator.UUIDId());
                int[] a = new int[5];
                for (int i = 0; i < data.size(); i++) a[i] = data.get(i).getEnabled() == 1 ? 1: 0;
                m.put("data", a);
                mqttPublisher.publish(deviceNo, PublishKey.照度模式选择, m);
            }
        }
    }
}
