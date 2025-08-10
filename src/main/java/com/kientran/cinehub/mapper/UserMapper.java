package com.kientran.cinehub.mapper;

import com.kientran.cinehub.dto.response.UserResponse;
import com.kientran.cinehub.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "avatarUrl", ignore = true) // Xử lý trong service
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}