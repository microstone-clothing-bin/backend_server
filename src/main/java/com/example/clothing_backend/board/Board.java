package com.example.clothing_backend.board;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity // JPA에서 이 클래스가 DB 테이블이랑 매핑된다는 뜻
@Table(name = "board") // 실제 DB 테이블 이름 지정
@Getter
@Setter
public class Board {

    @Id // 기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // auto_increment (DB에서 자동 증가)
    private Long boardId;

    private String title;
    private String content;
    private String nickname;

    @Column(columnDefinition = "DATETIME")
    // MySQL에서 DATETIME으로 명시
    private LocalDateTime redate;

    private Long userId;
    private int viewCnt; // 조회수

    @Lob
    // 대용량 바이너리 데이터 저장 (이미지)
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    private String reviewText;

    @Lob
    // 리뷰용 이미지 따로 저장
    @Column(name = "review_image", columnDefinition = "LONGBLOB")
    private byte[] reviewImage;

    @Transient
    // DB에 안 넣고 서버에서만 쓰는 값 (base64 변환용)
    private String imageBase64;

    @Transient
    private String reviewImageBase64;

    @PrePersist
    // INSERT 되기 전에 실행되는 콜백
    public void prePersist() {
        this.redate = LocalDateTime.now(); // 등록 시간 자동 저장
    }
}