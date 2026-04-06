package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.FavoriteRequest;
import com.kientran.cinehub.dto.response.FavoriteResponse;
import com.kientran.cinehub.service.FavoriteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteController {

    FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(
            @RequestBody FavoriteRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return new ResponseEntity<>(favoriteService.addFavorite(request, username), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getMyFavorites(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(favoriteService.getFavoritesByUser(username));
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long movieId,
            Authentication authentication) {
        String username = authentication.getName();
        favoriteService.removeFavorite(movieId, username);
        return ResponseEntity.noContent().build();
    }
}