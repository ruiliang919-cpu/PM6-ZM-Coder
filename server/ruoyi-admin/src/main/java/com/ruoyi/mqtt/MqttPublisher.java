package com.ruoyi.mqtt;

import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.ruoyi.cache.Key;
import com.ruoyi.mqtt.vo.Heart;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

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
            log.error("", e);
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

    public static void main(String[] args) {
        boolean[] initData = new boolean[845];
        Arrays.fill(initData, true);
        Map<String, Object> m = new HashMap<>();
        m.put("ip", "193");
        m.put("data", initData);
        System.out.println(JSONUtil.toJsonStr(m));
    }

    //    @Scheduled(fixedRate = 1000)
    public void postConstruct() {
        boolean[] initData = new boolean[845];
        Arrays.fill(initData, true);
        Map<String, Object> m = new HashMap<>();
        m.put("ip", "");
        m.put("data", initData);
        Map<Integer, Object[]> m1 = new HashMap<>();
        m1.put(99, new Object[]{"192.168.2.99", 1});
        m1.put(19, new Object[]{"192.168.2.19", 2});
        m1.put(193, new Object[]{"192.168.2.193", 3});
        IntStream.of(99, 19, 193).forEach(i -> {
            Heart heart = new Heart();
            heart.setIp(m1.get(i)[0].toString());
            Heart.Data data = new Heart.Data();
            data.setOnline(true);
            data.setTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            data.setVersion("阿弥诺斯");
            heart.setData(data);
            mqttOutboundChannel.send(MessageBuilder.withPayload(new Gson().toJson(heart))
                .setHeader(MqttHeaders.TOPIC, "/zm/" + i + "/heart")
                .setHeader(MqttHeaders.QOS, 2)
                .setHeader(MqttHeaders.RETAINED, false)
                .build());
            mqttOutboundChannel.send(MessageBuilder.withPayload(new Gson().toJson(m))
                .setHeader(MqttHeaders.TOPIC, "/zm/" + i + "/coil")
                .setHeader(MqttHeaders.QOS, 2)
                .setHeader(MqttHeaders.RETAINED, false)
                .build());
        });
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
