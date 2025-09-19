// 로그인 직후 loginInfo에 저장

package com.example.clothing_backend.global;

import com.example.clothing_backend.user.LoginInfo;
import com.example.clothing_backend.user.User;
import com.example.clothing_backend.user.UserDao;
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
        // 인증 성공 시 호출됨
        User user = (User) authentication.getPrincipal();

        // 로그인 정보 생성 (세션에 저장할 용도)
        LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getId(), user.getNickname());
        loginInfo.setRoles(userDao.getRoles(user.getUserId()));

        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user); // 실제 User 객체
        session.setAttribute("loginInfo", loginInfo); // 세션용 간략 정보

        // 사용자가 원래 요청한 페이지 없으면 /share로 이동
        setDefaultTargetUrl("/share");

        // 나머지는 부모 클래스(SavedRequestAwareAuthenticationSuccessHandler)에 맡김
        super.onAuthenticationSuccess(request, response, authentication);
    }
}