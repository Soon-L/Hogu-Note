package com.example.demo.websocket;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WebSocket으로 주고받는 채팅 메시지 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    public enum MessageType {
        ENTER,   // 입장
        TALK,    // 일반 메시지
        LEAVE    // 퇴장
    }

    private MessageType type;   // 메시지 종류
    private String roomId;      // 채팅방 UUID
    private String sender;      // 보낸 사람 닉네임
    private String content;     // 메시지 내용

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();
}
