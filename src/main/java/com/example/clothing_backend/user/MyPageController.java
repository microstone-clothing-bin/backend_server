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
@RequestMapping("/mypage") // /mypage로 들어오는 모든 요청을 처리
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService; // 유저 관련 서비스 주입

    // 마이페이지 화면
    @GetMapping
    public String myPage(HttpSession session, Model model) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User loginUser = (User) session.getAttribute("loginUser");

        // 로그인 정보가 없으면 로그인 페이지로 리다이렉트
        if (loginUser == null) {
            return "redirect:/login.html";
        }

        // DB에서 최신 프로필 이미지를 가져와 세션과 모델 업데이트
        String base64Image = userService.getProfileImageBase64(loginUser.getId());
        if (base64Image != null) {
            loginUser.setProfileImageBase64(base64Image); // User 객체에 이미지 반영
            session.setAttribute("loginUser", loginUser); // 세션 갱신
        }

        // 모델에 사용자 정보를 담아 뷰로 전달
        model.addAttribute("loginUser", loginUser);
        return "mypage"; // templates/mypage.html 렌더링
    }

    // 프로필 이미지 업로드
    @PostMapping("/uploadProfile")
    public String uploadProfile(@RequestParam MultipartFile profileImage, HttpSession session) throws IOException {
        User loginUser = (User) session.getAttribute("loginUser");

        // 로그인 안 되어 있으면 로그인 페이지로
        if (loginUser == null) {
            return "redirect:/login.html";
        }

        // 업로드된 이미지 저장
        userService.saveProfileImage(profileImage, loginUser.getId());

        // 업로드 후 마이페이지 새로고침
        return "redirect:/mypage";
    }

    // 비밀번호 재설정
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam String newPassword,
                                @RequestParam String newPasswordCheck,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute("loginUser");

        // 로그인 안 되어 있으면 로그인 페이지로
        if (loginUser == null) {
            return "redirect:/login.html";
        }

        // 새 비밀번호 확인이 일치하지 않으면 에러 메시지
        if (!newPassword.equals(newPasswordCheck)) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.");
            return "redirect:/mypage";
        }

        // 비밀번호 업데이트
        userService.updatePassword(loginUser.getId(), loginUser.getEmail(), newPassword);

        // 비밀번호 변경 후 세션 무효화 (다시 로그인 필요)
        session.invalidate();
        return "redirect:/login.html";
    }

    // 회원 탈퇴
    @PostMapping("/deleteAccount")
    public String deleteAccount(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        // 로그인 안 되어 있으면 로그인 페이지로
        if (loginUser == null) {
            return "redirect:/login.html";
        }

        // 회원 삭제
        userService.deleteUser(loginUser.getId());

        // 세션 무효화 후 홈으로 리다이렉트
        session.invalidate();
        return "redirect:/";
    }
}