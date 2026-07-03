package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoUpdateRequestDto {
	
	private Long memoId;
	private String originalMemo;
	private String summaryMemo;
	private String password;
	private String personalCode;

}
