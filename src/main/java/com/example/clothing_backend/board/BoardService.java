package com.example.clothing_backend.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    public List<Board> getBoards(int page) {
        // (추후 페이지네이션 로직 구현 필요)
        return boardRepository.findAll();
    }

    @Transactional
    public Board getBoard(long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + boardId));
        board.setViewCnt(board.getViewCnt() + 1); // 조회수 증가
        return board;
    }

    @Transactional
    public void addBoard(String nickname, String title, String content, Long userId, byte[] imageData, String reviewText, byte[] reviewImageData) {
        Board board = new Board();
        board.setNickname(nickname);
        board.setTitle(title);
        board.setContent(content);
        board.setUserId(userId);
        board.setImageData(imageData);
        board.setReviewText(reviewText);
        board.setReviewImage(reviewImageData);
        boardRepository.save(board);
    }

    @Transactional
    public void updateBoardTextOnly(long boardId, String title, String content, Long loginUserId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + boardId));

        if (!board.getUserId().equals(loginUserId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }
        board.setTitle(title);
        board.setContent(content);
    }

    @Transactional
    public void deleteBoard(long boardId) {
        boardRepository.deleteById(boardId);
    }

    @Transactional
    public void deleteBoard(Long userId, long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + boardId));
        if (!board.getUserId().equals(userId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }
        boardRepository.delete(board);
    }
}