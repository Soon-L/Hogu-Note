package com.example.demo;

import java.time.Instant;
import java.time.LocalDateTime;

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
//	private Instant createdAt;
//	private Instant updatedAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String password;
	
	
	public static MemoResponseDto fromEntity(Memo memo) {
		
		return MemoResponseDto.builder()
				.memoId(memo.memoId())
				.personalCode(memo.personalCode())
				.originalMemo(memo.originalMemo())
				.summaryMemo(null)
				.createdAt(memo.createdAt())
				.updatedAt(memo.updatedAt())
				.password(memo.password())
				.build();
	}

}
