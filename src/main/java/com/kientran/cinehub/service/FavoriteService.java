package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.FavoriteRequest;
import com.kientran.cinehub.dto.response.FavoriteResponse;
import com.kientran.cinehub.entity.Favorite;
import com.kientran.cinehub.entity.Movie;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.repository.FavoriteRepository;
import com.kientran.cinehub.repository.MovieRepository;
import com.kientran.cinehub.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteService {

    FavoriteRepository favoriteRepository;
    MovieRepository movieRepository;
    UserRepository userRepository;

    @Transactional
    public FavoriteResponse addFavorite(FavoriteRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        if (favoriteRepository.existsByUserIdAndMovieId(user.getId(), movie.getId())) {
            throw new RuntimeException("Movie is already in favorites");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .movie(movie)
                .addedDate(LocalDateTime.now())
                .build();

        favorite = favoriteRepository.save(favorite);
        return mapToResponse(favorite);
    }

    public List<FavoriteResponse> getFavoritesByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return favoriteRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeFavorite(Long movieId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Favorite> favorites = favoriteRepository.findByUserId(user.getId());
        Favorite favoriteToRemove = favorites.stream()
                .filter(f -> f.getMovie().getId().equals(movieId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        favoriteRepository.deleteByUserIdAndMovieId(user.getId(), movieId);
    }

    private FavoriteResponse mapToResponse(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .movieId(favorite.getMovie().getId())
                .movieTitle(favorite.getMovie().getTitle())
                .movieThumbnail(favorite.getMovie().getThumbnail())
                .addedDate(favorite.getAddedDate())
                .build();
    }
}