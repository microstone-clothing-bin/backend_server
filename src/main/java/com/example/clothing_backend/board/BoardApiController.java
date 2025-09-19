// 순수 데이터(JSON)만 다루는 컨트롤러

package com.example.clothing_backend.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;

    // List<Board> -> Page<Board> 로 변경, 파라미터도 Pageable로 변경
    @GetMapping
    public Page<Board> getBoards(
            @PageableDefault(size = 10, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Board> boardPage = boardService.getBoards(pageable);

        // Base64 인코딩 로직은 Page 객체의 내용물(content)에 적용
        boardPage.getContent().forEach(board -> {
            if (board.getImageData() != null)
                board.setImageBase64(Base64.getEncoder().encodeToString(board.getImageData()));
            if (board.getReviewImage() != null)
                board.setReviewImageBase64(Base64.getEncoder().encodeToString(board.getReviewImage()));
        });

        return boardPage;
    }

    @GetMapping("/{boardId}")
    public Board getBoard(@PathVariable long boardId) {
        Board board = boardService.getBoard(boardId);
        if (board.getImageData() != null)
            board.setImageBase64(Base64.getEncoder().encodeToString(board.getImageData()));
        if (board.getReviewImage() != null)
            board.setReviewImageBase64(Base64.getEncoder().encodeToString(board.getReviewImage()));
        return board;
    }
}