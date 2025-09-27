// 보안 설정 (최종 수정 완료)

package com.example.clothing_backend.global;

import com.example.clothing_backend.user.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                response.setCharacterEncoding("UTF-8");
                                Map<String, String> errorDetails = new HashMap<>();
                                errorDetails.put("status", "error");
                                errorDetails.put("message", "로그인이 필요합니다.");
                                response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
                            } else {
                                response.sendRedirect("/login.html");
                            }
                        })
                )

                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/", "/css/**", "/js/**", "/api/**",
                                "/login", "/login.html", "/register.html", "/userReg",
                                "/findIdForm", "/findId", "/findPwForm", "/findPw",
                                "/share", "/board"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .usernameParameter("id")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler())
                        .failureHandler(loginFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    // 로그인 성공 핸들러: API와 웹 요청을 구분
    private AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            String accept = request.getHeader("Accept");

            // API 클라이언트 요청인지 확인 (Accept 헤더 기준)
            if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
                response.setStatus(HttpStatus.OK.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                Map<String, String> successDetails = new HashMap<>();
                successDetails.put("status", "success");
                successDetails.put("message", "로그인에 성공했습니다.");
                response.getWriter().write(objectMapper.writeValueAsString(successDetails));
            } else {
                // 일반 브라우저 요청이면 메인 페이지로 리다이렉트
                response.sendRedirect("/");
            }
        };
    }

    // 로그인 실패 핸들러: API와 웹 요청을 구분
    private AuthenticationFailureHandler loginFailureHandler() {
        return (request, response, exception) -> {
            String accept = request.getHeader("Accept");

            if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                Map<String, String> errorDetails = new HashMap<>();
                errorDetails.put("status", "error");
                errorDetails.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
                response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
            } else {
                // 일반 브라우저 요청이면 로그인 페이지로 리다이렉트
                response.sendRedirect("/login.html?error=true");
            }
        };
    }

    // 로그아웃 성공 핸들러: API와 웹 요청을 구분
    private LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            String accept = request.getHeader("Accept");

            if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
                response.setStatus(HttpStatus.OK.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                Map<String, String> successDetails = new HashMap<>();
                successDetails.put("status", "success");
                successDetails.put("message", "로그아웃 되었습니다.");
                response.getWriter().write(objectMapper.writeValueAsString(successDetails));
            } else {
                response.sendRedirect("/");
            }
        };
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}