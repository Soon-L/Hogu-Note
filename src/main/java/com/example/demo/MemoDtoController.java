package com.example.demo;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memo")
public class MemoDtoController {
	
	private final MemoService memoService;
	
	
	// 새 메모 저장
	@PostMapping
	public ResponseEntity<MemoResponseDto> createMemo(@RequestBody MemoSaveRequestDto requestDto, HttpSession session){
		
		MemoResponseDto memoResponseDto = memoService.saveMemo(requestDto);
		
	    // 작성자 본인에게도 즉시 수정 권한(verifiedMemoId) 부여
	    session.setAttribute("verifiedMemoId", memoResponseDto.getMemoId());
		
		return new ResponseEntity<>(memoResponseDto, HttpStatus.CREATED);
		
	}
	
	
	
	// 기존 메모 수정
	@PutMapping("/update")
	public ResponseEntity<MemoResponseDto> updateMemo(@RequestBody MemoUpdateRequestDto requestDto, HttpSession session){
		
		try {		
			// 인증 확인
			Long verifiedMemoId = (Long) session.getAttribute("verifiedMemoId");
			
			if(verifiedMemoId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
			}
			if(!verifiedMemoId.equals(requestDto.getMemoId())){
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
			}
			
			
			MemoResponseDto memoResponseDto = memoService.updateMemo(requestDto);
			return new ResponseEntity<>(memoResponseDto, HttpStatus.OK);
		}catch(MemoNotFoundException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
			
		}catch(UnauthorizedException e) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401
		}catch(Exception e) {
			 return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500
		}

	}
	
	
	// 비밀메모 체크
	@PostMapping("/{personalCode}/type")
	public ResponseEntity<MemoTypeResponseDto> getMemoType(@PathVariable(value="personalCode") String personalCode) {
		
		// 메모 없으면 404
		if(!memoService.memoExist(personalCode)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		// 시크릿메모 여부 확인
		boolean isSecret = memoService.checkMemoType(personalCode);
		
		return new ResponseEntity<>(new MemoTypeResponseDto(isSecret), HttpStatus.OK);
	}
	

	// 메모 불러오기
	@PostMapping("/load")
	public ResponseEntity<Object> loadMemo(@RequestBody MemoLoadRequestDto requestDto, HttpSession session){
		
		
		try {
			MemoResponseDto memoResponseDto = memoService.loadMemo(requestDto.getPersonalCode(), requestDto.getPassword());
			
			// 현재 세션 삭제
		    session.removeAttribute("verifiedMemoId");
		    
		    // 권한 새로 추가
		    session.setAttribute("verifiedMemoId", memoResponseDto.getMemoId());

		    // 비밀번호 통과 시점에만 웹소켓 권한 부여!
		    session.setAttribute("code", requestDto.getPersonalCode()); 
		    
			return new ResponseEntity<>(memoResponseDto, HttpStatus.CREATED);
			
		}catch(MemoNotFoundException e){
			return new ResponseEntity<>(new ErrorResponseDto(e.getMessage(), "NOT_FOUND"), HttpStatus.NOT_FOUND); // 404
			
		}catch(UnauthorizedException e) {
			return new ResponseEntity<>(new ErrorResponseDto(e.getMessage(), "UNAUTHORIZED"), HttpStatus.UNAUTHORIZED); // 401
		}catch(Exception e) {
			 return new ResponseEntity<>(new ErrorResponseDto(e.getMessage(), "INTERNAL_SERVER_ERROR"), HttpStatus.INTERNAL_SERVER_ERROR); // 500
		}
		
		
	}
	
	
	
	// 편집 중 세션 만료 방지
	@GetMapping("/session/keep-alive")
	public ResponseEntity<Void> keepAlive() {
	    return ResponseEntity.ok().build();
	}
	
	
	
}
	