package com.example.clothing_backend.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;

    // 마이페이지 뷰를 보여주는 메소드
    @GetMapping
    public String myPage(HttpSession session, Model model) {
        // 세션에서 로그인 정보를 가져옴
        User loginUser = (User) session.getAttribute("loginUser");

        // 로그인 정보가 없으면 로그인 페이지로 리다이렉트
        if (loginUser == null) {
            return "redirect:/login.html";
        }

        // DB에서 최신 프로필 이미지를 다시 불러와서 세션과 모델에 업데이트
        String base64Image = userService.getProfileImageBase64(loginUser.getId());
        if (base64Image != null) {
            loginUser.setProfileImageBase64(base64Image);
            session.setAttribute("loginUser", loginUser);
        }

        // 모델에 사용자 정보를 담아서 mypage.html로 전달
        model.addAttribute("loginUser", loginUser);
        return "mypage"; // templates/mypage.html
    }

    // 프로필 이미지 업로드 처리
    @PostMapping("/uploadProfile")
    public String uploadProfile(@RequestParam MultipartFile profileImage, HttpSession session) throws IOException {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login.html";
        }

        userService.saveProfileImage(profileImage, loginUser.getId());
        return "redirect:/mypage"; // 프로필 변경 후 마이페이지로 새로고침
    }

    // 비밀번호 재설정 처리
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

    // 회원 탈퇴 처리
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