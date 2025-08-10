package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.AddToFavoriteRequest;
import com.kientran.cinehub.dto.response.FavoriteResponse;
import com.kientran.cinehub.service.FavoriteService;
import com.kientran.cinehub.service.UserService;
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
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteController {

    FavoriteService favoriteService;
    UserService userService;

    @GetMapping
    public ResponseEntity<Page<FavoriteResponse>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        String userId = userService.getUserResponseByEmail(authentication.getName()).getId();
        Pageable pageable = PageRequest.of(page, size);

        Page<FavoriteResponse> favorites = favoriteService.getFavorites(userId, pageable);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping
    public ResponseEntity<FavoriteResponse> addToFavorite(
            @Valid @RequestBody AddToFavoriteRequest request,
            Authentication authentication) {

        String userId = userService.getUserResponseByEmail(authentication.getName()).getId();
        FavoriteResponse response = favoriteService.addToFavorite(userId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{contentId}")
    public ResponseEntity<String> removeFromFavorite(
            @PathVariable String contentId,
            Authentication authentication) {

        String userId = userService.getUserResponseByEmail(authentication.getName()).getId();
        favoriteService.removeFromFavorite(userId, contentId);

        return ResponseEntity.ok("Đã xóa khỏi danh sách yêu thích thành công");
    }

    @GetMapping("/{contentId}/check")
    public ResponseEntity<Boolean> checkIsFavorite(
            @PathVariable String contentId,
            Authentication authentication) {

        String userId = userService.getUserResponseByEmail(authentication.getName()).getId();
        boolean isFavorite = favoriteService.isFavorite(userId, contentId);

        return ResponseEntity.ok(isFavorite);
    }
}