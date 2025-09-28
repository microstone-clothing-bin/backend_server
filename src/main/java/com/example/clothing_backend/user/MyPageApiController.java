// json을 반환하는 컨트롤러

package com.example.clothing_backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageApiController {

    private final UserService userService;

    // 마이페이지 정보 조회 (프로필 이미지 포함)
    @GetMapping("")
    public ResponseEntity<?> getMyPageInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        User user = userService.getUser(userDetails.getUsername());
        String base64Image = userService.getProfileImageBase64(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("nickname", user.getNickname());
        response.put("email", user.getEmail());
        response.put("profileImageBase64", base64Image);

        return ResponseEntity.ok(response);
    }

    // 프로필 이미지 업로드
    @PostMapping("/profile-image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("image") MultipartFile profileImage,
                                                     @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        User user = userService.getUser(userDetails.getUsername());
        userService.saveProfileImage(profileImage, user.getId());
        return ResponseEntity.ok("프로필 이미지가 성공적으로 업로드되었습니다.");
    }

    // 비밀번호 재설정
    @PostMapping("/password")
    public ResponseEntity<String> resetPassword(@RequestParam String newPassword,
                                                @RequestParam String newPasswordCheck,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        if (!newPassword.equals(newPasswordCheck)) {
            return ResponseEntity.badRequest().body("새 비밀번호가 일치하지 않습니다.");
        }
        User user = userService.getUser(userDetails.getUsername());
        userService.updatePassword(user.getId(), user.getEmail(), newPassword);

        // 클라이언트(웹/앱)는 이 응답을 받으면 강제로 로그아웃 시키고
        // 사용자가 다시 로그인하도록 유도하는 것이 보안상 안전합니다.
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다. 다시 로그인해주세요.");
    }

    // 회원 탈퇴
    @DeleteMapping("/account")
    public ResponseEntity<String> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        User user = userService.getUser(userDetails.getUsername());
        userService.deleteUser(user.getId());
        return ResponseEntity.ok("회원 탈퇴가 성공적으로 처리되었습니다.");
    }
}