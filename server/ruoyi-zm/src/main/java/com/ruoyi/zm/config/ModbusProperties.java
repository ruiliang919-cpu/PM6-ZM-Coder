package com.ruoyi.zm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Modbus TCP 通讯配置属性
 */
@Component
@ConfigurationProperties(prefix = "modbus.tcp")
public class ModbusProperties {

    /** 通讯超时时间(毫秒) */
    private int timeout = 1000;

    /** 通讯重试次数 */
    private int retries = 1;

    /** 最大连接数 */
    private int maxConnections = 100;

    /** 心跳检测间隔(秒) */
    private int heartbeatInterval = 30;

    /** 连接过期时间(秒) */
    private int connectionTtl = 86400;

    /** Modbus TCP 端口 */
    private int port = 502;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public int getConnectionTtl() {
        return connectionTtl;
    }

    public void setConnectionTtl(int connectionTtl) {
        this.connectionTtl = connectionTtl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
