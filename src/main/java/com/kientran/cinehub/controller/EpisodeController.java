package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.EpisodeRequest;
import com.kientran.cinehub.dto.response.EpisodeResponse;
import com.kientran.cinehub.service.EpisodeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/episodes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EpisodeController {

    EpisodeService episodeService;

    @PostMapping
    public ResponseEntity<EpisodeResponse> createEpisode(@RequestBody EpisodeRequest request) {
        return new ResponseEntity<>(episodeService.createEpisode(request), HttpStatus.CREATED);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<EpisodeResponse>> getEpisodesByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(episodeService.getEpisodesByMovie(movieId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpisodeResponse> getEpisodeById(@PathVariable Long id) {
        return ResponseEntity.ok(episodeService.getEpisodeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EpisodeResponse> updateEpisode(@PathVariable Long id, @RequestBody EpisodeRequest request) {
        return ResponseEntity.ok(episodeService.updateEpisode(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEpisode(@PathVariable Long id) {
        episodeService.deleteEpisode(id);
        return ResponseEntity.noContent().build();
    }
}