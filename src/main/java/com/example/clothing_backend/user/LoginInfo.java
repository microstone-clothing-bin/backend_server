package com.example.clothing_backend.user;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class LoginInfo {

    private Long userId;
    private String id;
    private String nickname;
    private List<String> roles;

    public LoginInfo(Long userId, String id, String nickname) {
        this.userId = userId;
        this.id = id;
        this.nickname = nickname;
    }
}