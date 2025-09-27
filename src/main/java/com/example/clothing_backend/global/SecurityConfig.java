//// 보안 설정 (진짜 최종 수정 완료)
//
//package com.example.clothing_backend.global;
//
//import com.example.clothing_backend.user.CustomUserDetailsService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final CustomUserDetailsService customUserDetailsService;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
//
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            // API 요청에 대해 401 에러와 JSON 응답
//                            if (request.getRequestURI().startsWith("/api/")) {
//                                response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                                response.setCharacterEncoding("UTF-8");
//                                Map<String, String> errorDetails = new HashMap<>();
//                                errorDetails.put("status", "error");
//                                errorDetails.put("message", "로그인이 필요합니다.");
//                                response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
//                            } else {
//                                response.sendRedirect("/login.html");
//                            }
//                        })
//                )
//
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers(
//                                "/", "/css/**", "/js/**", "/api/**",
//                                "/login", "/login.html", "/register.html", "/userReg",
//                                "/findIdForm", "/findId", "/findPwForm", "/findPw",
//                                "/share", "/board"
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login.html")
//                        .loginProcessingUrl("/login")
//                        .usernameParameter("id")
//                        .passwordParameter("password")
//                        .successHandler(loginSuccessHandler())
//                        .failureHandler(loginFailureHandler())
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessHandler(logoutSuccessHandler())
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                )
//                .userDetailsService(customUserDetailsService);
//
//        return http.build();
//    }
//
//    // 로그인 성공 핸들러
//    private AuthenticationSuccessHandler loginSuccessHandler() {
//        return (request, response, authentication) -> {
//            String accept = request.getHeader("Accept");
//            // 브라우저 요청인지 확인
//            boolean isBrowser = accept != null && accept.contains("text/html");
//
//            if (!isBrowser) {
//                // 브라우저가 아니면 (API 요청이면) JSON 응답
//                response.setStatus(HttpStatus.OK.value());
//                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                response.setCharacterEncoding("UTF-8");
//                Map<String, String> successDetails = new HashMap<>();
//                successDetails.put("status", "success");
//                successDetails.put("message", "로그인에 성공했습니다.");
//                response.getWriter().write(objectMapper.writeValueAsString(successDetails));
//            } else {
//                // 브라우저 요청이면 메인 페이지로 리다이렉트
//                response.sendRedirect("/");
//            }
//        };
//    }
//
//    // 로그인 실패 핸들러
//    private AuthenticationFailureHandler loginFailureHandler() {
//        return (request, response, exception) -> {
//            String accept = request.getHeader("Accept");
//            // 브라우저 요청인지 확인
//            boolean isBrowser = accept != null && accept.contains("text/html");
//
//            if (!isBrowser) {
//                // API 요청이면 JSON 응답
//                response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                response.setCharacterEncoding("UTF-8");
//                Map<String, String> errorDetails = new HashMap<>();
//                errorDetails.put("status", "error");
//                errorDetails.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
//                response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
//            } else {
//                // 브라우저 요청이면 로그인 페이지로 리다이렉트
//                response.sendRedirect("/login.html?error=true");
//            }
//        };
//    }
//
//    // 로그아웃 성공 핸들러
//    private LogoutSuccessHandler logoutSuccessHandler() {
//        return (request, response, authentication) -> {
//            String accept = request.getHeader("Accept");
//            // 브라우저 요청인지 확인
//            boolean isBrowser = accept != null && accept.contains("text/html");
//
//            if (!isBrowser) {
//                // API 요청이면 JSON 응답
//                response.setStatus(HttpStatus.OK.value());
//                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                response.setCharacterEncoding("UTF-8");
//                Map<String, String> successDetails = new HashMap<>();
//                successDetails.put("status", "success");
//                successDetails.put("message", "로그아웃 되었습니다.");
//                response.getWriter().write(objectMapper.writeValueAsString(successDetails));
//            } else {
//                // 브라우저 요청이면 메인 페이지로 리다이렉트
//                response.sendRedirect("/");
//            }
//        };
//    }
//
//    // CORS 설정
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}

// 보안 설정 (CSRF 켜고 API와 공존하는 최종 버전)

package com.example.clothing_backend.global;

import com.example.clothing_backend.user.CustomUserDetailsService;
import com.example.clothing_backend.user.LoginInfo;
import com.example.clothing_backend.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
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
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(requestHandler)
                )

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
                                "/", "/css/**", "/js/**", "/api/**", // /api/** 는 일단 열어두고 각 컨트롤러에서 세부 설정
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
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    private AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;
                HttpSession session = request.getSession();
                session.setAttribute("loginUser", user);
                LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getId(), user.getNickname());
                session.setAttribute("loginInfo", loginInfo);
            }

            String accept = request.getHeader("Accept");
            if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
                response.setStatus(HttpStatus.OK.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                Map<String, String> successDetails = new HashMap<>();
                successDetails.put("status", "success");
                successDetails.put("message", "로그인에 성공했습니다.");
                response.getWriter().write(objectMapper.writeValueAsString(successDetails));
            } else {
                response.sendRedirect("/");
            }
        };
    }

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
                response.sendRedirect("/login.html?error=true");
            }
        };
    }

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