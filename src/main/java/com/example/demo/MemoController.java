package com.example.demo;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpSession;
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
        
        // 새메모인지 확인
        boolean checkExist = memoService.memoExist(uniqueCode);
        System.out.println("뭐냐고     : "+checkExist);
        model.addAttribute("checkExist", checkExist);
        

		
		
		return "newMemo";
	}
	
	
	
	
	
	
	// 메모 불러오기(오직 메인 통해서만 접근 가능)
	// 0629 메모: 지금 구조를 restAPI로 바꿔서 데이터 전달하기 -> 비밀메모의 세션정보 관리하기
	@GetMapping("/memo/{personalCode}")
	public String displayMemo(@PathVariable("personalCode") String personalCode, Model model, HttpSession session) {
		
		
		System.out.println("진입1");
		
		// 공개 메모 or 비번 확인된 메모 반환
		// 비밀번호 확인은 MemoDtoController에서 처리
		try {
			
			System.out.println("진입2");
			
			 MemoResponseDto memo = memoService.findMemoByPersonalCode(personalCode);
			 model.addAttribute("memo", memo);
			 
			 System.out.println("점검 memo.getMemoId : " + memo.getMemoId());
			 
			 // 수정 권한 부여
			 session.setAttribute("verifiedMemoId", memo.getMemoId());
			 
		     // 새메모인지 확인
		     boolean checkExist = memoService.memoExist(personalCode);
		     model.addAttribute("checkExist", checkExist);
			 
			 
			 return "loadMemo";
			 
		}catch(MemoNotFoundException e) {
			 model.addAttribute("errorMessage", e.getMessage());
			 
			 return "errorPage";	
		}
		
	}
	
	
	
	
	

}
