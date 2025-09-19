package com.example.clothing_backend.user;

import com.example.clothing_backend.user.LoginInfo;
import com.example.clothing_backend.user.User;
import com.example.clothing_backend.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final UserService userservice;

    // ------------------ User API ------------------

    @PostMapping("/user/register")
    public String registerUser(@RequestParam String id,
                               @RequestParam String password,
                               @RequestParam String passwordCheck,
                               @RequestParam String nickname,
                               @RequestParam String email) {
        if (!password.equals(passwordCheck)) return "비밀번호 불일치";

        User user = new User();
        user.setId(id);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setEmail(email);

        userservice.addUser(user); // User 객체 넘김

        return "success";
    }

    @PostMapping("/user/login")
    public String loginUser(@RequestParam String id,
                            @RequestParam String password,
                            HttpSession session) {

        User user = userservice.getUser(id);
        if (!user.getPassword().equals(password)) return "로그인 실패";

        // profileImageBase64는 User에 저장
        String base64Image = userservice.getProfileImageBase64(user.getId());
        if (base64Image != null) user.setProfileImageBase64(base64Image);

        session.setAttribute("loginUser", user);

        LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getId(), user.getNickname());
        loginInfo.setRoles(userservice.getRoles(user.getUserId()));
        session.setAttribute("loginInfo", loginInfo);

        return "success";
    }

    @PostMapping("/user/logout")
    public String logoutUser(HttpSession session) {
        session.invalidate();
        return "success";
    }

    // ------------------ MyPage API ------------------

    @PostMapping("/mypage/uploadProfile")
    public String uploadProfile(@RequestParam MultipartFile profileImage, HttpSession session) throws IOException {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "로그인 필요";

        // bytes를 User에 넣기 위해 saveProfileImage 수정 필요
        String base64 = userservice.saveProfileImage(profileImage, loginUser.getId());
        loginUser.setProfileImageBase64(base64);
        session.setAttribute("loginUser", loginUser);
        return "success";
    }

    @PostMapping("/mypage/resetPassword")
    public String resetPassword(@RequestParam String newPassword,
                                @RequestParam String newPasswordCheck,
                                HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "로그인 필요";
        if (!newPassword.equals(newPasswordCheck)) return "비밀번호 불일치";

        userservice.updatePassword(loginUser.getId(), loginUser.getEmail(), newPassword);
        session.invalidate();
        return "success";
    }

    @PostMapping("/mypage/deleteAccount")
    public String deleteAccount(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "로그인 필요";

        userservice.deleteUser(loginUser.getId());
        session.invalidate();
        return "success";
    }
}
