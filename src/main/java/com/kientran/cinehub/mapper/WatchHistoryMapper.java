package com.kientran.cinehub.mapper;

import com.kientran.cinehub.dto.response.WatchHistoryResponse;
import com.kientran.cinehub.entity.WatchHistory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
@DecoratedWith(WatchHistoryMapperDecorator.class)
public interface WatchHistoryMapper {

    @Mapping(target = "contentTitle", ignore = true)
    @Mapping(target = "contentPosterPath", ignore = true)
    WatchHistoryResponse toResponse(WatchHistory watchHistory);

    List<WatchHistoryResponse> toResponseList(List<WatchHistory> watchHistories);
}
