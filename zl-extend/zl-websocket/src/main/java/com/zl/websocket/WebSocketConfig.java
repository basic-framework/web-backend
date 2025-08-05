package com.zl.websocket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类，用于注册WebSocket的Bean
 * @Author GuihaoLv
 */
@Configuration
public class WebSocketConfig {
    /**
     * 创建并返回一个 ServerEndpointExporter 实例。
     * ServerEndpointExporter 是 Spring 提供的一个工具类，用于自动注册使用了 @ServerEndpoint 注解的 WebSocket 端点。
     * 这样可以避免手动注册每个 WebSocket 端点。
     * @return ServerEndpointExporter 实例
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}