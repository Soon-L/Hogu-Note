package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.DispatcherType;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {
	
	 @Bean
	    public PasswordEncoder passwordEncoder() {
	        // 기본 강도(Strength)는 10이며, 필요에 따라 4~31 사이의 값을 지정할 수 있습니다.
	        return new BCryptPasswordEncoder();
	        
	 }
	 
	 
	 
     // 특정 HTTP 요청에 대한 웹 기반 보안 구성 (수정된 버전)
    @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	.csrf(csrf -> csrf.disable()) // CSRF 비활성화(restAPI 사용시 필수 / 단, 쿠키에 비밀번호 저장 안해야 보안됨)
        	.authorizeHttpRequests((request) -> 
        	request.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
              // 다음 경로들은 로그인 없이 접근 허용
             .requestMatchers("/**").permitAll()
             );

         return http.build();
     }

}


