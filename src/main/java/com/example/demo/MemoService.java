package com.example.demo;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemoService {
	
	private final MemoRepository memoRepository;
	
	public MemoResponseDto saveMemo(MemoRequestDto requestDto) {
		
		Memo memo = Memo.builder()
				.originalMemo(requestDto.getOriginalMemo())
				.summaryMemo(requestDto.getSummaryMemo())
				.pw(requestDto.getPw())
				.build();
		
		Memo savedMemo = memoRepository.save(memo);
		
		return MemoResponseDto.fromEntity(savedMemo);
	}

}
