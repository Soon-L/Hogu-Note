package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Setter 자동 생성 -> setPw 필요 x
@NoArgsConstructor
@AllArgsConstructor
public class MemoRequestDto {
	
	private String originalMemo;
	private String summaryMemo;
	private String pw;

}
