// 보안 설정

package com.example.clothing_backend.global;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 기능은 form 로그인을 위해 기본적으로 활성화 상태를 유지
                // .csrf(disable)을 하지 않으면 자동으로 켜짐

                .authorizeHttpRequests(authz -> authz
                        // 아래 주소들은 로그인 없이 누구나 접근 가능하도록 허용
                        .requestMatchers(
                                "/", "/index.html", "/login.html", "/register.html",
                                "/findIdForm", "/findPwForm", "/userReg", "/findId", "/findPw",
                                "/share", "/board",
                                "/js/**", "/css/**", "/images/**", "/static/**",
                                "/api/**" // API 경로는 일단 모두 허용
                        ).permitAll()
                        // 그 외 나머지 모든 요청은 로그인 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login.html") // 커스텀 로그인 페이지 경로
                        .loginProcessingUrl("/login") // 로그인을 처리할 URL (Spring Security가 이 주소를 감시함)
                        .usernameParameter("id") // 로그인 폼에서 아이디 필드의 name
                        .passwordParameter("password") // 로그인 폼에서 비밀번호 필드의 name
                        .defaultSuccessUrl("/share", true) // 로그인 성공 시 무조건 게시판 목록으로 이동
                        .failureUrl("/login.html?error=true") // 로그인 실패 시 에러 파라미터와 함께 로그인 페이지로
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // POST 요청으로 로그아웃
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}