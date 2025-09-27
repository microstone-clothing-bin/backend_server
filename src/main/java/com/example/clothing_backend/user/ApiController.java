//package com.example.clothing_backend.user;
//
//import com.example.clothing_backend.user.LoginInfo;
//import com.example.clothing_backend.user.User;
//import com.example.clothing_backend.user.UserService;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class ApiController {
//
//    private final UserService userservice;
//
//    // User API
//
//    @PostMapping("/user/register")
//    public String registerUser(@RequestParam String id,
//                               @RequestParam String password,
//                               @RequestParam String passwordCheck,
//                               @RequestParam String nickname,
//                               @RequestParam String email) {
//        if (!password.equals(passwordCheck)) return "비밀번호 불일치";
//
//        User user = new User();
//        user.setId(id);
//        user.setPassword(password);
//        user.setNickname(nickname);
//        user.setEmail(email);
//
//        userservice.addUser(user); // User 객체를 서비스에 넘김
//
//        return "success";
//    }
//
//    @PostMapping("/user/login")
//    public String loginUser(@RequestParam String id,
//                            @RequestParam String password,
//                            HttpSession session) {
//
//        User user = userservice.getUser(id);
//        if (!user.getPassword().equals(password)) return "로그인 실패";
//
//        // 프로필 이미지 Base64 세팅
//        String base64Image = userservice.getProfileImageBase64(user.getId());
//        if (base64Image != null) user.setProfileImageBase64(base64Image);
//
//        session.setAttribute("loginUser", user);
//
//        // 로그인 정보 세션에 저장 (권한 포함)
//        LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getId(), user.getNickname());
//        loginInfo.setRoles(userservice.getRoles(user.getUserId()));
//        session.setAttribute("loginInfo", loginInfo);
//
//        return "success";
//    }
//
//    @PostMapping("/user/logout")
//    public String logoutUser(HttpSession session) {
//        session.invalidate(); // 세션 삭제
//        return "success";
//    }
//
//    // MyPage API
//
//    @CrossOrigin(
//            origins = {"http://localhost:5173"},      allowCredentials = "true"
//    ) // 도메인 허용 쿠키 허용 쿠키허용 추가
//
//    @PostMapping("/mypage/uploadProfile")
//    public String uploadProfile(@RequestParam MultipartFile profileImage, HttpSession session) throws IOException {
//        User loginUser = (User) session.getAttribute("loginUser");
//        if (loginUser == null) return "로그인 필요";
//
//        // 이미지 bytes를 Base64로 변환 후 User에 세팅
//        String base64 = userservice.saveProfileImage(profileImage, loginUser.getId());
//        loginUser.setProfileImageBase64(base64);
//        session.setAttribute("loginUser", loginUser);
//        return "success";
//    }
//
//    @PostMapping("/mypage/resetPassword")
//    public String resetPassword(@RequestParam String newPassword,
//                                @RequestParam String newPasswordCheck,
//                                HttpSession session) {
//
//        User loginUser = (User) session.getAttribute("loginUser");
//        if (loginUser == null) return "로그인 필요";
//        if (!newPassword.equals(newPasswordCheck)) return "비밀번호 불일치";
//
//        userservice.updatePassword(loginUser.getId(), loginUser.getEmail(), newPassword);
//        session.invalidate(); // 비밀번호 변경 후 세션 초기화
//        return "success";
//    }
//
//    @PostMapping("/mypage/deleteAccount")
//    public String deleteAccount(HttpSession session) {
//        User loginUser = (User) session.getAttribute("loginUser");
//        if (loginUser == null) return "로그인 필요";
//
//        userservice.deleteUser(loginUser.getId());
//        session.invalidate(); // 탈퇴 후 세션 초기화
//        return "success";
//    }
//}