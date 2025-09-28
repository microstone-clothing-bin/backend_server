package com.example.clothing_backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // --- UserService에서 사용하는 맞춤 조회 메소드들 ---

    /**
     * 로그인 ID(String)로 사용자를 조회합니다.
     * JpaRepository의 기본 findById(Long)을 대체합니다.
     */
    Optional<User> findById(String id);

    /**
     * 로그인 ID(String)의 중복 여부를 확인합니다.
     */
    boolean existsById(String id);

    /**
     * 닉네임의 중복 여부를 확인합니다.
     */
    boolean existsByNickname(String nickname);

    /**
     * 이메일의 중복 여부를 확인합니다.
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임과 이메일이 모두 일치하는 사용자를 조회합니다. (아이디 찾기 기능용)
     */
    Optional<User> findByNicknameAndEmail(String nickname, String email);

    /**
     * 로그인 ID와 이메일이 모두 일치하는 사용자를 조회합니다. (비밀번호 재설정 기능용)
     */
    Optional<User> findByIdAndEmail(String id, String email);

    /**
     * 사용자의 고유 PK(userId, Long)를 사용하여 해당 유저의 모든 권한(Role) 목록을 조회합니다.
     * JPQL(Java Persistence Query Language)을 사용한 쿼리입니다.
     */
    @Query("SELECT r.roleName FROM User u JOIN u.roles r WHERE u.userId = :userId")
    List<String> findRolesByUserId(@Param("userId") Long userId);
}