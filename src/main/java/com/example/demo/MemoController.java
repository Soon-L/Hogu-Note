package com.example.demo;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;


@Controller // 단순 매핑용
@RequiredArgsConstructor
public class MemoController {
	
	private final MemoService memoService;
	
	// 메인페이지
	@GetMapping("")
	public String defaultMain() {
		
		return "main";
	}
	
	
	@GetMapping("/main")
	public String main() {
		
		
		return "main";
	}
	
	
	
	
	// 새메모 페이지
	@GetMapping("/new_memo")
	public String newMemo(Model model) {
		
		// 코드 생성
		String uniqueCode = memoService.personalCodeCheck();
		
		// 코드 추가
        model.addAttribute("personalCode", uniqueCode);
        

		
		
		return "newMemo";
	}
	
	
	
	
	
	
	// 메모 불러오기(오직 메인 통해서만 접근 가능)
	@GetMapping("/memo/{personalCode}")
	public String displayMemo(@PathVariable("personalCode") String personalCode, Model model) {
		
		// 공개 메모 or 비번 확인된 메모 반환
		// 비밀번호 확인은 MemoDtoController에서 처리
		try {
			 MemoResponseDto memo = memoService.findMemoByPersonalCode(personalCode);
			 model.addAttribute("memo", memo);
			 
			 return "loadMemo";
			 
		}catch(MemoNotFoundException e) {
			 model.addAttribute("errorMessage", e.getMessage());
			 
			 return "errorPage";	
		}
		
	}
	
	
	
	
	

}
