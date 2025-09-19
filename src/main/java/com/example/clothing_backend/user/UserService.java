package com.example.clothing_backend.user;

import com.example.clothing_backend.user.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public void addUser(User user) {
        // 비밀번호를 암호화하여 저장
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        // DAO를 통해 사용자 등록, 반환된 savedUser에서 자동 생성된 ID 사용
        User savedUser = userDao.addUser(user.getEmail(), user.getId(), encodedPassword, user.getNickname());

        // 기본 권한(role) 매핑 (ROLE_USER)
        userDao.mappingUserRole(savedUser.getUserId());
    }

    // 로그인 시 사용자 조회
    public User getUser(String id) {
        return userDao.getUserById(id);
    }

    // 중복 확인 (아이디, 닉네임)
    public boolean isDuplicate(String type, String value) {
        if ("id".equals(type)) {
            return userDao.existsById(value);
        } else if ("nickname".equals(type)) {
            return userDao.existsByNickname(value);
        }
        return false;
    }

    // 역할 조회
    @Transactional(readOnly = true)
    public List<String> getRoles(Long userId) {
        return userDao.getRoles(userId);
    }

    // 아이디 찾기
    @Transactional(readOnly = true)
    public String findIdByNicknameAndEmail(String nickname, String email) {
        return userDao.findIdByNicknameAndEmail(nickname, email);
    }

    // 비밀번호 찾기
    @Transactional(readOnly = true)
    public String findPwByIdAndEmail(String id, String email) {
        return userDao.findPwByIdAndEmail(id, email);
    }

    // 프로필 이미지 저장
    @Transactional
    public String saveProfileImage(MultipartFile file, String id) {
        try {
            byte[] bytes = file.getBytes();
            String filename = file.getOriginalFilename();
            userDao.saveProfileImage(id, bytes, filename);

            // 브라우저에서 바로 보여줄 수 있도록 Base64 인코딩 후 반환
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("프로필 이미지 저장 실패", e);
        }
    }

    // 프로필 이미지 조회
    @Transactional(readOnly = true)
    public String getProfileImageBase64(String id) {
        byte[] imageBytes = userDao.getProfileImageBlob(id);
        if (imageBytes != null && imageBytes.length > 0) {
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        }
        return null;
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(String id) {
        userDao.deleteUser(id);
    }

    // 비밀번호 재설정
    @Transactional
    public void updatePassword(String id, String email, String newPassword) {
        // 새 비밀번호를 암호화 후 DB 업데이트
        String encodedPassword = passwordEncoder.encode(newPassword);
        userDao.updatePassword(id, email, encodedPassword);
    }
}
