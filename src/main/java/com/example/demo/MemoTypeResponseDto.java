package com.example.demo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// DB -> 사용자 데이터 전달
public class MemoTypeResponseDto {
	
	private boolean isSecret;
	


}
