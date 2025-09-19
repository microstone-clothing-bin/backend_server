package com.example.clothing_backend.user;

import com.example.clothing_backend.user.dao.UserDao;
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

    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 로그인 폼에서 입력한 아이디(id)가 username 파라미터로 들어옴
        User user = userDao.getUserById(username);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        // 해당 사용자의 권한(role) 정보를 가져와서 Security가 이해할 수 있는 형태로 변환
        List<GrantedAuthority> authorities = userDao.getRoles(user.getUserId()).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        user.setAuthorities(authorities); // User 객체에 권한 정보 설정
        return user;
    }
}