package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.response.MovieResponse;
import com.kientran.cinehub.service.RecommendationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationController {

    RecommendationService recommendationService;

    /**
     * Gợi ý phim cho user đang đăng nhập (Collaborative Filtering).
     * Nếu user mới (Cold Start) → trả phim phổ biến.
     */
    @GetMapping
    public ResponseEntity<List<MovieResponse>> getRecommendations(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(recommendationService.getRecommendationsForUser(username));
    }

    /**
     * Tìm phim tương tự cho một phim cụ thể (Item-Based CF).
     * API này public, không cần đăng nhập.
     */
    @GetMapping("/similar/{movieId}")
    public ResponseEntity<List<MovieResponse>> getSimilarMovies(@PathVariable Long movieId) {
        return ResponseEntity.ok(recommendationService.getSimilarMovies(movieId));
    }
}
