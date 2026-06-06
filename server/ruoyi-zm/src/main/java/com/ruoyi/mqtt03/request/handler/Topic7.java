package com.ruoyi.mqtt03.request.handler;

import cn.hutool.json.JSONObject;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.mqtt03.request.base.RequestHandler;
import com.ruoyi.mqttwrite.common.CommonDataInt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("Topic7")
@RequiredArgsConstructor
public class Topic7 implements RequestHandler {
    private final MqttPublisher mqttPublisher;
    private final Key key;

    @Override
    public void handle(Integer deviceNo, JSONObject payload) {
        int id = payload.getInt("id");
        int zoneId = d(deviceNo, id);
        CommonDataInt data = new CommonDataInt();
        data.setAddr("0xA5AE");
        CommonDataInt.D d = new CommonDataInt.D();
        d.setId(id);
        d.setValue(zoneId);
        data.setData(d);
        mqttPublisher.publish(deviceNo, PublishKey.分区编号, data);
    }

    int d(Integer deviceId, Integer groupId) {
        short[] codes = key.getRemoteByArr(deviceId, "0xA5AE");
        if (codes[0] != -1) return codes[groupId - 1];
        else return 1;
    }
}
