package com.ruoyi.mqtt03.request.handler;

import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.zm.domain.DevConfigInfraredSensor;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("Topic26")
@RequiredArgsConstructor
public class Topic26 implements RequestHandler {
    private final Key k;
    private final MqttPublisher mqttPublisher;

    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
        int moduleId = payload.getInt("id");
        Map<String, Object> hwMap = (Map<String, Object>) k.getRemote(deviceNo, "0XAE7A");
        if (hwMap != null && !hwMap.isEmpty()) {
            List<DevConfigInfraredSensor> data = (List<DevConfigInfraredSensor>) hwMap.getOrDefault("data", null);
            if (data != null && !data.isEmpty()) {
                Map<String, Object> m = new HashMap<>();
                m.put("msgId", IdGenerator.UUIDId());
                int[] a = new int[3];
                for (int i = 0; i < data.size(); i++) a[i] = data.get(i).getEnabled() == 1 ? 1: 0;
                m.put("data", a);
                mqttPublisher.publish(deviceNo, PublishKey.红外模式选择, m);
            }
        }
    }
}
