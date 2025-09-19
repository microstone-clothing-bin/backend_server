package com.example.clothing_backend.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name = "`user`") // MySQL 예약어(user)라 백틱으로 감싸서 테이블명 지정
@Getter
@Setter
public class User implements UserDetails { // Spring Security UserDetails 구현

    // 기본 사용자 정보
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // PK

    @Column(nullable = false, unique = true)
    private String id; // 로그인 아이디, username 역할

    @Column(nullable = false)
    private String password; // 암호화된 비밀번호

    @Column(nullable = false)
    private String nickname; // 닉네임

    @Column(nullable = false)
    private String email; // 이메일

    private LocalDateTime redate; // 가입일 또는 마지막 정보 수정일

    // 프로필 이미지
    @Lob
    private byte[] profileImageBlob; // DB에 실제 이미지 저장용 BLOB

    @Transient
    private String profileImageBase64; // 프론트에 전송할 Base64 문자열용, DB에는 저장 X

    // Spring Security 권한
    @Transient
    private Collection<? extends GrantedAuthority> authorities;
    // UserDetails에서 필요한 권한 정보, DB 저장 X

    // UserDetails 인터페이스 구현

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities; // Security가 권한 체크할 때 사용
    }

    @Override
    public String getUsername() {
        return this.id; // Security가 로그인 아이디로 인식
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부
    }
}