package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
// ws://localhost:8080/memo 로 접속함
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
    private final StompHandler stompHandler; // 생성한 인터셉터 주입
    
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler); // 클라이언트 요청 진입 시 인터셉터 검증
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독할 prefix: /topic/memo/{code}
        config.enableSimpleBroker("/topic");
        // 클라이언트가 메시지를 보낼 prefix: /app/memo/{code}
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 연결 엔드포인트, SockJS fallback 지원
        registry.addEndpoint("/ws/memo")
                .setHandshakeHandler(new SessionHandshakeHandler()) // code 주입
                .withSockJS();
    }
}