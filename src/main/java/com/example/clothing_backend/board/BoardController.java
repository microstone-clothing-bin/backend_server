// 사용자가 보는 HTML 페이지만을 담당하는 컨트롤러

package com.example.clothing_backend.board;

import com.example.clothing_backend.user.LoginInfo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시판 목록 페이지
    @GetMapping("/share")
    public String boardList(@RequestParam(defaultValue = "1") int page, Model model) {
        model.addAttribute("list", boardService.getBoards(page));
        // pageCount 등 페이지네이션 정보 추가 필요
        return "list"; // templates/list.html
    }

    // 게시판 상세 페이지
    @GetMapping("/board")
    public String boardDetail(@RequestParam("boardId") long boardId, Model model) {
        model.addAttribute("board", boardService.getBoard(boardId));
        return "detail"; // templates/detail.html
    }

    // 글쓰기 폼 페이지
    @GetMapping("/writeform")
    public String writeForm(HttpSession session, Model model) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) return "redirect:/login.html";
        model.addAttribute("loginInfo", loginInfo);
        return "writeform"; // templates/writeform.html
    }

    // 글쓰기 처리
    @PostMapping("/write")
    public String write(@RequestParam String title, @RequestParam String content, @RequestParam(required = false) MultipartFile image, HttpSession session) throws IOException {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) return "redirect:/login.html";
        byte[] imageData = (image != null && !image.isEmpty()) ? image.getBytes() : null;
        boardService.addBoard(loginInfo.getNickname(), title, content, loginInfo.getUserId(), imageData, null, null);
        return "redirect:/share";
    }

    // 수정 폼 페이지
    @GetMapping("/updateform")
    public String updateForm(@RequestParam("boardId") long boardId, HttpSession session, Model model) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) return "redirect:/login.html";
        Board board = boardService.getBoard(boardId);
        if (!board.getUserId().equals(loginInfo.getUserId())) return "redirect:/share";
        model.addAttribute("board", board);
        model.addAttribute("loginInfo", loginInfo);
        return "updateform"; // templates/updateform.html
    }

    // 수정 처리
    @PostMapping("/update")
    public String updateBoard(@RequestParam("boardId") long boardId, @RequestParam("title") String title, @RequestParam("content") String content, HttpSession session) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) return "redirect:/login.html";
        boardService.updateBoardTextOnly(boardId, title, content, loginInfo.getUserId());
        return "redirect:/board?boardId=" + boardId;
    }

    // 삭제 처리
    @GetMapping("/delete") // POST가 더 안전하지만, 임시로 GET으로 처리 (나중에 POST 방식으로 바꿔야 함)
    public String deleteBoard(@RequestParam("boardId") long boardId, HttpSession session) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) return "redirect:/login.html";

        Board board = boardService.getBoard(boardId);
        if (loginInfo.getRoles().contains("ROLE_ADMIN") || board.getUserId().equals(loginInfo.getUserId())) {
            boardService.deleteBoard(boardId);
        }
        return "redirect:/share";
    }
}