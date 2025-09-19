// 보안 설정

package com.example.clothing_backend.global;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests(authz -> authz
                        // 아래 주소들은 로그인 없이 누구나 접근 가능하도록 허용
                        .requestMatchers(
                                "/", "/index.html", "/login.html", "/register.html",
                                "/findIdForm", "/findPwForm", "/userReg", "/findId", "/findPw",
                                "/share", "/board",
                                "/js/**", "/css/**", "/images/**", "/static/**",
                                "/api/user/register",
                                "/api/checkDuplicate",
                                "/api/clothing-bins/**"
                        ).permitAll()
                        // 그 외 나머지 모든 요청은 로그인 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/api/user/login")
                        .usernameParameter("id")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/share")
                        .failureUrl("/login.html?error=true")
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