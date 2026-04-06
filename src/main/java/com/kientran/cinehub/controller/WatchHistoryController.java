package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.WatchHistoryRequest;
import com.kientran.cinehub.dto.response.WatchHistoryResponse;
import com.kientran.cinehub.service.WatchHistoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchHistoryController {

    WatchHistoryService watchHistoryService;

    @PostMapping
    public ResponseEntity<WatchHistoryResponse> saveHistory(
            @RequestBody WatchHistoryRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return new ResponseEntity<>(watchHistoryService.saveWatchHistory(request, username), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<WatchHistoryResponse>> getMyHistory(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(watchHistoryService.getMyWatchHistory(username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        watchHistoryService.deleteWatchHistory(id, username);
        return ResponseEntity.noContent().build();
    }
}