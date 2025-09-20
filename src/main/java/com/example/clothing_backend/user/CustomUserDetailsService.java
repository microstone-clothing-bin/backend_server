package com.example.clothing_backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // ✅ UserDao -> UserRepository 로 변경

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        List<GrantedAuthority> authorities = userRepository.findRolesByUserId(user.getUserId()).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // User 객체는 UserDetails를 구현했으므로 바로 사용 가능
        // (단, User 객체에 권한을 설정해주는 로직이 필요하다면 추가해야 함)
        return user;
    }
}