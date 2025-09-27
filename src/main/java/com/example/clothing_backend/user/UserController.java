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

import com.example.clothing_backend.user.LoginInfo;
import com.example.clothing_backend.user.User;
import com.example.clothing_backend.user.UserRepository;
import com.example.clothing_backend.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

// API 요청만 처리하는 컨트롤러로 통합
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user") // 모든 URL 앞에 /api/user 를 붙임
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    /**
     * API 클라이언트(웹, 앱) 전용 로그인 처리
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> apiLogin(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        try {
            String id = loginRequest.get("id");
            String password = loginRequest.get("password");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(id, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();
            LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getId(), user.getNickname());
            // 필요하다면 role 정보도 추가
            // loginInfo.setRoles(userRepository.findRolesByUserId(user.getUserId()));
            session.setAttribute("loginUser", user);
            session.setAttribute("loginInfo", loginInfo);

            response.put("status", "success");
            response.put("message", "로그인에 성공했습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * API 클라이언트용 회원가입 처리
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> apiRegister(@RequestBody User user) {
        userService.addUser(user);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "회원가입이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 아이디 중복 체크 API
     */
    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Boolean>> checkIdDuplicate(@RequestParam("id") String id) {
        return ResponseEntity.ok(Collections.singletonMap("isDuplicate", userService.isDuplicate("id", id)));
    }

    /**
     * 이메일 중복 체크 API
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailDuplicate(@RequestParam("email") String email) {
        // UserService에 isDuplicate("email", email) 로직이 구현되어 있어야 함
        return ResponseEntity.ok(Collections.singletonMap("isDuplicate", userService.isDuplicate("email", email)));
    }

    /**
     * 닉네임 중복 체크 API
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNicknameDuplicate(@RequestParam("nickname") String nickname) {
        // userService.isDuplicate를 호출해서 결과를 값으로 넣어줌
        return ResponseEntity.ok(Collections.singletonMap("isDuplicate", userService.isDuplicate("nickname", nickname)));
    }

    /**
     * 아이디 찾기 API
     */
    @PostMapping("/find-id")
    public ResponseEntity<Map<String, String>> findId(@RequestBody Map<String, String> payload) {
        String nickname = payload.get("nickname");
        String email = payload.get("email");
        String foundId = userService.findIdByNicknameAndEmail(nickname, email);

        Map<String, String> response = new HashMap<>();
        if (foundId != null) {
            response.put("status", "success");
            response.put("id", foundId);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "일치하는 회원이 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 비밀번호 찾기(재설정) API
     */
    @PostMapping("/find-pw")
    public ResponseEntity<Map<String, String>> findPw(@RequestBody Map<String, String> payload) {
        String id = payload.get("id");
        String email = payload.get("email");
        boolean isSuccess = userService.findPwByIdAndEmail(id, email); // boolean 반환하도록 수정 필요

        Map<String, String> response = new HashMap<>();
        if (isSuccess) {
            response.put("status", "success");
            response.put("message", "가입하신 이메일로 임시 비밀번호 관련 안내를 전송했습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "일치하는 회원이 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}