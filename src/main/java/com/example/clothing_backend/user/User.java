// Spring Security 연동

package com.example.clothing_backend.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "`user`")
@Getter
@Setter
public class User implements UserDetails { // ✅ UserDetails 구현 추가

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    private LocalDateTime redate;

    @Lob
    private byte[] profileImageBlob;

    @Transient
    private String profileImageBase64;

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    // --- UserDetails 인터페이스 메소드 구현 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getUsername() {
        return this.id; // Spring Security는 id를 username으로 인식
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}