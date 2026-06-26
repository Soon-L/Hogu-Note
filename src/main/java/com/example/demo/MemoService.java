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
		
//		System.out.println("저장 시도: "+savedMemo);
//		System.out.println("비밀번호 해싱 됐냐?: " + savedMemo.getPassword());
		
		//System.out.println("해싱된 비번 저장: "+memo.password());
		
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
	
	
	
	
	
//	// 코드로 원본 메모 불러오기
//	public MemoResponseDto findMemoByPersonalCode(MemoRequestDto requestDto) throws Exception {
//		
//		
//		Optional<Memo> existingMemo = memoRepository.findByPersonalCode(requestDto.getPersonalCode());
//		
//		Memo memo = existingMemo.orElseThrow();
//
//		
//		
//		// 비밀메모일경우 예외처리 (공개메모는 안 탐)
//		if(memo.getPassword() != "") { // 공개 메모가 아니면	
//			
//			if(requestDto.getPassword() == null || requestDto.getPassword().isEmpty()) { // 비밀번호 입력
//				throw new Exception("시크릿 메모는 비밀번호를 입력해야합니다.");
//				
//			}
//			if(!passwordEncoder.matches(requestDto.getPassword(), memo.getPassword())){ // 비밀번호 일치
//				throw new Exception("비밀번호가 일치하지 않습니다");
//			}
//
//		}
//			
//		
//		return MemoResponseDto.fromEntity(memo);
//		
//	}
	
	
	
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
				
				System.out.println("입력 비번: " + providedPassword);
				System.out.println("DB 비번: " + memo.password());
				boolean matchResult = passwordEncoder.matches(providedPassword.trim(), memo.password());
				System.out.println("돟일한지 (matches method result): " + matchResult);
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
	
	
	
	
//    // 기존 메모 수정
//	@Transactional
//	public MemoResponseDto updateMemo(MemoRequestDto requestDto) {
//		
//		
//		// 비밀번호 해싱
//		 String hashedPassword = passwordEncoder.encode(requestDto.getPassword());
//		 
//		
//		Optional<Memo> beforeMemo = memoRepository.findById(requestDto.getMemoId());
//		
//		Memo memo = beforeMemo.get();
//		
//		memo.setOriginalMemo(requestDto.getOriginalMemo());
//		memo.setSummaryMemo(null);
//		memo.setPassword(hashedPassword);
//		memo.setUpdatedAt(Instant.now());
//		
//		System.out.println("수정된 정보: " + memo);
//		
//		memoRepository.save(memo);
//		
//		
//		return MemoResponseDto.fromEntity(memo);
//		
//	}
	
	
	
	
	// 메모 수정
	@Transactional
	 public MemoResponseDto updateMemo(String personalCode, MemoUpdateRequestDto requestDto) {
		
		// 코드로 조회
		Memo memo = memoRepository.findByPersonalCode(personalCode)
				 .orElseThrow(() -> new MemoNotFoundException("Memo with personal code " + personalCode + " not found."));
		
		
		
		// 시크릿메모여부 확인
				if(memo.password() != "") {
					
					// 비밀번호 입력 확인
					if (requestDto.getPassword() == null || requestDto.getPassword().isEmpty()) {
						 throw new UnauthorizedException("Password is required for this secret memo.");
					}
					
					// 비밀번호 해싱 확인
					if(!passwordEncoder.matches(requestDto.getPassword(), memo.password())) {
						throw new UnauthorizedException("Invalid password for secret memo.");
					}
				}else {
					if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
						memo.password(passwordEncoder.encode(requestDto.getPassword()));
					}
				}
				
				
				memo.originalMemo(requestDto.getOriginalMemo());
				memo.summaryMemo(requestDto.getSummaryMemo());
				//memo.updatedAt(Instant.now());
				memo.updatedAt(LocalDateTime.now());
				
				
				Memo updatedMemo = memoRepository.save(memo);
				
				return MemoResponseDto.fromEntity(updatedMemo);
				
	}
	
	
	
	
	







}
