package com.group2.glamping.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:3000")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Cho phép broadcast tin nhắn qua "/topic"
        registry.enableSimpleBroker("/topic");
        // Các tin nhắn gửi từ client phải có prefix "/app"
        registry.setApplicationDestinationPrefixes("/app");
    }

    //    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        // Cho phép gửi tin nhắn đến các destination bắt đầu bằng /topic và /queue
//        registry.enableSimpleBroker("/topic", "/queue");
//        // Đặt prefix cho user-destination, ví dụ: /user
//        registry.setUserDestinationPrefix("/user");
//        // Các tin nhắn gửi từ client sẽ có prefix /app
//        registry.setApplicationDestinationPrefixes("/app");
//    }
}

