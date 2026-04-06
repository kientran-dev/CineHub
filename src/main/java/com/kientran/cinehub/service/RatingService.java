package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.RatingRequest;
import com.kientran.cinehub.dto.response.RatingResponse;
import com.kientran.cinehub.entity.Movie;
import com.kientran.cinehub.entity.Rating;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.repository.MovieRepository;
import com.kientran.cinehub.repository.RatingRepository;
import com.kientran.cinehub.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingService {

    RatingRepository ratingRepository;
    MovieRepository movieRepository;
    UserRepository userRepository;

    @Transactional
    public RatingResponse rateMovie(RatingRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Rating rating = ratingRepository.findByUserIdAndMovieId(user.getId(), movie.getId())
                .orElse(Rating.builder()
                        .user(user)
                        .movie(movie)
                        .build());

        rating.setScore(request.getScore());
        rating.setRatingDate(LocalDateTime.now());

        rating = ratingRepository.save(rating);
        return mapToResponse(rating);
    }

    public RatingResponse getMyRating(Long movieId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rating rating = ratingRepository.findByUserIdAndMovieId(user.getId(), movieId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        return mapToResponse(rating);
    }

    private RatingResponse mapToResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .movieId(rating.getMovie().getId())
                .username(rating.getUser().getUsername())
                .score(rating.getScore())
                .ratingDate(rating.getRatingDate())
                .build();
    }
}