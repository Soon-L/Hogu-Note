package com.example.demo;

//ChatController.java

import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ChatController {

 private final SimpMessagingTemplate messagingTemplate;

 public ChatController(SimpMessagingTemplate messagingTemplate) {
     this.messagingTemplate = messagingTemplate;
 }

 /**
  * 클라이언트가 /app/chat/{roomId} 로 메시지 전송 시 호출
  * 같은 roomId 구독자들에게만 브로드캐스트
  */
 @MessageMapping("/memo/{code}")
 public void handleMessage(
         @DestinationVariable("code") String code,
         @Payload ChatMessageDto message,
         SimpMessageHeaderAccessor headerAccessor) {

     // WebSocket 세션 attributes에서 roomId 검증 (보안)
     Map<String, Object> attrs = headerAccessor.getSessionAttributes();
     String sessionCode = attrs != null ? (String) attrs.get("code") : null;

     // URL의 roomId와 세션의 roomId가 일치하는지 확인
     if (!code.equals(sessionCode)) {
         // 불일치 시 무시 (다른 방에 메시지 전송 시도 차단)
         return;
     }

     message.setCode(code);

     // /topic/room/{roomId} 구독자들에게 전송
     messagingTemplate.convertAndSend("/topic/memo/" + code, message);
 }
 
 
 
	 // "/app/editor"로 들어오는 메시지를 받는다.
//	 @MessageMapping("/memo/{code}")
//	 public void receive(@DestinationVariable("code") String code, @Payload ChatMessageDto message,
//			 			SimpMessageHeaderAccessor headerAccessor) {
//		 
//		 
//	     // WebSocket 세션 attributes에서 roomId 검증 (보안)
//	     Map<String, Object> attrs = headerAccessor.getSessionAttributes();
//	     String sessionCode = attrs != null ? (String) attrs.get("code") : null;
//	
//	     // URL의 roomId와 세션의 roomId가 일치하는지 확인
//	     if (!code.equals(sessionCode)) {
//	         // 불일치 시 무시 (다른 방에 메시지 전송 시도 차단)
//	         return;
//	     }
//	     
//	     message.setCode(code);
//	
//	     
//	     System.out.println("메세지 수신 완");
//	     // 받은 내용을 모든 구독자에게 다시 전송한다.
//	     messagingTemplate.convertAndSend("/topic/memo/" + code, message);
//	     
//	     System.out.println("브로드캐스트 완료!");
//	
//	 }
	 
	 
	 
	 // "/app/editor"로 들어오는 메시지를 받는다.
//	 @MessageMapping("/code/{pcode}")
//	 public void receiveCode(@DestinationVariable("pcode") String pcode, @Payload ChatCodeDto message,
//			 			SimpMessageHeaderAccessor headerAccessor) {
//		 
//		 
//	     // WebSocket 세션 attributes에서 roomId 검증 (보안)
//	     Map<String, Object> attrs = headerAccessor.getSessionAttributes();
//	     String sessionCode = attrs != null ? (String) attrs.get("code") : null;
//	
//	     // URL의 roomId와 세션의 roomId가 일치하는지 확인
//	     if (!pcode.equals(sessionCode)) {
//	         // 불일치 시 무시 (다른 방에 메시지 전송 시도 차단)
//	         return;
//	     }
//	     
//	     message.setPcode(pcode);
//	
//	     
//	     System.out.println("메세지 수신 완");
//	     // 받은 내용을 모든 구독자에게 다시 전송한다.
//	     messagingTemplate.convertAndSend("/topic/memo/" + pcode, message);
//	     
//	     System.out.println("브로드캐스트 완료!");
//	
//	 }
}
