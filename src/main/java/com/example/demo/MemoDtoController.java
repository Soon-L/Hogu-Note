package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memo")
public class MemoDtoController {
	
	private final MemoService memoService;
	
	
	@PostMapping
	public ResponseEntity<MemoResponseDto> saveMemo(@RequestBody MemoRequestDto requestDto){
		
		MemoResponseDto memoResponseDto = memoService.saveMemo(requestDto);
		
		return new ResponseEntity<>(memoResponseDto, HttpStatus.CREATED);
		
	}

}
