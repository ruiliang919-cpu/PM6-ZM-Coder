package com.ruoyi.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableIntegration
@RequiredArgsConstructor
public class MqttConfig {
    private final MqttBean mqttProperties;

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{mqttProperties.getUrl()});
        options.setUserName(mqttProperties.getUserName());
        options.setPassword(mqttProperties.getPassword().toCharArray());
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(600);
        options.setMaxInflight(200);
        options.setExecutorServiceTimeout(60);
        return options;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        return factory;
    }

    @Bean
    public MessageProducerSupport mqttInboundAdapter() {
        return new MqttPahoMessageDrivenChannelAdapter(
            mqttProperties.getId() + "_in",
            mqttClientFactory(),
            "/zm/+/heart", "/zm/+/holding", "/zm/+/coil", "/zm/+/connect", "/zm/+/request"
        );
    }

    @Bean
    public MessageHandler mqttOutboundMessageHandler() {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(
            mqttProperties.getId() + "_out",
            mqttClientFactory());
        handler.setAsync(true);
        return handler;
    }

    @Bean
    public IntegrationFlow mqttOutboundFlow() {
        return IntegrationFlows.from("mqttOutboundChannel")
            .handle(mqttOutboundMessageHandler())
            .get();
    }

    @Bean(name = "mqttOutboundChannel")
    public MessageChannel mqttOutboundChannel() {
        return new QueueChannel(500);
    }

    @Bean
    public IntegrationFlow mqttInboundFlow() {
        return IntegrationFlows
            .from(mqttInboundAdapter())
            .channel(mqttInputChannel())
            .get();
    }

    @Bean(name = "mqttInputChannel")
    public MessageChannel mqttInputChannel() {
        return new ExecutorChannel(mqttTaskExecutor());
    }

    @Bean
    public Executor mqttTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(32);
        executor.setMaxPoolSize(64);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("mqtt-pool-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        log.info("MQTT线程池初始化: coreSize=32, maxSize=64, queueCapacity=500, rejectedHandler=CallerRunsPolicy");
        return executor;
    }
}
