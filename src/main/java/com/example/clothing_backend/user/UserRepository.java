package com.example.clothing_backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 로그인 시 아이디(id)로 User 객체 조회
    User findById(String id);

    // 회원가입 시 아이디 중복 확인
    boolean existsById(String id);

    // 회원가입 시 닉네임 중복 확인
    boolean existsByNickname(String nickname);
}