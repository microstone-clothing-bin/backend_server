// 핸들러

package com.example.clothing_backend.global;

import com.example.clothing_backend.user.LoginInfo;
import com.example.clothing_backend.user.User;
import com.example.clothing_backend.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 로그인 성공하면 Principal 꺼내오기 (UserDetails 상속한 User 객체)
        User user = (User) authentication.getPrincipal();

        // 세션에 넣을 가벼운 로그인 정보 객체 (id, 닉네임, roles만)
        LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getId(), user.getNickname());
        loginInfo.setRoles(userRepository.findRolesByUserId(user.getUserId()));

        // 세션에 저장 (필요할 때 꺼내 쓰려고)
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);       // 전체 User 객체
        session.setAttribute("loginInfo", loginInfo); // 가벼운 LoginInfo 객체

        // 무조건 메인 페이지("/")로 이동하도록 강제 (추후 수정해야 할 수도..)
        response.sendRedirect("/");
    }
}