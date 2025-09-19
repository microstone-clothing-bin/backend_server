package com.example.clothing_backend.board;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wish")
@RequiredArgsConstructor
public class WishApiController {

    private final WishService wishService;

    @PostMapping("/add")
    public String addWish(@RequestParam Long userId, @RequestParam Long binId) {
        wishService.addWish(userId, binId);
        return "success";
    }

    @PostMapping("/remove")
    public String removeWish(@RequestParam Long userId, @RequestParam Long binId) {
        wishService.removeWish(userId, binId);
        return "success";
    }

    @GetMapping("/list")
    public List<Long> getUserWishes(@RequestParam Long userId) {
        return wishService.getUserWishes(userId).stream().map(Wish::getBinId).collect(Collectors.toList());
    }
}