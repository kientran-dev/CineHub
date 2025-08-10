package com.kientran.cinehub.mapper;

import com.kientran.cinehub.dto.response.FavoriteResponse;
import com.kientran.cinehub.entity.Favorite;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
@DecoratedWith(FavoriteMapperDecorator.class)
public interface FavoriteMapper {

    @Mapping(target = "contentTitle", ignore = true)
    @Mapping(target = "contentPosterPath", ignore = true)
    @Mapping(target = "contentOverview", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    FavoriteResponse toResponse(Favorite favorite);

    List<FavoriteResponse> toResponseList(List<Favorite> favorites);
}