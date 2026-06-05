package com.example.demo;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// DB -> 사용자 데이터 전달
public class MemoResponseDto {
	
	private Long memoId;
	private String personalCode;
	private String originalMemo;
	private String summaryMemo;
	private String pw;
	private Instant createdAt;
	private Instant updatedAt;
	
	
	public static MemoResponseDto fromEntity(Memo memo) {
		
		return MemoResponseDto.builder()
				.memoId(memo.getMemoId())
				.personalCode(memo.getPersonalCode())
				.originalMemo(memo.getOriginalMemo())
				.summaryMemo(memo.getSummaryMemo())
				.pw(memo.getPw())
				.createdAt(memo.getCreatedAt())
				.updatedAt(memo.getUpdatedAt())
				.build();
	}

}
