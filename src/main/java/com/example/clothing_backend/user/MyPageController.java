package com.example.clothing_backend.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;

    @GetMapping
    public String myPage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login.html";
        }
        // DB에서 최신 프로필 이미지를 다시 불러와서 세션과 모델에 업데이트
        String base64Image = userService.getProfileImageBase64(loginUser.getId());
        loginUser.setProfileImageBase64(base64Image);
        session.setAttribute("loginUser", loginUser);

        model.addAttribute("loginUser", loginUser);
        return "mypage";
    }

    @PostMapping("/uploadProfile")
    public String uploadProfile(@RequestParam MultipartFile profileImage, HttpSession session) throws IOException {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login.html";
        }

        String base64 = userService.saveProfileImage(profileImage, loginUser.getId());

        loginUser.setProfileImageBase64(base64);

        session.setAttribute("loginUser", loginUser);
        return "redirect:/mypage";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam String newPassword,
                                @RequestParam String newPasswordCheck,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login.html";
        }
        if (!newPassword.equals(newPasswordCheck)) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.");
            return "redirect:/mypage";
        }

        userService.updatePassword(loginUser.getId(), loginUser.getEmail(), newPassword);
        session.invalidate();
        return "redirect:/login.html";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login.html";
        }

        userService.deleteUser(loginUser.getId());
        session.invalidate();
        return "redirect:/";
    }
}