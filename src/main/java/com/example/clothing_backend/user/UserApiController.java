package com.example.clothing_backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    // 신규: 회원가입 API
    @PostMapping("/register")
    public Map<String, Object> registerUser(@RequestParam String id,
                                            @RequestParam String password,
                                            @RequestParam String passwordCheck,
                                            @RequestParam String nickname,
                                            @RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        if (!password.equals(passwordCheck)) {
            response.put("status", "error");
            response.put("message", "비밀번호가 일치하지 않습니다.");
            return response;
        }

        // TODO: 각 항목에 대한 중복 체크 로직을 여기에 추가하면 더 좋습니다.

        User user = new User();
        user.setId(id);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setEmail(email);

        userService.addUser(user);

        response.put("status", "success");
        response.put("message", "회원가입이 성공적으로 완료되었습니다.");
        return response;
    }

    // 기존: 중복 체크 API
    @GetMapping("/check-duplicate")
    public Map<String, Object> checkDuplicate(@RequestParam String type, @RequestParam String value) {
        Map<String, Object> response = new HashMap<>();
        boolean isDuplicate = false;

        if ("id".equals(type)) {
            isDuplicate = userService.isUserIdDuplicate(value);
        } else if ("nickname".equals(type)) {
            isDuplicate = userService.isNicknameDuplicate(value);
        } else if ("email".equals(type)) {
            isDuplicate = userService.isEmailDuplicate(value);
        }

        response.put("isDuplicate", isDuplicate);
        return response;
    }
}