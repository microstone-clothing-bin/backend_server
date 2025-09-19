package com.example.clothing_backend.user.dao;

import com.example.clothing_backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setUserId(rs.getLong("user_id"));
            user.setId(rs.getString("id"));
            user.setPassword(rs.getString("password"));
            user.setNickname(rs.getString("nickname"));
            user.setEmail(rs.getString("email"));
            user.setRedate(rs.getTimestamp("redate").toLocalDateTime());
            user.setProfileImageBlob(rs.getBytes("profile_image_blob"));
            return user;
        };
    }

    // 회원가입
    public User addUser(String email, String id, String password, String nickname) {
        String sql = "INSERT INTO user (email, id, password, nickname) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, id);
            ps.setString(3, password);
            ps.setString(4, nickname);
            return ps;
        }, keyHolder);

        // 불필요한 DB 조회를 없애고 바로 User 객체를 만들어 반환
        User user = new User();
        user.setUserId(keyHolder.getKey().longValue());
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setNickname(nickname);

        return user;
    }

    // 사용자-역할 매핑
    public void mappingUserRole(Long userId) {
        String sql = "INSERT INTO user_role (user_id, role_id) VALUES (?, (SELECT role_id FROM role WHERE role_name = 'ROLE_USER'))";
        jdbcTemplate.update(sql, userId);
    }

    // 사용자 조회 (로그인 ID 기준)
    public User getUserById(String id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper(), id);
        } catch (Exception e) {
            return null;
        }
    }

    // 역할 조회
    public List<String> getRoles(Long userId) {
        String sql = "SELECT r.role_name FROM role r JOIN user_role ur ON r.role_id = ur.role_id WHERE ur.user_id = ?";
        return jdbcTemplate.queryForList(sql, String.class, userId);
    }

    // 아이디 찾기
    public String findIdByNicknameAndEmail(String nickname, String email) {
        String sql = "SELECT id FROM user WHERE nickname = ? AND email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, nickname, email);
        } catch (Exception e) {
            return null;
        }
    }

    // 비밀번호 찾기
    public String findPwByIdAndEmail(String id, String email) {
        String sql = "SELECT password FROM user WHERE id = ? AND email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, id, email);
        } catch (Exception e) {
            return null;
        }
    }

    // SQL 인젝션 보안을 위해 아이디/닉네임 중복확인 메소드를 명확하게 분리
    public boolean existsById(String id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM user WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    public boolean existsByNickname(String nickname) {
        String sql = "SELECT EXISTS(SELECT 1 FROM user WHERE nickname = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, nickname);
    }

    // 프로필 이미지 저장
    public void saveProfileImage(String id, byte[] bytes, String filename) {
        String sql = "UPDATE user SET profile_image_blob = ?, profile_image_name = ? WHERE id = ?";
        jdbcTemplate.update(sql, bytes, filename, id);
    }

    // 프로필 이미지 조회
    public byte[] getProfileImageBlob(String id) {
        String sql = "SELECT profile_image_blob FROM user WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, byte[].class, id);
    }

    // 회원 탈퇴
    public void deleteUser(String id) {
        String sql = "DELETE FROM user WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // 비밀번호 재설정
    public void updatePassword(String id, String email, String newPassword) {
        String sql = "UPDATE user SET password = ? WHERE id = ? AND email = ?";
        jdbcTemplate.update(sql, newPassword, id, email);
    }
}