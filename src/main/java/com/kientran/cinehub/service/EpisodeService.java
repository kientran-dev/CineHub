package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.EpisodeRequest;
import com.kientran.cinehub.dto.response.EpisodeResponse;
import com.kientran.cinehub.dto.response.EpisodeVersionResponse;
import com.kientran.cinehub.entity.Episode;
import com.kientran.cinehub.entity.Movie;
import com.kientran.cinehub.repository.EpisodeRepository;
import com.kientran.cinehub.repository.MovieRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EpisodeService {

    EpisodeRepository episodeRepository;
    MovieRepository movieRepository;

    public EpisodeResponse createEpisode(EpisodeRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Episode episode = Episode.builder()
                .movie(movie)
                .episodeNumber(request.getEpisodeNumber())
                .episodeName(request.getEpisodeName())
                .build();

        episode = episodeRepository.save(episode);
        return mapToResponse(episode);
    }

    public List<EpisodeResponse> getEpisodesByMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new RuntimeException("Movie not found");
        }
        return episodeRepository.findByMovieId(movieId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EpisodeResponse getEpisodeById(Long id) {
        Episode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        return mapToResponse(episode);
    }

    public EpisodeResponse updateEpisode(Long id, EpisodeRequest request) {
        Episode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Episode not found"));

        episode.setEpisodeNumber(request.getEpisodeNumber());
        episode.setEpisodeName(request.getEpisodeName());

        episode = episodeRepository.save(episode);
        return mapToResponse(episode);
    }

    public void deleteEpisode(Long id) {
        if (!episodeRepository.existsById(id)) {
            throw new RuntimeException("Episode not found");
        }
        episodeRepository.deleteById(id);
    }

    private EpisodeResponse mapToResponse(Episode episode) {
        List<EpisodeVersionResponse> versions = episode.getEpisodeVersions() == null
                ? Collections.emptyList()
                : episode.getEpisodeVersions().stream()
                    .map(v -> EpisodeVersionResponse.builder()
                            .id(v.getId())
                            .episodeId(episode.getId())
                            .videoUrl(v.getVideoUrl())
                            .type(v.getType())
                            .build())
                    .collect(Collectors.toList());

        return EpisodeResponse.builder()
                .id(episode.getId())
                .movieId(episode.getMovie().getId())
                .episodeNumber(episode.getEpisodeNumber())
                .episodeName(episode.getEpisodeName())
                .episodeVersions(versions)
                .build();
    }
}