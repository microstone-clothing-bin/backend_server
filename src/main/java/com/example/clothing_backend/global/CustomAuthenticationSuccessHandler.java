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

    private final UserRepository userRepository; // ✅ UserDao -> UserRepository로 변경

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getId(), user.getNickname());

        loginInfo.setRoles(userRepository.findRolesByUserId(user.getUserId()));

        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);
        session.setAttribute("loginInfo", loginInfo);

        setDefaultTargetUrl("/share");

        super.onAuthenticationSuccess(request, response, authentication);
    }
}