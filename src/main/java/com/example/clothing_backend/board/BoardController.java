package com.example.clothing_backend.board;

import com.example.clothing_backend.user.LoginInfo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // --- HTML 페이지를 보여주는 메소드들 ---

    @GetMapping("/share")
    public String boardList(@PageableDefault(page = 0, size = 10, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<Board> paging = boardService.getBoards(pageable);
        model.addAttribute("paging", paging);
        return "list";
    }

    @GetMapping("/board")
    public String boardDetail(@RequestParam("boardId") long boardId, Model model) {
        model.addAttribute("board", boardService.getBoard(boardId));
        return "detail";
    }

    @GetMapping("/writeform")
    public String writeForm(HttpSession session, Model model) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) return "redirect:/login.html";
        model.addAttribute("loginInfo", loginInfo);
        return "writeform";
    }

    @PostMapping("/write")
    public String write(@RequestParam String title, @RequestParam String content, @RequestParam(required = false) MultipartFile image, HttpSession session) throws IOException {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) return "redirect:/login.html";
        byte[] imageData = (image != null && !image.isEmpty()) ? image.getBytes() : null;
        boardService.addBoard(loginInfo.getNickname(), title, content, loginInfo.getUserId(), imageData, null, null);
        return "redirect:/share";
    }

    @GetMapping("/updateform")
    public String updateForm(@RequestParam("boardId") long boardId, HttpSession session, Model model) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) return "redirect:/login.html";
        Board board = boardService.getBoard(boardId);
        if (!board.getUserId().equals(loginInfo.getUserId())) return "redirect:/share";
        model.addAttribute("board", board);
        model.addAttribute("loginInfo", loginInfo);
        return "updateform";
    }

    @PostMapping("/update")
    public String updateBoard(@RequestParam("boardId") long boardId, @RequestParam("title") String title, @RequestParam("content") String content, HttpSession session) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) return "redirect:/login.html";
        boardService.updateBoardTextOnly(boardId, title, content, loginInfo.getUserId());
        return "redirect:/board?boardId=" + boardId;
    }

    @GetMapping("/delete")
    public String deleteBoard(@RequestParam("boardId") long boardId, HttpSession session) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) return "redirect:/login.html";
        Board board = boardService.getBoard(boardId);
        if (loginInfo.getRoles().contains("ROLE_ADMIN") || board.getUserId().equals(loginInfo.getUserId())) {
            boardService.deleteBoard(boardId);
        }
        return "redirect:/share";
    }

    // --- API (JSON 데이터를 반환하는 메소드들) ---
    // ✅ @ResponseBody 어노테이션을 붙여서 이 메소드들은 데이터를 직접 반환하도록 설정

    @GetMapping("/api/boards")
    @ResponseBody
    public Page<Board> getBoardsApi(@PageableDefault(size = 10, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Board> boardPage = boardService.getBoards(pageable);
        boardPage.getContent().forEach(board -> {
            if (board.getImageData() != null)
                board.setImageBase64(Base64.getEncoder().encodeToString(board.getImageData()));
            if (board.getReviewImage() != null)
                board.setReviewImageBase64(Base64.getEncoder().encodeToString(board.getReviewImage()));
        });
        return boardPage;
    }

    @GetMapping("/api/boards/{boardId}")
    @ResponseBody
    public Board getBoardApi(@PathVariable long boardId) {
        Board board = boardService.getBoard(boardId);
        if (board.getImageData() != null)
            board.setImageBase64(Base64.getEncoder().encodeToString(board.getImageData()));
        if (board.getReviewImage() != null)
            board.setReviewImageBase64(Base64.getEncoder().encodeToString(board.getReviewImage()));
        return board;
    }
}