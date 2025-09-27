//package com.example.clothing_backend.user;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@RequiredArgsConstructor
//public class UserController {
//
//    private final UserService userService; // 유저 관련 비즈니스 로직 처리 서비스
//
//    @GetMapping("/register.html")
//    public String registerForm() { return "register"; } // 회원가입 폼
//
//    @GetMapping("/login.html")
//    public String loginForm() { return "login"; } // 로그인 폼
//
//    @GetMapping("/findIdForm")
//    public String findIdForm() { return "findId"; } // 아이디 찾기 폼
//
//    @GetMapping("/findPwForm")
//    public String findPwForm() { return "findPw"; } // 비밀번호 찾기 폼
//
//    @PostMapping("/userReg")
//    public String processRegistration(User user) {
//        userService.addUser(user); // 새 유저 DB에 저장
//        return "reg_success"; // 회원가입 완료 페이지로 이동
//    }
//
//    @PostMapping("/findId")
//    public String findId(@RequestParam String nickname, @RequestParam String email, Model model) {
//        String foundId = userService.findIdByNicknameAndEmail(nickname, email);
//        // 찾은 ID가 있으면 메시지 세팅, 없으면 에러 메시지
//        model.addAttribute("message", foundId != null
//                ? "회원님의 아이디는 [ " + foundId + " ] 입니다."
//                : "일치하는 회원이 없습니다.");
//        return "findId";
//    }
//
//    @PostMapping("/findPw")
//    public String findPw(@RequestParam String id, @RequestParam String email, Model model) {
//        // 비밀번호 초기화 (임시 비밀번호 메일 전송 등)
//        userService.findPwByIdAndEmail(id, email);
//        model.addAttribute("message", "가입하신 이메일로 임시 비밀번호 관련 안내를 전송했습니다.");
//        return "findPw";
//    }
//
//    @GetMapping("/api/checkDuplicate")
//    @ResponseBody
//    public String checkDuplicate(@RequestParam String type, @RequestParam String value) {
//        // type = "id" 또는 "nickname"
//        // 중복이면 "duplicate", 사용 가능하면 "ok"
//        return userService.isDuplicate(type, value) ? "duplicate" : "ok";
//    }
//}

package com.example.clothing_backend.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/user") // 기본 경로를 /api/user로 설정
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    // --- 기존 웹 페이지용 매핑 (그대로 유지) ---
    @GetMapping("/register.html")
    public String registerForm() { return "register"; }

    // --- API 전용 엔드포인트 ---

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> apiLogin(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        String id = loginRequest.get("id");
        String password = loginRequest.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(id, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            session.setAttribute("loginUser", user);
            return ResponseEntity.ok(Map.of("status", "success", "message", "로그인 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "아이디 또는 비밀번호가 일치하지 않습니다."));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> apiRegister(@RequestBody User user) {
        try {
            userService.addUser(user);
            return ResponseEntity.ok(Map.of("status", "success", "message", "회원가입이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "회원가입 실패: " + e.getMessage()));
        }
    }

    // 중복 체크 API
    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Boolean>> checkIdDuplicate(@RequestParam("id") String id) {
        return ResponseEntity.ok(Collections.singletonMap("isDuplicate", userService.isDuplicate("id", id)));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNicknameDuplicate(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(Collections.singletonMap("isDuplicate", userService.isDuplicate("nickname", nickname)));
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailDuplicate(@RequestParam("email") String email) {
        return ResponseEntity.ok(Collections.singletonMap("isDuplicate", userService.isDuplicate("email", email)));
    }

    // 아이디/비밀번호 찾기 API
    @PostMapping("/find-id")
    public ResponseEntity<Map<String, String>> findId(@RequestBody Map<String, String> request) {
        String foundId = userService.findIdByNicknameAndEmail(request.get("nickname"), request.get("email"));
        return ResponseEntity.ok(Collections.singletonMap("id", foundId));
    }

    @PostMapping("/find-pw")
    public ResponseEntity<Map<String, String>> findPw(@RequestBody Map<String, String> request) {
        boolean isSuccess = userService.findPwByIdAndEmail(request.get("id"), request.get("email"));
        if (isSuccess) {
            // 실제 이메일 전송 로직은 UserService에 구현해야 함
            return ResponseEntity.ok(Map.of("status", "success", "message", "가입하신 이메일로 안내 메일을 전송했습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "일치하는 회원 정보가 없습니다."));
        }
    }

    // --- [ApiController에서 이동 및 수정] 마이페이지 기능 ---

    @PostMapping("/mypage/profile-upload")
    public ResponseEntity<Map<String, String>> uploadProfile(
            @RequestParam("profileImage") MultipartFile profileImage,
            @AuthenticationPrincipal User loginUser) {
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "로그인이 필요합니다."));
        }
        try {
            String base64Image = userService.saveProfileImage(profileImage, loginUser.getId());
            return ResponseEntity.ok(Map.of("status", "success", "profileImageUrl", base64Image));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", "이미지 업로드 실패"));
        }
    }

    @PostMapping("/mypage/password-reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody Map<String, String> passwordRequest,
            @AuthenticationPrincipal User loginUser) {
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "로그인이 필요합니다."));
        }
        String newPassword = passwordRequest.get("newPassword");
        // 비밀번호 확인 로직은 프론트에서 처리하는 것을 가정
        userService.updatePassword(loginUser.getId(), loginUser.getEmail(), newPassword);
        return ResponseEntity.ok(Map.of("status", "success", "message", "비밀번호가 변경되었습니다. 다시 로그인해주세요."));
    }

    @PostMapping("/mypage/delete-account")
    public ResponseEntity<Map<String, String>> deleteAccount(@AuthenticationPrincipal User loginUser) {
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "로그인이 필요합니다."));
        }
        userService.deleteUser(loginUser.getId());
        return ResponseEntity.ok(Map.of("status", "success", "message", "회원 탈퇴가 완료되었습니다."));
    }
}