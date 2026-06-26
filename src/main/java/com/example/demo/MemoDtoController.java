package com.example.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memo")
public class MemoDtoController {
	
	private final MemoService memoService;
	
	
	// 새 메모 저장
	@PostMapping
	public ResponseEntity<MemoResponseDto> createMemo(@RequestBody MemoSaveRequestDto requestDto){
		
		MemoResponseDto memoResponseDto = memoService.saveMemo(requestDto);
		
		return new ResponseEntity<>(memoResponseDto, HttpStatus.CREATED);
		
	}
	
	
	
	// 기존 메모 수정
	@PutMapping("/{personalCode}")
	public ResponseEntity<MemoResponseDto> updateMemo(@PathVariable(value="personalCode") String personalCode, 
														@RequestBody MemoUpdateRequestDto requestDto){
		
		try {
			MemoResponseDto memoResponseDto = memoService.updateMemo(personalCode, requestDto);
			return new ResponseEntity<>(memoResponseDto, HttpStatus.OK);
		}catch(MemoNotFoundException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
			
		}catch(UnauthorizedException e) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401
		}catch(Exception e) {
			 return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500
		}

	}
	
	
	
	// DB 비번 -> js 넘기기
	@GetMapping("/{personalCode}")
    public Map<String, Object> getData(@PathVariable(value="personalCode")String personalCode, MemoResponseDto responseDto) {
        Map<String, Object> response = new HashMap<>();
        Object dto = memoService.findMemoByPersonalCode(personalCode);
        response.put("message", "성공");
//        response.put("password", responseDto.getPassword()); // null
        response.put("dto", dto);
        
        return response; // JSON으로 자동 변환됨
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
	
	
	
	
	// DB 비번 -> js 넘기기
//	@GetMapping("/load")
//    public Map<String, Object> gettest(@PathVariable(value="personalCode")String personalCode, MemoResponseDto responseDto) {
//        Map<String, Object> response = new HashMap<>();
//        Object dto = memoService.findMemoByPersonalCode(personalCode);
//        response.put("message", "성공");
//        response.put("password", responseDto.getPassword());
//        response.put("originalMemo", dto);
//        
//        return response; // JSON으로 자동 변환됨
//    }
	
	// 메모 불러오기
	@PostMapping("/load")
	public ResponseEntity<Object> loadMemo(@RequestBody MemoLoadRequestDto requestDto){
		
		
		try {
			MemoResponseDto memoResponseDto = memoService.loadMemo(requestDto.getPersonalCode(), requestDto.getPassword());
			return new ResponseEntity<>(memoResponseDto, HttpStatus.CREATED);
			
		}catch(MemoNotFoundException e){
			return new ResponseEntity<>(new ErrorResponseDto(e.getMessage(), "NOT_FOUND"), HttpStatus.NOT_FOUND); // 404
			
		}catch(UnauthorizedException e) {
			return new ResponseEntity<>(new ErrorResponseDto(e.getMessage(), "UNAUTHORIZED"), HttpStatus.UNAUTHORIZED); // 401
		}catch(Exception e) {
			 return new ResponseEntity<>(new ErrorResponseDto(e.getMessage(), "INTERNAL_SERVER_ERROR"), HttpStatus.INTERNAL_SERVER_ERROR); // 500
		}
		
		
	}
	
	
	
}
	