package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.MovieRequest;
import com.kientran.cinehub.dto.response.ActorResponse;
import com.kientran.cinehub.dto.response.EpisodeResponse;
import com.kientran.cinehub.dto.response.EpisodeVersionResponse;
import com.kientran.cinehub.dto.response.GenreResponse;
import com.kientran.cinehub.dto.response.MovieResponse;
import com.kientran.cinehub.entity.Movie;
import com.kientran.cinehub.repository.MovieRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService {

    MovieRepository movieRepository;

    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .englishTitle(request.getEnglishTitle())
                .thumbnail(request.getThumbnail())
                .poster(request.getPoster())
                .director(request.getDirector())
                .releaseYear(request.getReleaseYear())
                .country(request.getCountry())
                .status(request.getStatus())
                .type(request.getType())
                .imdb(request.getImdbScore())
                .build();

        movie = movieRepository.save(movie);
        return mapToResponse(movie);
    }

    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        return mapToResponse(movie);
    }

    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        movie.setTitle(request.getTitle());
        movie.setEnglishTitle(request.getEnglishTitle());
        movie.setThumbnail(request.getThumbnail());
        movie.setPoster(request.getPoster());
        movie.setDirector(request.getDirector());
        movie.setReleaseYear(request.getReleaseYear());
        movie.setCountry(request.getCountry());
        movie.setStatus(request.getStatus());
        movie.setType(request.getType());
        movie.setImdb(request.getImdbScore());

        movie = movieRepository.save(movie);
        return mapToResponse(movie);
    }

    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found");
        }
        movieRepository.deleteById(id);
    }

    private MovieResponse mapToResponse(Movie movie) {
        // Tính average rating
        double avgRating = 0;
        int totalRatings = 0;
        if (movie.getRatings() != null && !movie.getRatings().isEmpty()) {
            totalRatings = movie.getRatings().size();
            avgRating = movie.getRatings().stream()
                    .mapToDouble(r -> r.getScore() != null ? r.getScore() : 0)
                    .average()
                    .orElse(0);
        }

        // Map genres
        java.util.Set<GenreResponse> genres = null;
        if (movie.getGenres() != null) {
            genres = movie.getGenres().stream()
                    .map(g -> GenreResponse.builder()
                            .id(g.getId())
                            .name(g.getName())
                            .build())
                    .collect(java.util.stream.Collectors.toSet());
        }

        // Map episodes
        List<EpisodeResponse> episodes = null;
        if (movie.getEpisodes() != null) {
            episodes = movie.getEpisodes().stream()
                    .map(e -> {
                        List<EpisodeVersionResponse> versions = e.getEpisodeVersions() == null
                                ? Collections.emptyList()
                                : e.getEpisodeVersions().stream()
                                    .map(v -> EpisodeVersionResponse.builder()
                                            .id(v.getId())
                                            .episodeId(e.getId())
                                            .videoUrl(v.getVideoUrl())
                                            .type(v.getType())
                                            .build())
                                    .collect(Collectors.toList());
                        return EpisodeResponse.builder()
                                .id(e.getId())
                                .movieId(movie.getId())
                                .episodeNumber(e.getEpisodeNumber())
                                .episodeName(e.getEpisodeName())
                                .episodeVersions(versions)
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        // Map actors
        List<ActorResponse> actors = null;
        if (movie.getActors() != null) {
            actors = movie.getActors().stream()
                    .map(a -> ActorResponse.builder()
                            .id(a.getId())
                            .fullName(a.getFullName())
                            .imageUrl(a.getImageUrl())
                            .build())
                    .collect(Collectors.toList());
        }

        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .englishTitle(movie.getEnglishTitle())
                .thumbnail(movie.getThumbnail())
                .poster(movie.getPoster())
                .description(movie.getDescription())
                .director(movie.getDirector())
                .releaseYear(movie.getReleaseYear())
                .duration(movie.getDuration())
                .country(movie.getCountry())
                .status(movie.getStatus())
                .type(movie.getType())
                .imdbScore(movie.getImdb())
                .averageRating(avgRating)
                .totalRatings(totalRatings)
                .genres(genres)
                .episodes(episodes)
                .actors(actors)
                .build();
    }
}