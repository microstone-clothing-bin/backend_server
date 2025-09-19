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

    // --- HTML 페이지 라우팅 ---
    @GetMapping("/register.html")
    public String registerForm() {
        return "register";
    }

    @GetMapping("/login.html")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/findIdForm")
    public String findIdForm() {
        return "findId";
    }

    @GetMapping("/findPwForm")
    public String findPwForm() {
        return "findPw";
    }

    // --- 폼 처리 ---
    @PostMapping("/userReg")
    public String processRegistration(User user) { // User 객체로 바로 받음
        userService.addUser(user);
        return "reg_success";
    }

    @PostMapping("/findId")
    public String findId(@RequestParam String nickname, @RequestParam String email, Model model) {
        String foundId = userService.findIdByNicknameAndEmail(nickname, email);
        if (foundId != null) {
            model.addAttribute("message", "회원님의 아이디는 [ " + foundId + " ] 입니다.");
        } else {
            model.addAttribute("message", "입력하신 정보와 일치하는 회원이 없습니다.");
        }
        return "findId";
    }

    @PostMapping("/findPw")
    public String findPw(@RequestParam String id, @RequestParam String email, Model model) {
        String foundPw = userService.findPwByIdAndEmail(id, email);
        if (foundPw != null) {
            model.addAttribute("message", "회원님의 비밀번호는 [ " + foundPw + " ] 입니다.");
        } else {
            model.addAttribute("message", "입력하신 정보와 일치하는 회원이 없습니다.");
        }
        return "findPw";
    }

    // --- API (JSON 등 데이터 반환) ---
    @GetMapping("/api/checkDuplicate")
    @ResponseBody
    public String checkDuplicate(@RequestParam String type, @RequestParam String value) {
        boolean isDuplicate = userService.isDuplicate(type, value);
        return isDuplicate ? "duplicate" : "ok";
    }

    // 실제 로그인/로그아웃 처리는 SecurityConfig가 담당하므로
    // @PostMapping("/api/user/login")과 logout 메소드는 여기서는 내가 뺏음
    // SecurityConfig의 .loginProcessingUrl("/api/user/login") 설정이 대신 처리중
}