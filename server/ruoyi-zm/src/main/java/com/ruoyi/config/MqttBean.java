package com.ruoyi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Data
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttBean {
    private String id = UUID.randomUUID().toString();
    private String url;
    private String userName;
    private String password;
}
