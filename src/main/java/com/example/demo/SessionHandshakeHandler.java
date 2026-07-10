package com.example.demo;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import jakarta.servlet.http.HttpSession;


// MemoController에서 세션 생성 후 이 코드 읽힘
// HTTP 세션 정보를 Websocket 세션에 복사하는 클래스 
public class SessionHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,	// HTTP -> webSocket의 모든 요청 정보를 담고 있다. 
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        // HTTP 세션에서 code를 꺼내 WebSocket attributes에 저장
        if (request instanceof ServletServerHttpRequest servletRequest) {	// instanceof: 객체 타입 확인용
            HttpSession session = servletRequest.getServletRequest().getSession(false); // getSession(false): 세션x -> null 반환
            if (session != null) {
                String code = (String) session.getAttribute("code");
                if (code != null) {
                    attributes.put("code", code);
                }
            }
        }
        
        // 개개인 구분 필요x
        return () -> "anonymous";
    }
}
