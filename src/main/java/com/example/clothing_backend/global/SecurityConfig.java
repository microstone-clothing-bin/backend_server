package com.example.clothing_backend.global.config;

import com.example.clothing_backend.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호 암호화용 BCrypt 인코더
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // 인증 없이 접근 가능한 URL 목록
                        .requestMatchers(
                                "/", "/login", "/login.html", "/register.html", "/userReg",
                                "/findIdForm", "/findId", "/findPwForm", "/findPw",
                                "/share", "/board",
                                "/css/**", "/js/**", "/api/**"
                        ).permitAll()
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .formLogin(form -> form
                        .loginPage("/login.html") // 커스텀 로그인 페이지
                        .loginProcessingUrl("/login") // 로그인 처리 URL
                        .usernameParameter("id") // 폼에서 id 사용
                        .passwordParameter("password") // 폼에서 password 사용
                        .defaultSuccessUrl("/", false) // 로그인 성공 후 이동 (true -> 항상, false -> 원래 요청 페이지 우선)
                        .failureUrl("/login.html?error=true") // 로그인 실패 시 이동
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 URL
                        .logoutSuccessUrl("/") // 로그아웃 후 이동
                )
                .userDetailsService(customUserDetailsService); // 사용자 인증 로직

        return http.build();
    }
}