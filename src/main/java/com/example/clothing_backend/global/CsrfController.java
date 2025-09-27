package com.example.clothing_backend.global;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CsrfController {

    @GetMapping("/api/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        // 현재 세션에 저장된 CSRF 토큰 정보를 반환
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }
}