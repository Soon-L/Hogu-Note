package com.example.demo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemoService {
	
	private final MemoRepository memoRepository;
	private final PasswordEncoder passwordEncoder;
	
    private int length = 16; // personalCode 길이
    private String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // 사용 가능한 문자
    private Random random = new Random();
    
    
	
    // 새 메모 저장
    @Transactional
	public MemoResponseDto saveMemo(MemoSaveRequestDto requestDto) {
    	
    	List<Memo> list = memoRepository.findAll();
    	System.out.println("데이터 수: "+list.size());
    	
    	String password;
		
    	
		// 비밀번호 해싱
    	if(requestDto.getPassword().trim() != "") {
    		password = passwordEncoder.encode(requestDto.getPassword().trim());
    	}
    	else {
    		password = null;
    	}
		 
		 System.out.println("원본 비번: " + requestDto.getPassword().trim());
		 System.out.println("해시 비번: " + password);
				
		Memo memo = Memo.builder()
				.originalMemo(requestDto.getOriginalMemo())
				.summaryMemo(null)
				.personalCode(requestDto.getPersonalCode())
				.password(password)
				.build();
		
		Memo savedMemo = memoRepository.save(memo);

		
		return MemoResponseDto.fromEntity(savedMemo);
	}
	
	
	
	
	
	// 코드 중복 검사
	@Transactional
	public String personalCodeCheck() {
		
		String personalCode;
		boolean isUnique;
	

		do {
			StringBuilder sb = new StringBuilder(length); // personalCode만큼의 용량으로 초기화
			
			// 랜덤 코드 생성
	        for (int i = 0; i < length; i++) {
	        	int index = random.nextInt(characters.length()); // 랜덤 인덱스 선택
	        	sb.append(characters.charAt(index));
	        	}
	        personalCode= sb.toString();
	        
	        
	        // 중복 검사
	        // list로 가져와서 반복문 검사 => X
	        // 생성한 코드로 DB 데이터 존재유무 확인 => O
	        Optional<Memo> existingMemo = memoRepository.findByPersonalCode(personalCode);
	        isUnique = existingMemo.isEmpty(); // DB에 존재X = true / DB에 존재O = false
	        
		} while (!isUnique); // while문은 true일때 반복함
							 // !true(DB에 존재X) = false => 반복X 

		

		return personalCode;
		
		
	}
	
	
	
	// 메모 존재 여부 확인
	@Transactional
	public boolean memoExist(String personalCode) {
		
		return memoRepository.existsByPersonalCode(personalCode);
	}
	
	
	
	// 비밀번호 체크
	@Transactional
	public Boolean checkMemoType(String personalCode) {
		
		Optional<Memo> memoOptional = memoRepository.findByPersonalCode(personalCode);
		if(memoOptional.isPresent()) {
			Memo memo = memoOptional.get();
			
			return memo.password() != ""; // 비번o == 시크릿메모
			
		}
		
		return false; // 비번x == 공개메모
	}
	
	

	
	
	// 메모 불러오기
	@Transactional
	public MemoResponseDto loadMemo(String personalCode, String providedPassword) {

		Memo memo = memoRepository.findByPersonalCode(personalCode)
					.orElseThrow(() -> new MemoNotFoundException("코드 " + personalCode + " 못찾음"));
		
		// 시크릿메모여부 확인
		if(memo.password() != "" && memo.password() != null && !memo.password().isEmpty()) {
			
			// 비밀번호 입력 확인
			if (providedPassword == null || providedPassword.isEmpty()) {
				 throw new UnauthorizedException("비밀번호 미입력");
			}
			
			// 비밀번호 해싱 확인
			if(!passwordEncoder.matches(providedPassword.trim(), memo.password())) {
				throw new UnauthorizedException("비밀번호 불일치.");
			}
		}
		
		// 공개메모 or 비번 일치
		return MemoResponseDto.fromEntity(memo);
		
	}
	
	
	
	
	// 비밀번호 검증 없이 메모를 찾는 메서드
	@Transactional
	public MemoResponseDto findMemoByPersonalCode(String personalCode) {
		 Memo memo = memoRepository.findByPersonalCode(personalCode)
				 .orElseThrow(() -> new MemoNotFoundException("Memo with personal code " + personalCode + " not found."));
		 
		 return MemoResponseDto.fromEntity(memo);
	}
	
	
	
	
	// 메모 수정
	@Transactional
	 public MemoResponseDto updateMemo(MemoUpdateRequestDto requestDto) {
		
		// 코드로 조회
		Memo memo = memoRepository.findByPersonalCode(requestDto.getPersonalCode())
				 .orElseThrow(() -> new MemoNotFoundException("Memo with personal code " + requestDto.getPersonalCode() + " not found."));
		
		
				
				// 수정 내용 저장
				memo.originalMemo(requestDto.getOriginalMemo());
				memo.summaryMemo(requestDto.getSummaryMemo());
				//memo.updatedAt(Instant.now());
				memo.updatedAt(Instant.now());
				
				
				Memo updatedMemo = memoRepository.save(memo);
				
				return MemoResponseDto.fromEntity(updatedMemo);
				
	}
	

}
