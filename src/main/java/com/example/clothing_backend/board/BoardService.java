package com.example.clothing_backend.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    // List<Board> -> Page<Board> 로 변경, 파라미터도 Pageable로 변경
    public Page<Board> getBoards(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    @Transactional
    public Board getBoard(long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + boardId));
        board.setViewCnt(board.getViewCnt() + 1);
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