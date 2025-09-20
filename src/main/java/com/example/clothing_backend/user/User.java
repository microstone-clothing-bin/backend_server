package com.example.clothing_backend.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "`user`")
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String email;

    private LocalDateTime redate;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    private byte[] profileImageBlob;

    @Transient
    private String profileImageBase64;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role", // 중간 매핑 테이블
            joinColumns = @JoinColumn(name = "user_id"), // User 테이블의 FK
            inverseJoinColumns = @JoinColumn(name = "role_id") // Role 테이블의 FK
    )
    private Set<Role> roles = new HashSet<>();


    // --- UserDetails 인터페이스 메소드 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() { return this.id; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}