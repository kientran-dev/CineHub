package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.RatingRequest;
import com.kientran.cinehub.dto.response.RatingResponse;
import com.kientran.cinehub.service.RatingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingController {

    RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingResponse> rateMovie(
            @RequestBody RatingRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(ratingService.rateMovie(request, username));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<RatingResponse> getMyRating(
            @PathVariable Long movieId,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(ratingService.getMyRating(movieId, username));
    }
}