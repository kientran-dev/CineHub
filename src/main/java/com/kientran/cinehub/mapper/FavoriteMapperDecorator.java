package com.kientran.cinehub.mapper;

import com.kientran.cinehub.dto.response.FavoriteResponse;
import com.kientran.cinehub.entity.Content;
import com.kientran.cinehub.entity.Favorite;
import com.kientran.cinehub.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public abstract class FavoriteMapperDecorator implements FavoriteMapper {

    @Autowired
    private FavoriteMapper delegate;

    @Autowired
    private ContentRepository contentRepository;

    @Override
    public FavoriteResponse toResponse(Favorite favorite) {
        FavoriteResponse response = delegate.toResponse(favorite);
        if (response != null && favorite.getContentId() != null) {
            Optional<Content> content = contentRepository.findById(favorite.getContentId());
            if (content.isPresent()) {
                Content contentEntity = content.get();
                response.setContentTitle(contentEntity.getTitle());
                response.setContentPosterPath(contentEntity.getPosterPath());
                response.setContentOverview(contentEntity.getOverview());
                response.setAverageRating(contentEntity.getAverageRating());
                response.setRatingCount(contentEntity.getRatingCount());
            }
        }
        return response;
    }

    @Override
    public List<FavoriteResponse> toResponseList(List<Favorite> favorites) {
        List<FavoriteResponse> responses = delegate.toResponseList(favorites);
        if (responses != null) {
            responses.forEach(response -> {
                Optional<Favorite> originalFavorite = favorites.stream()
                        .filter(f -> f.getId().equals(response.getId()))
                        .findFirst();
                if (originalFavorite.isPresent() && originalFavorite.get().getContentId() != null) {
                    Optional<Content> content = contentRepository.findById(originalFavorite.get().getContentId());
                    if (content.isPresent()) {
                        Content contentEntity = content.get();
                        response.setContentTitle(contentEntity.getTitle());
                        response.setContentPosterPath(contentEntity.getPosterPath());
                        response.setContentOverview(contentEntity.getOverview());
                        response.setAverageRating(contentEntity.getAverageRating());
                        response.setRatingCount(contentEntity.getRatingCount());
                    }
                }
            });
        }
        return responses;
    }
}