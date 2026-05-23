package com.ruoyi.mqtt;

import cn.hutool.json.JSONUtil;

import com.ruoyi.cache.Key;
import com.ruoyi.zm.domain.DevWriteInstruct;
import com.ruoyi.zm.mapper.DevWriteInstructMapper;
import com.ruoyi.zm.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class MqttPublisher {
    private final MessageChannel mqttOutboundChannel;
    private final Key key;
    private final DevWriteInstructMapper instructMapper;

    public void publish(int deviceNo, PublishKey topic, Object payload) {
        try {
            String ip = key.getCreateTCP(deviceNo).getIp();
            // String v = new Gson().toJson(payload);
            String v = JSONUtil.toJsonStr(payload);
            String t = topic.getTopic(ip.split("\\.")[3]);
            mqttOutboundChannel.send(MessageBuilder.withPayload(v)
                .setHeader(MqttHeaders.TOPIC, t)
                .setHeader(MqttHeaders.QOS, 2)
                .setHeader(MqttHeaders.RETAINED, false)
                .build());
            if (topic != PublishKey.事件记录 && topic != PublishKey.照度外控值)
                save(ip, deviceNo, v, t);
        } catch (Exception e) {
            log.error("MQTT publish failed, topic={}, deviceNo={}", topic != null ? topic.name() : "null", deviceNo, e);
        }
    }

    @Async
    public void save(String ip, int deviceNo, String value, String topic) {
        DevWriteInstruct d = new DevWriteInstruct();
        d.setId(IdGenerator.UUIDId());
        d.setTimestamp(System.currentTimeMillis());
        d.setIp(ip);
        d.setSalveId(deviceNo);
        d.setWriteValue(value);
        d.setAddr(topic);
        instructMapper.insert(d);
    }

    @Scheduled(fixedRate = 20000)
    public void link() {
        mqttOutboundChannel.send(MessageBuilder.withPayload("{}")
            .setHeader(MqttHeaders.TOPIC, "connect")
            .setHeader(MqttHeaders.QOS, 0)
            .setHeader(MqttHeaders.RETAINED, false)
            .build());
    }
}
