package com.kientran.cinehub.mapper;

import com.kientran.cinehub.dto.response.WatchHistoryResponse;
import com.kientran.cinehub.entity.Content;
import com.kientran.cinehub.entity.WatchHistory;
import com.kientran.cinehub.repository.ContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public abstract class WatchHistoryMapperDecorator implements WatchHistoryMapper {

    @Autowired
    private WatchHistoryMapper delegate;

    @Autowired
    private ContentRepository contentRepository;

    @Override
    public WatchHistoryResponse toResponse(WatchHistory watchHistory) {
        WatchHistoryResponse response = delegate.toResponse(watchHistory);
        if (response != null) {
            // Lấy thông tin content dựa trên movieId
            Optional<Content> content = contentRepository.findById(watchHistory.getMovieId());
            if (content.isPresent()) {
                Content contentEntity = content.get();
                response.setContentTitle(contentEntity.getTitle());
                response.setContentPosterPath(contentEntity.getPosterPath());
            }
        }
        return response;
    }

    @Override
    public List<WatchHistoryResponse> toResponseList(List<WatchHistory> watchHistories) {
        List<WatchHistoryResponse> responses = delegate.toResponseList(watchHistories);
        if (responses != null) {
            responses.forEach(response -> {
                Optional<Content> content = contentRepository.findById(response.getMovieId());
                if (content.isPresent()) {
                    Content contentEntity = content.get();
                    response.setContentTitle(contentEntity.getTitle());
                    response.setContentPosterPath(contentEntity.getPosterPath());
                }
            });
        }
        return responses;
    }
}