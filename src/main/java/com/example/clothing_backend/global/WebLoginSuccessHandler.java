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
public class WebLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 로그인 성공한 User 정보 가져오기
        User user = (User) authentication.getPrincipal();

        // 세션에 저장할 가벼운 로그인 정보 객체 생성
        LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getId(), user.getNickname());
        loginInfo.setRoles(userRepository.findRolesByUserId(user.getUserId()));

        // 세션에 사용자 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);
        session.setAttribute("loginInfo", loginInfo);

        // 사용자를 원래 가려던 페이지나 기본 페이지("/")로 리다이렉트
        setDefaultTargetUrl("/");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}