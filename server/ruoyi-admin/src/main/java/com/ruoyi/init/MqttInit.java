package com.ruoyi.init;

import com.ruoyi.init.bean.HeartInit;
import com.ruoyi.mqtt.MqttPublisher;
import com.ruoyi.mqtt.PublishKey;
import com.ruoyi.utils.device.DListUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttInit {
    private final DListUtil dListUtil;
    private final MqttPublisher mqttPublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        redisTemplate.opsForValue().set("zm:heart:init", "", 100, TimeUnit.SECONDS);
    }

    public void initHeart() {
        dListUtil.Nos().forEach(deviceNo -> {
            int no = Math.toIntExact(deviceNo);
            mqttPublisher.publish(no, PublishKey.获取心跳和版本号, new HeartInit(no));
        });
    }

    public void checkHeart(int no) {
        mqttPublisher.publish(no, PublishKey.获取心跳和版本号, new HeartInit(no));
    }
}
