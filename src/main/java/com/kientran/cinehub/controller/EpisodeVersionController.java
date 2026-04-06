package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.EpisodeVersionRequest;
import com.kientran.cinehub.dto.response.EpisodeVersionResponse;
import com.kientran.cinehub.service.EpisodeVersionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/episode-versions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EpisodeVersionController {

    EpisodeVersionService episodeVersionService;

    @PostMapping
    public ResponseEntity<EpisodeVersionResponse> createEpisodeVersion(@RequestBody EpisodeVersionRequest request) {
        return new ResponseEntity<>(episodeVersionService.createEpisodeVersion(request), HttpStatus.CREATED);
    }

    @GetMapping("/episode/{episodeId}")
    public ResponseEntity<List<EpisodeVersionResponse>> getVersionsByEpisode(@PathVariable Long episodeId) {
        return ResponseEntity.ok(episodeVersionService.getVersionsByEpisode(episodeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EpisodeVersionResponse> updateEpisodeVersion(
            @PathVariable Long id,
            @RequestBody EpisodeVersionRequest request) {
        return ResponseEntity.ok(episodeVersionService.updateEpisodeVersion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEpisodeVersion(@PathVariable Long id) {
        episodeVersionService.deleteEpisodeVersion(id);
        return ResponseEntity.noContent().build();
    }
}
