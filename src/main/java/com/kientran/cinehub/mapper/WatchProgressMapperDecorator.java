package com.kientran.cinehub.mapper;

import com.kientran.cinehub.dto.response.WatchProgressResponse;
import com.kientran.cinehub.entity.Content;
import com.kientran.cinehub.entity.Episode;
import com.kientran.cinehub.entity.WatchProgress;
import com.kientran.cinehub.repository.ContentRepository;
import com.kientran.cinehub.repository.EpisodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public abstract class WatchProgressMapperDecorator implements WatchProgressMapper {

    @Autowired
    private WatchProgressMapper delegate;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Override
    public WatchProgressResponse toResponse(WatchProgress watchProgress) {
        WatchProgressResponse response = delegate.toResponse(watchProgress);
        if (response != null) {
            // Lấy thông tin content
            if (watchProgress.getContentId() != null) {
                Optional<Content> content = contentRepository.findById(watchProgress.getContentId());
                if (content.isPresent()) {
                    Content contentEntity = content.get();
                    response.setContentTitle(contentEntity.getTitle());
                    response.setContentPosterPath(contentEntity.getPosterPath());
                }
            }

            // Lấy thông tin episode nếu có
            if (watchProgress.getEpisodeId() != null) {
                Optional<Episode> episode = episodeRepository.findById(watchProgress.getEpisodeId());
                if (episode.isPresent()) {
                    response.setEpisodeName(episode.get().getName());
                }
            }
        }
        return response;
    }

    @Override
    public List<WatchProgressResponse> toResponseList(List<WatchProgress> watchProgresses) {
        List<WatchProgressResponse> responses = delegate.toResponseList(watchProgresses);
        if (responses != null) {
            responses.forEach(response -> {
                Optional<WatchProgress> originalProgress = watchProgresses.stream()
                        .filter(wp -> wp.getId().equals(response.getId()))
                        .findFirst();
                if (originalProgress.isPresent()) {
                    WatchProgress progress = originalProgress.get();

                    // Lấy thông tin content
                    if (progress.getContentId() != null) {
                        Optional<Content> content = contentRepository.findById(progress.getContentId());
                        if (content.isPresent()) {
                            Content contentEntity = content.get();
                            response.setContentTitle(contentEntity.getTitle());
                            response.setContentPosterPath(contentEntity.getPosterPath());
                        }
                    }

                    // Lấy thông tin episode nếu có
                    if (progress.getEpisodeId() != null) {
                        Optional<Episode> episode = episodeRepository.findById(progress.getEpisodeId());
                        if (episode.isPresent()) {
                            response.setEpisodeName(episode.get().getName());
                        }
                    }
                }
            });
        }
        return responses;
    }
}