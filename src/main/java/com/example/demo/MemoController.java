package com.example.demo;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller // 단순 매핑용
public class MemoController {
	
	// 메인페이지
	@GetMapping("/main")
	public String main() {
		
		
		return "main";
	}
	
	
	// 새메모 페이지
	@GetMapping("/new_memo")
	public String newMemo(Model model) {
		
        int length = 16; // 원하는 문자열 길이
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // 사용 가능한 문자
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
        	int index = random.nextInt(characters.length()); // 랜덤 인덱스 선택
        	sb.append(characters.charAt(index));
        	}
        System.out.println("랜덤 문자열: " + sb.toString());
        
        
        model.addAttribute("personalCode", sb.toString());

		
		
		return "newMemo";
	}
	

}
