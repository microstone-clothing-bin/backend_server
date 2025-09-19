package com.example.clothing_backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findById(String id); // 로그인 ID(String)로 사용자 조회

    boolean existsById(String id); // 아이디 중복 확인
    boolean existsByNickname(String nickname); // 닉네임 중복 확인
}