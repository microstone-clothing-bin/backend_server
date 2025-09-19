// 로그인 직후 loginInfo에 저장

package com.example.clothing_backend.global;

import com.example.clothing_backend.user.LoginInfo;
import com.example.clothing_backend.user.User;
import com.example.clothing_backend.user.dao.UserDao;
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

    private final UserDao userDao;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getId(), user.getNickname());
        loginInfo.setRoles(userDao.getRoles(user.getUserId()));

        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);
        session.setAttribute("loginInfo", loginInfo);

        // 사용자가 원래 가려던 곳이 없을 때 보낼 기본 주소 설정
        setDefaultTargetUrl("/share");

        // 나머지는 똑똑한 부모 클래스에 맡기기
        super.onAuthenticationSuccess(request, response, authentication);
    }
}