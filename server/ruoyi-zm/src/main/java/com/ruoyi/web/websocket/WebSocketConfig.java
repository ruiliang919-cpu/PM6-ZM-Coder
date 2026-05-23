package com.ruoyi.web.websocket;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 配置类
 * 基于 Spring STOMP + SockJS，同时支持原生 WebSocket 连接
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 服务端推送的前缀，客户端订阅 /topic/** 即可接收消息
        config.enableSimpleBroker("/topic");
        // 客户端发送消息的前缀（暂不使用，保留扩展）
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // SockJS 端点（浏览器有 sockjs-client 库时使用）
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .addInterceptors(new WebSocketAuthInterceptor())
            .withSockJS();

        // 原生 WebSocket 端点（前端无 sockjs-client 时直连）
        registry.addEndpoint("/ws/native")
            .setAllowedOriginPatterns("*")
            .addInterceptors(new WebSocketAuthInterceptor());
    }

    /**
     * WebSocket 握手鉴权拦截器
     * 在握手阶段校验 token 参数，拒绝未认证连接
     */
    @Slf4j
    static class WebSocketAuthInterceptor implements HandshakeInterceptor {

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                String token = servletRequest.getServletRequest().getParameter("token");
                if (token == null || token.isEmpty()) {
                    log.warn("WebSocket握手被拒绝: token为空");
                    return false;
                }
                try {
                    Object loginId = StpUtil.getLoginIdByToken(token);
                    if (loginId == null) {
                        log.warn("WebSocket握手被拒绝: token无效, token={}", token);
                        return false;
                    }
                    attributes.put("loginId", loginId);
                    return true;
                } catch (Exception e) {
                    log.warn("WebSocket握手异常: token校验失败, token={}", token, e);
                    return false;
                }
            }
            log.warn("WebSocket握手被拒绝: 非Servlet请求");
            return false;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                WebSocketHandler wsHandler, Exception exception) {
            // do nothing
        }
    }
}
