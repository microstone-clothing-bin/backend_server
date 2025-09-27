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

package com.example.clothing_backend.global;

import com.example.clothing_backend.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // [추가] AuthenticationManager를 Bean으로 등록해서 다른 곳에서 주입받아 쓸 수 있게 함
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // API 요청에 대해서는 CSRF 검사를 하지 않도록 설정
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

                .authorizeHttpRequests(authz -> authz
                        // [수정] 새로 만든 API 주소를 포함하여 인증 없이 접근 가능한 경로 설정
                        .requestMatchers(
                                "/", "/css/**", "/js/**", "/api/user/**", // /api/user/ 하위 모든 경로 허용
                                "/login", "/login.html", "/register.html"
                                // ... 기타 웹 페이지 경로들
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // [수정] formLogin은 이제 순수 웹 전용. 핸들러 없이 기본 설정 사용.
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login") // HTML form의 action과 일치
                        .usernameParameter("id")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true) // 성공 시 무조건 메인페이지로 이동
                        .failureUrl("/login.html?error=true") // 실패 시 에러 파라미터와 함께 로그인 페이지로
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
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