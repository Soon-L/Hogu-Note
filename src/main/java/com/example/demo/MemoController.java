package com.example.demo;

import java.util.Optional;
import java.util.UUID;

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
	public String defaultMain(HttpSession session, Model model) {

		// 메인으로 돌아오면 이전 메모 세션 정리 → 새 메모 진입 시 새 코드 발급
		session.removeAttribute("code");
		session.removeAttribute("role");

		return "main";
	}

	
	
	
	
	// 새메모 페이지 (작성자)
	@GetMapping("/new_memo")
	public String new_memo(HttpSession session, Model model) {
		
        // 세션에 code 랜덤 생성
        String code = (String) session.getAttribute("code");
        code = memoService.personalCodeCheck();
        session.setAttribute("code", code);
        
        // 작성자 권한 부여
        session.setAttribute("role", "WRITER");
		
		// 코드 추가
        model.addAttribute("personalCode", code);
        model.addAttribute("role", "WRITER");
        
        // 새메모인지 확인
        boolean checkExist = memoService.memoExist(code);
        model.addAttribute("checkExist", checkExist);
        
		return "newMemo";
	}
	
	
	
	 // 메모에 join - 참여자(VIEWER)
    @GetMapping("/new_memo/{code}")
    public String joinMemo(@PathVariable(value = "code") String code, HttpSession session, Model model) {

        String sessionCode = (String) session.getAttribute("code");

        // 세션에 다른 code가 이미 있으면 접근 차단
        if (sessionCode != null && !sessionCode.equals(code)) {
            model.addAttribute("errorMessage", "접근 권한이 없습니다.");
            return "errorPage";
        }

        // 최초 입장이거나 동일 code 재진입 → 세션에 저장
        session.setAttribute("code", code);
        session.setAttribute("role", "VIEWER");

        model.addAttribute("personalCode", code);
        model.addAttribute("role", "VIEWER");
        model.addAttribute("checkExist", false);

        return "newMemo";
    }
	
	
	
	
	
	
	// 메모 불러오기(수정사항 : 오직 메인 통해서만 접근 가능하게 수정)
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
			 
		     // 수정 불가
		     session.setAttribute("role", "VIEWER");
		     model.addAttribute("role", "VIEWER");
			 
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
