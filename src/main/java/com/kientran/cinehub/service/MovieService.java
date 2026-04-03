package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.MovieRequest;
import com.kientran.cinehub.dto.response.GenreResponse;
import com.kientran.cinehub.dto.response.MovieResponse;
import com.kientran.cinehub.entity.Genre;
import com.kientran.cinehub.entity.Movie;
import com.kientran.cinehub.repository.GenreRepository;
import com.kientran.cinehub.repository.MovieRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService{

    MovieRepository movieRepository;
    GenreRepository genreRepository;

    @Transactional
    public MovieResponse createMovie(MovieRequest requestDTO) {
        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(requestDTO.getGenreIds()));
        Movie movie = Movie.builder()
                .title(requestDTO.getTitle())
                .englishTitle(requestDTO.getEnglishTitle())
                .thumbnail(requestDTO.getThumbnail())
                .poster(requestDTO.getPoster())
                .director(requestDTO.getDirector())
                .releaseYear(requestDTO.getReleaseYear())
                .country(requestDTO.getCountry())
                .status(requestDTO.getStatus())
                .type(requestDTO.getType())
                .imdb(requestDTO.getImdb())
                .genres(genres) // Gán danh sách thể loại vào đây
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
    public MovieResponse updateMovie(Long id, MovieRequest requestDTO) {

        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(requestDTO.getGenreIds()));
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        
        movie.setTitle(requestDTO.getTitle());
        movie.setEnglishTitle(requestDTO.getEnglishTitle());
        movie.setThumbnail(requestDTO.getThumbnail());
        movie.setPoster(requestDTO.getPoster());
        movie.setDirector(requestDTO.getDirector());
        movie.setReleaseYear(requestDTO.getReleaseYear());
        movie.setCountry(requestDTO.getCountry());
        movie.setStatus(requestDTO.getStatus());
        movie.setType(requestDTO.getType());
        movie.setImdb(requestDTO.getImdb());
        movie.setGenres(genres);

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
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .englishTitle(movie.getEnglishTitle())
                .thumbnail(movie.getThumbnail())
                .poster(movie.getPoster())
                .director(movie.getDirector())
                .releaseYear(movie.getReleaseYear())
                .country(movie.getCountry())
                .status(movie.getStatus())
                .type(movie.getType())
                .imdb(movie.getImdb())
                .genres(movie.getGenres().stream()
                        .map(genre -> GenreResponse.builder()
                                .id(genre.getId())
                                .name(genre.getName())
                                .build())
                        .collect(Collectors.toSet()))                .build();
    }
}