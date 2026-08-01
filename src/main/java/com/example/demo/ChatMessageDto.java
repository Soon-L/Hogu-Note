package com.example.demo;

import java.util.Map;

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
    private String content;
    private String coding;
	private String clientId;
    private String type; // "UPDATE", "JOIN", "LEAVE"
    private Boolean visible; // 코드박스 보임/숨김 상태
    private String theme; // 코드미러 테마(라이트, 다크)
    private String mode; // 배경색(다크모드, 라이트모드)
    private Boolean wideCodebox; // 코드박스 와이드모드
    private Boolean wiedQuillbox; // quill 와이드모드

}

//
//public class ChatMessageDto {
//    private Map<String, Object> delta;
//    private String clientId;
//
//}

