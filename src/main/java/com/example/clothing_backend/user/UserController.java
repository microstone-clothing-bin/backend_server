package com.example.clothing_backend.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // HTML 페이지 라우팅

    // 회원가입 페이지
    @GetMapping("/register.html")
    public String registerForm() {
        return "register"; // templates/register.html
    }

    // 로그인 페이지
    @GetMapping("/login.html")
    public String loginForm() {
        return "login"; // templates/login.html
    }

    // 아이디 찾기 페이지
    @GetMapping("/findIdForm")
    public String findIdForm() {
        return "findId"; // templates/findId.html
    }

    // 비밀번호 찾기 페이지
    @GetMapping("/findPwForm")
    public String findPwForm() {
        return "findPw"; // templates/findPw.html
    }

    // 폼 처리

    // 회원가입 처리 (User 객체 자동 바인딩)
    @PostMapping("/userReg")
    public String processRegistration(User user) {
        userService.addUser(user); // DB에 사용자 저장
        return "reg_success"; // 성공 페이지
    }

    // 아이디 찾기 처리
    @PostMapping("/findId")
    public String findId(@RequestParam String nickname, @RequestParam String email, Model model) {
        String foundId = userService.findIdByNicknameAndEmail(nickname, email);
        if (foundId != null) {
            model.addAttribute("message", "회원님의 아이디는 [ " + foundId + " ] 입니다.");
        } else {
            model.addAttribute("message", "입력하신 정보와 일치하는 회원이 없습니다.");
        }
        return "findId"; // 결과 메시지 출력
    }

    // 비밀번호 찾기 처리
    @PostMapping("/findPw")
    public String findPw(@RequestParam String id, @RequestParam String email, Model model) {
        String foundPw = userService.findPwByIdAndEmail(id, email);
        if (foundPw != null) {
            model.addAttribute("message", "회원님의 비밀번호는 [ " + foundPw + " ] 입니다.");
        } else {
            model.addAttribute("message", "입력하신 정보와 일치하는 회원이 없습니다.");
        }
        return "findPw"; // 결과 메시지 출력
    }

    // API

    // 중복 체크 API (type: "id", "email", "nickname")
    @GetMapping("/api/checkDuplicate")
    @ResponseBody // 문자열 그대로 반환
    public String checkDuplicate(@RequestParam String type, @RequestParam String value) {
        boolean isDuplicate = userService.isDuplicate(type, value);
        return isDuplicate ? "duplicate" : "ok"; // 중복이면 "duplicate", 아니면 "ok"
    }

    // 실제 로그인/로그아웃 처리:
    // SecurityConfig에서 .loginProcessingUrl("/api/user/login")로 처리되므로
    // 이 컨트롤러에서 로그인/로그아웃 메소드 빼놨음
}