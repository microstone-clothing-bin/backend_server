package com.example.clothing_backend.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`user`") // user는 SQL 예약어이므로 ``로 감싸주었음
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // ✅ int -> Long 으로 수정
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private LocalDateTime redate;

    @Lob
    private byte[] profileImage;

    private String profileImageName;

    @Lob
    private byte[] profileImageBlob;

    @Transient // DB 컬럼과 매핑하지 않음
    private String profileImageBase64;
}