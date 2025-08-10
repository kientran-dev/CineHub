package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.AddWatchHistoryRequest;
import com.kientran.cinehub.dto.response.WatchHistoryResponse;
import com.kientran.cinehub.service.WatchHistoryService;
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
@RequestMapping("/api/watch-history")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchHistoryController {

    WatchHistoryService watchHistoryService;

    @GetMapping
    public ResponseEntity<Page<WatchHistoryResponse>> getWatchHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        String userId = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);

        Page<WatchHistoryResponse> history = watchHistoryService.getWatchHistory(userId, pageable);
        return ResponseEntity.ok(history);
    }

    @PostMapping
    public ResponseEntity<WatchHistoryResponse> addToWatchHistory(
            @Valid @RequestBody AddWatchHistoryRequest request,
            Authentication authentication) {

        String userId = authentication.getName();
        WatchHistoryResponse response = watchHistoryService.addToWatchHistory(userId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<String> removeFromWatchHistory(
            @PathVariable String movieId,
            Authentication authentication) {

        String userId = authentication.getName();
        watchHistoryService.removeFromWatchHistory(userId, movieId);

        return ResponseEntity.ok("Đã xóa khỏi lịch sử xem thành công");
    }
}