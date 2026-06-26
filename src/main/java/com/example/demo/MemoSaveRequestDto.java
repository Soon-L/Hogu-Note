package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoSaveRequestDto {
	
	private String originalMemo;
	private String summaryMemo;
	private String personalCode;
	private String password;

}
