package com.example.demo;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// DB -> 사용자 데이터 전달
public class MemoTypeResponseDto {
	
    @JsonProperty("isSecret") // JSON 키 이름을 "isSecret"으로 확실하게 고정
    private boolean isSecret;
	


}
