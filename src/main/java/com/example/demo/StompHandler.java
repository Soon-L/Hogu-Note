package com.example.demo;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StompHandler implements ChannelInterceptor {

	// 시크릿메모면 인증 전 웹소켓 막기
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 클라이언트가 /topic/memo/{code} 구독(SUBSCRIBE)을 시도할 때
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination(); // 예: /topic/memo/aB8kL2...
            
            if (destination != null && destination.startsWith("/topic/memo/")) {
                String subscribeCode = destination.replace("/topic/memo/", "");
                
                // WebSocket 세션 attributes에서 저장된 code 가져오기
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                String sessionCode = sessionAttributes != null ? (String) sessionAttributes.get("code") : null;

                // 세션에 인증된 code와 구독하려는 방 code가 다르면 구독 거부!
                if (sessionCode == null || !sessionCode.equals(subscribeCode)) {
                    throw new IllegalArgumentException("구독 권한이 없습니다.");
                }
            }
        }
        return message;
    }
}