package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<Memo, Long>{

	// 메모 존재 여부 확인
	boolean existsByPersonalCode(String personalCode);

	// 코드로 메모 찾기
	Optional<Memo> findByPersonalCode(String personalCode);



}
