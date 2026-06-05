package com.example.demo;

import org.springframework.stereotype.Controller;
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
	public String newMemo() {
		
		
		return "newMemo";
	}
	

}
