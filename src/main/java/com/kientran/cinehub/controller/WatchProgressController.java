package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.UpdateWatchProgressRequest;
import com.kientran.cinehub.dto.response.WatchProgressResponse;
import com.kientran.cinehub.service.UserService;
import com.kientran.cinehub.service.WatchProgressService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watch-progress")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchProgressController {

    WatchProgressService watchProgressService;
    UserService userService;

    @GetMapping("/continue-watching")
    public ResponseEntity<Page<WatchProgressResponse>> getContinueWatching(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        String userId = userService.getUserResponseByEmail(authentication.getName()).getId();
        Pageable pageable = PageRequest.of(page, size);

        Page<WatchProgressResponse> continueWatching = watchProgressService.getContinueWatching(userId, pageable);
        return ResponseEntity.ok(continueWatching);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateWatchProgress(
            @Valid @RequestBody UpdateWatchProgressRequest request,
            Authentication authentication) {

        String userId = userService.getUserResponseByEmail(authentication.getName()).getId();
        WatchProgressResponse response = watchProgressService.updateWatchProgress(userId, request);

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok("Đã xem xong, nội dung đã được xóa khỏi danh sách tiếp tục xem");
        }
    }

    @DeleteMapping("/{contentId}")
    public ResponseEntity<String> removeFromContinueWatching(
            @PathVariable String contentId,
            Authentication authentication) {

        String userId = userService.getUserResponseByEmail(authentication.getName()).getId();
        watchProgressService.removeFromContinueWatching(userId, contentId);

        return ResponseEntity.ok("Đã xóa khỏi tiếp tục xem thành công");
    }
}