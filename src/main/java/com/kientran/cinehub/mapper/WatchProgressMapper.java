package com.kientran.cinehub.mapper;

import com.kientran.cinehub.dto.response.WatchProgressResponse;
import com.kientran.cinehub.entity.WatchProgress;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
@DecoratedWith(WatchProgressMapperDecorator.class)
public interface WatchProgressMapper {

    @Mapping(target = "contentTitle", ignore = true)
    @Mapping(target = "contentPosterPath", ignore = true)
    @Mapping(target = "episodeName", ignore = true)
    WatchProgressResponse toResponse(WatchProgress watchProgress);

    List<WatchProgressResponse> toResponseList(List<WatchProgress> watchProgresses);
}