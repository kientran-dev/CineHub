package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.EpisodeVersionRequest;
import com.kientran.cinehub.dto.response.EpisodeVersionResponse;
import com.kientran.cinehub.entity.Episode;
import com.kientran.cinehub.entity.EpisodeVersion;
import com.kientran.cinehub.repository.EpisodeRepository;
import com.kientran.cinehub.repository.EpisodeVersionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EpisodeVersionService {

    EpisodeVersionRepository episodeVersionRepository;
    EpisodeRepository episodeRepository;

    public EpisodeVersionResponse createEpisodeVersion(EpisodeVersionRequest request) {
        Episode episode = episodeRepository.findById(request.getEpisodeId())
                .orElseThrow(() -> new RuntimeException("Episode not found"));

        EpisodeVersion version = EpisodeVersion.builder()
                .episode(episode)
                .videoUrl(request.getVideoUrl())
                .type(request.getType())
                .build();

        version = episodeVersionRepository.save(version);
        return mapToResponse(version);
    }

    public List<EpisodeVersionResponse> getVersionsByEpisode(Long episodeId) {
        if (!episodeRepository.existsById(episodeId)) {
            throw new RuntimeException("Episode not found");
        }
        return episodeVersionRepository.findByEpisodeId(episodeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EpisodeVersionResponse updateEpisodeVersion(Long id, EpisodeVersionRequest request) {
        EpisodeVersion version = episodeVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EpisodeVersion not found"));

        version.setVideoUrl(request.getVideoUrl());
        version.setType(request.getType());

        version = episodeVersionRepository.save(version);
        return mapToResponse(version);
    }

    public void deleteEpisodeVersion(Long id) {
        if (!episodeVersionRepository.existsById(id)) {
            throw new RuntimeException("EpisodeVersion not found");
        }
        episodeVersionRepository.deleteById(id);
    }

    private EpisodeVersionResponse mapToResponse(EpisodeVersion version) {
        return EpisodeVersionResponse.builder()
                .id(version.getId())
                .episodeId(version.getEpisode().getId())
                .videoUrl(version.getVideoUrl())
                .type(version.getType())
                .build();
    }
}
