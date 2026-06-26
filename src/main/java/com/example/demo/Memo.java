package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "memo")
@Data // Getter, Setter, toString, equals, hasCode 자동 생성
@NoArgsConstructor
@AllArgsConstructor
@Builder // 빌더 패턴을 사용해서 객체 생성 가능
@Accessors(fluent = true)
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long memoId;

//    @CreationTimestamp // 엔티티 생성시 현재 시간 자동 설정
//    @Column(name = "created_at", nullable = false, updatable = false) // updatable = false 생성시에만 설정
//    private Instant createdAt;
//
//    @UpdateTimestamp // 엔티티 수정시 현재 시간 자동 설정
//    @Column(name = "updated_at", nullable = false)
//    private Instant updatedAt;
    
    @CreationTimestamp // 엔티티 생성시 현재 시간 자동 설정
    @Column(name = "created_at", nullable = false, updatable = false) // updatable = false 생성시에만 설정
    private LocalDateTime createdAt;

    @UpdateTimestamp // 엔티티 수정시 현재 시간 자동 설정
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "personal_code", unique = true, nullable = false, length = 255) // 길이 기본값 255
    private String personalCode;

    @Column(name = "original_memo", columnDefinition = "TEXT")
    private String originalMemo;

    @Column(name = "summary_memo", columnDefinition = "TEXT")
    private String summaryMemo;
    
    @Column(name = "password", nullable = true)
    private String password;

}
