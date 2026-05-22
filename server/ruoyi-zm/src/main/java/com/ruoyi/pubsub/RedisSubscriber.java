package com.ruoyi.pubsub;

import com.ruoyi.init.MqttInit;
import com.ruoyi.web.controller.zm.MeterEnergyController;
import com.ruoyi.web.controller.zm.WriteController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {
    private final WriteController w;
    private final MeterEnergyController m;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MqttInit mqttInit;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String mKey = new String(message.getBody());
        if (mKey.startsWith("lock")) return;
        String topic = new String(message.getChannel());
        if (RedisPublisher.Expired.equals(topic)) {
            if (mKey.startsWith("zm:record:module")) {
                try {
                    w.workModule(Integer.parseInt(mKey.split("~")[1]), 0);
                } catch (Exception e) {
                    log.error("{}", mKey, e);
                }
            }
            if (mKey.startsWith("zm:clear:power")) {
                try {
                    redisTemplate.delete("zm:energyMeter");
                    m.clear();
                } catch (Exception e) {
                    log.error("{}", mKey, e);
                }
            }
            if (mKey.startsWith("zm:heart:init")) {
                try {
                    mqttInit.initHeart();
                } catch (Exception e) {
                    log.error("{}", mKey, e);
                }
            }
        }
    }
}
