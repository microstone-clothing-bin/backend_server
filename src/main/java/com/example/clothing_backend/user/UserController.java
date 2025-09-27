package com.example.clothing_backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // 유저 관련 비즈니스 로직 처리 서비스

    @GetMapping("/register.html")
    public String registerForm() { return "register"; } // 회원가입 폼

    @GetMapping("/login.html")
    public String loginForm() { return "login"; } // 로그인 폼

    @GetMapping("/findIdForm")
    public String findIdForm() { return "findId"; } // 아이디 찾기 폼

    @GetMapping("/findPwForm")
    public String findPwForm() { return "findPw"; } // 비밀번호 찾기 폼

    @PostMapping("/userReg")
    public String processRegistration(User user) {
        userService.addUser(user); // 새 유저 DB에 저장
        return "reg_success"; // 회원가입 완료 페이지로 이동
    }

    @PostMapping("/findId")
    public String findId(@RequestParam String nickname, @RequestParam String email, Model model) {
        String foundId = userService.findIdByNicknameAndEmail(nickname, email);
        // 찾은 ID가 있으면 메시지 세팅, 없으면 에러 메시지
        model.addAttribute("message", foundId != null
                ? "회원님의 아이디는 [ " + foundId + " ] 입니다."
                : "일치하는 회원이 없습니다.");
        return "findId";
    }

    @PostMapping("/findPw")
    public String findPw(@RequestParam String id, @RequestParam String email, Model model) {
        // 비밀번호 초기화 (임시 비밀번호 메일 전송 등)
        userService.findPwByIdAndEmail(id, email);
        model.addAttribute("message", "가입하신 이메일로 임시 비밀번호 관련 안내를 전송했습니다.");
        return "findPw";
    }

    @GetMapping("/api/checkDuplicate")
    @ResponseBody
    public String checkDuplicate(@RequestParam String type, @RequestParam String value) {
        // type = "id" 또는 "nickname"
        // 중복이면 "duplicate", 사용 가능하면 "ok"
        return userService.isDuplicate(type, value) ? "duplicate" : "ok";
    }
}
