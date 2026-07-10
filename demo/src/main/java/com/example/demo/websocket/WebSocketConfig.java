package com.example.demo.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 설정 클래스
 * - /ws/chat/{roomId} 경로로 WebSocket 연결을 수락
 * - URI 변수로 roomId(UUID)를 받아 핸들러에 전달
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;

    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
            .addHandler(chatWebSocketHandler, "/ws/chat/{roomId}")
            .setAllowedOriginPatterns("*"); // 개발용: 모든 출처 허용
    }
}
