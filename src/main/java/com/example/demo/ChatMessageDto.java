package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor // Jackson이 역직렬화할 때 필요로 하는 기본 생성자를 제공
@AllArgsConstructor
@Getter
@Setter
public class ChatMessageDto {
    private String code;
    private String sender;
    private String content;
    private String type; // "UPDATE", "JOIN", "LEAVE"

}

