package com.example.clothing_backend.board;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "board")
@Getter
@Setter
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    private String title;
    private String content;
    private String nickname;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime redate;

    private Long userId;
    private int viewCnt;

    @Lob
    // DB의 LONGBLOB 타입과 정확히 일치하도록 columnDefinition 추가
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    private String reviewText;

    @Lob
    // DB의 LONGBLOB 타입과 정확히 일치하도록 columnDefinition 추가
    @Column(name = "review_image", columnDefinition = "LONGBLOB")
    private byte[] reviewImage;

    @Transient
    private String imageBase64;

    @Transient
    private String reviewImageBase64;

    @PrePersist
    public void prePersist() {
        this.redate = LocalDateTime.now();
    }
}