package com.kientran.cinehub.mapper;

import com.kientran.cinehub.dto.response.UserResponse;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.service.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class UserMapperDecorator implements UserMapper {

    @Autowired
    private UserMapper delegate;

    @Autowired
    private AvatarService avatarService;

    @Override
    public UserResponse toResponse(User user) {
        UserResponse response = delegate.toResponse(user);
        if (response != null) {
            // Set avatarUrl bằng AvatarService
            response.setAvatarUrl(avatarService.getAvatarUrl(response.getAvatarId()));
        }
        return response;
    }

    @Override
    public List<UserResponse> toResponseList(List<User> users) {
        List<UserResponse> responses = delegate.toResponseList(users);
        if (responses != null) {
            responses.forEach(response ->
                    response.setAvatarUrl(avatarService.getAvatarUrl(response.getAvatarId()))
            );
        }
        return responses;
    }
}