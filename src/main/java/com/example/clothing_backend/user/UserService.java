package com.example.clothing_backend.user;

import com.example.clothing_backend.user.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // ✅ 비밀번호 암호화 기능 import
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
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        userDao.addUser(user.getEmail(), user.getId(), user.getPassword(), user.getNickname());
        userDao.mappingUserRole(user.getUserId());
    }

    // 로그인 시 사용
    public User getUser(String id) {
        return userDao.getUserById(id);
    }

    // 중복 확인
    public boolean isDuplicate(String type, String value) {
        if ("id".equals(type)) {
            return userDao.existsByField("id", value);
        } else if ("nickname".equals(type)) {
            return userDao.existsByField("nickname", value);
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
        String encodedPassword = passwordEncoder.encode(newPassword);
        userDao.updatePassword(id, email, encodedPassword);
    }
}