package com.kientran.cinehub.mapper;

import com.kientran.cinehub.dto.response.UserResponse;
import com.kientran.cinehub.entity.User;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

    /**
     * Chuyển đổi User entity sang UserResponse DTO
     */
    @Mapping(target = "avatarId", source = "avatarId")
    @Mapping(target = "avatarUrl", ignore = true) // Sẽ được set trong decorator
    UserResponse toResponse(User user);

    /**
     * Chuyển đổi danh sách User sang danh sách UserResponse
     */
    List<UserResponse> toResponseList(List<User> users);
}