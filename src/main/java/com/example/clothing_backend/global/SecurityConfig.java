package com.example.clothing_backend.global;

import com.example.clothing_backend.user.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final WebLoginSuccessHandler webLoginSuccessHandler;
    private final ApiLoginSuccessHandler apiLoginSuccessHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- API용 보안 설정 (가장 높은 우선순위) ---
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // Deprecated 코드 수정 완료
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/register", "/api/user/check-duplicate", "/api/clothing-bins/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(form -> form
                        .loginProcessingUrl("/api/login")
                        .usernameParameter("id") // API 로그인도 'id' 파라미터를 사용
                        .successHandler(apiLoginSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            Map<String, Object> data = new HashMap<>();
                            data.put("status", "error");
                            data.put("message", "로그인 정보가 올바르지 않습니다.");
                            response.getWriter().write(objectMapper.writeValueAsString(data));
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            Map<String, Object> data = new HashMap<>();
                            data.put("status", "success");
                            data.put("message", "로그아웃 되었습니다.");
                            response.getWriter().write(objectMapper.writeValueAsString(data));
                        })
                )
                .exceptionHandling(e -> e.authenticationEntryPoint((request, response, authException) ->
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                ));
        return http.build();
    }

    // --- 웹 페이지용 보안 설정 (두 번째 우선순위) ---
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/css/**", "/js/**", "/login", "/login.html",
                                "/register.html", "/userReg", "/findIdForm", "/findId",
                                "/findPwForm", "/findPw", "/share", "/board"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .usernameParameter("id")
                        .passwordParameter("password")
                        .successHandler(webLoginSuccessHandler)
                        .failureUrl("/login.html?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .userDetailsService(customUserDetailsService);

        return http.build();
    }
}